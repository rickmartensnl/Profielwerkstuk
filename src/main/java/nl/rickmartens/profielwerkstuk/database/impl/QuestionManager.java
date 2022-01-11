package nl.rickmartens.profielwerkstuk.database.impl;

import nl.rickmartens.profielwerkstuk.ProfielwerkstukServerLauncher;
import nl.rickmartens.profielwerkstuk.database.Model;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import io.sentry.Sentry;
import lombok.Getter;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class QuestionManager {

    @Getter private static QuestionManager questionManager;

    public QuestionManager() {
        questionManager = this;
    }

    public Question getQuestion(UUID uuid) throws DatabaseOfflineException {
        try {
            return new Question(uuid);
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }
	
	public static class Question implements Model {

        @Expose @Getter private final UUID uuid;
        @Expose @Getter @Setter private String question;
        @Expose @Getter @Setter private String information;
        @Expose @Getter @Setter private int flags;
        @Expose @Getter @Setter private QuestionAnswer answer;
        @Expose @Getter @Setter private Map<String, QuestionVariable> variables;
        @Getter @Setter private QuestionVariable tips;
        @Expose @Getter @Setter private UserManager.User creator;

        public Question(UUID uuid) throws SQLException, DatabaseOfflineException {
            this.uuid = uuid;

            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT * FROM `questions` WHERE uuid = ?;");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.question = resultSet.getString("question");
                this.information = resultSet.getString("information");
                this.flags = resultSet.getInt("flags");
                this.answer = new Gson().fromJson(resultSet.getString("answer"), QuestionManager.QuestionAnswer.class);
                java.lang.reflect.Type type = new TypeToken<Map<String, QuestionVariable>>() {}.getType();
                this.variables = new Gson().fromJson(resultSet.getString("variables"), type);
                this.creator = UserManager.getUserManager().getUser(UUID.fromString(resultSet.getString("creator_uuid")));
            }
        }

        @Override
        public String getJsonObject() {
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            return gson.toJson(this);
        }

    }

    public static class QuestionAnswer {

        @Getter private final Type type;
        @Getter @Setter private String unit;
        @Getter @Setter private String calculation;

        public QuestionAnswer(Type type, String calculation) {
            this.type = type;
            this.calculation = calculation;
        }

    }

    public enum Type {

        STRING(String.class),
        INT(Integer.class);

        @Getter private final Class clazz;

        Type(Class clazz) {
            this.clazz = clazz;
        }

    }

    public static class QuestionVariable {

        @Expose @Getter private final Type type;
        @Getter @Setter private Object value;
        @Expose @Getter @Setter private String unit;
        @Expose @Getter @Setter private String theValue;
        @Getter @Setter private String depends;

        public QuestionVariable(Type type) {
            this.type = type;
        }

    }

}
