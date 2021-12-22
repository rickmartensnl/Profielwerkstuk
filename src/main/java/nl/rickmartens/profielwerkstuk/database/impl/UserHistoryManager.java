package nl.rickmartens.profielwerkstuk.database.impl;

import nl.rickmartens.profielwerkstuk.ProfielwerkstukServerLauncher;
import nl.rickmartens.profielwerkstuk.database.Model;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import nl.rickmartens.profielwerkstuk.utils.QuestionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import io.sentry.Sentry;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserHistoryManager {

    @Getter private static UserHistoryManager userHistoryManager;

    public UserHistoryManager() {
        userHistoryManager = this;
    }

    public UserHistory createNewUserHistory(UserManager.User user) throws DatabaseOfflineException {
        try {
            return new UserHistory(user, null, null, null, UUID.fromString("f928f9eb-4c51-11ec-9265-2e399d554045"));
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }

    public UserHistory[] getUnfinished(UserManager.User user) throws DatabaseOfflineException {
        try {
            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT uuid FROM `users_play_history` WHERE flags = ? AND user_uuid = ?;");
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, user.getUuid().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            List<UserHistory> returnable = new ArrayList<>();

            while (resultSet.next()) {
                returnable.add(new UserHistory(UUID.fromString(resultSet.getString("uuid"))));
            }

            return returnable.toArray(new UserHistory[0]);
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }
    
    public UserHistory getSessionByUuid(UUID uuid) throws DatabaseOfflineException {
        try {
            return new UserHistory(uuid);
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }

    public static class UserHistory implements Model {

        @Expose @Getter private final UUID uuid;
        @Expose @Getter @Setter private String answer;
        @Expose @Getter @Setter private int flags;
        @Expose @Getter @Setter private double correctPercentage;
        @Expose @Getter @Setter private Map<String, QuestionManager.QuestionVariable> variableValues;
        @Expose @Getter @Setter private QuestionManager.Question question;
        @Expose @Getter @Setter private UserManager.User user;

        public UserHistory(UUID uuid) throws SQLException, DatabaseOfflineException {
            this.uuid = uuid;

            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT * FROM `users_play_history` WHERE uuid = ?;");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.answer = null;
                this.flags = resultSet.getInt("flags");
                this.correctPercentage = resultSet.getDouble("correctPercentage");
                this.question = QuestionManager.getQuestionManager().getQuestion(UUID.fromString(resultSet.getString("question_uuid")));
                java.lang.reflect.Type type = new TypeToken<Map<String, QuestionManager.QuestionVariable>>() {}.getType();
                this.variableValues = new Gson().fromJson(resultSet.getString("variableValues"), type);
                this.user = UserManager.getUserManager().getUser(UUID.fromString(resultSet.getString("user_uuid")));
            }
        }

        public UserHistory(UserManager.User user, @Nullable UUID subjectId, @Nullable UUID chapterId, @Nullable UUID paragraphId, @Nullable UUID questionId) throws SQLException, DatabaseOfflineException {
            this.uuid = UUID.randomUUID();
            this.answer = null;
            this.flags = 1;
            this.correctPercentage = -1D;
            this.variableValues = new HashMap<>();
            this.user = user;

            if (questionId != null) {
                PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT * FROM `questions` WHERE uuid = ?;");
                preparedStatement.setString(1, questionId.toString());
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    this.question = QuestionManager.getQuestionManager().getQuestion(UUID.fromString(resultSet.getString("uuid")));

                    for (Map.Entry<String, QuestionManager.QuestionVariable> entry : this.question.getVariables().entrySet()) {
                        QuestionManager.QuestionVariable var = new QuestionManager.QuestionVariable(entry.getValue().getType());
                        if (entry.getValue().getDepends() != null) {
                            var.setTheValue(String.valueOf(QuestionUtil.generateFromQuestionFormat(entry.getValue(), this.variableValues.get(entry.getValue().getDepends()))));
                        } else {
                            var.setTheValue(String.valueOf(QuestionUtil.generateFromQuestionFormat(entry.getValue())));
                        }
                        var.setUnit(entry.getValue().getUnit());
                        var.setDepends(entry.getValue().getDepends());
                        this.variableValues.put(entry.getKey(), var);
                    }
                }
            } else if (paragraphId != null) {
                PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT * FROM `questions` WHERE paragraph_uuid = ?;");
                preparedStatement.setString(1, paragraphId.toString());
                ResultSet resultSet = preparedStatement.executeQuery();

                ArrayList<QuestionManager.Question> questionArray = new ArrayList<>();

                while (resultSet.next()) {
                    questionArray.add(QuestionManager.getQuestionManager().getQuestion(UUID.fromString(resultSet.getString("uuid"))));
                }

                this.question = questionArray.get(new Random().nextInt(questionArray.size()));

                for (Map.Entry<String, QuestionManager.QuestionVariable> entry : this.question.getVariables().entrySet()) {
                    QuestionManager.QuestionVariable var = new QuestionManager.QuestionVariable(entry.getValue().getType());
                    if (entry.getValue().getDepends() != null) {
                        var.setTheValue(String.valueOf(QuestionUtil.generateFromQuestionFormat(entry.getValue(), this.variableValues.get(entry.getValue().getDepends()))));
                    } else {
                        var.setTheValue(String.valueOf(QuestionUtil.generateFromQuestionFormat(entry.getValue())));
                    }
                    var.setUnit(entry.getValue().getUnit());
                    var.setDepends(entry.getValue().getDepends());
                    this.variableValues.put(entry.getKey(), var);
                }
            }

            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("INSERT INTO `users_play_history` (uuid, answer, flags, correctPercentage, variableValues, question_uuid, user_uuid) VALUES (?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setString(1, this.uuid.toString());
            preparedStatement.setString(2, new Gson().toJson(this.answer));
            preparedStatement.setInt(3, this.flags);
            preparedStatement.setNull(4, 3);
            preparedStatement.setString(5, new Gson().toJson(this.variableValues));
            preparedStatement.setString(6, this.question.getUuid().toString());
            preparedStatement.setString(7, this.user.getUuid().toString());
            preparedStatement.executeUpdate();
        }
        
        public void save() throws DatabaseOfflineException {
            try {
                PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("UPDATE `users_play_history` SET answer = ?, flags = ?, correctPercentage = ? WHERE uuid = ?;");
                preparedStatement.setString(1, this.answer);
                preparedStatement.setInt(2, this.flags);
                preparedStatement.setDouble(3, this.correctPercentage);
                preparedStatement.setString(4, this.uuid.toString());
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                Sentry.captureException(exception);
                throw new DatabaseOfflineException();
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

}
