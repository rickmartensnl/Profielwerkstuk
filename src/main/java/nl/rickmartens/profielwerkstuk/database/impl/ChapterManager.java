package nl.rickmartens.profielwerkstuk.database.impl;

import nl.rickmartens.profielwerkstuk.ProfielwerkstukServerLauncher;
import nl.rickmartens.profielwerkstuk.database.Model;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import io.sentry.Sentry;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class ChapterManager {

    @Getter private static ChapterManager chapterManager;

    public ChapterManager() {
        chapterManager = this;
    }

    public Chapter getChapter(UUID uuid) throws DatabaseOfflineException {
        try {
            return new Chapter(uuid);
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }

    public ArrayList<Chapter> getAllChaptersBySubject(UUID subjectUuid) throws DatabaseOfflineException {
        ArrayList<Chapter> chapters = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT `uuid` FROM `chapters` WHERE `subject_uuid` = ?;");
            preparedStatement.setString(1, subjectUuid.toString());
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                chapters.add(new Chapter(UUID.fromString(resultSet.getString("uuid"))));
            }
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }

        return chapters;
    }

    public class Chapter implements Model {

        @Expose @Getter private final UUID uuid;
        @Expose @Getter private String name;
        @Expose @Getter private int flags;
        @Expose @Getter private SubjectManager.Subject subject;
        @Expose @Getter private UserManager.User creator;

        public Chapter(UUID uuid) throws SQLException, DatabaseOfflineException {
            this.uuid = uuid;

            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT * FROM `chapters` WHERE uuid = ?;");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.name = resultSet.getString("name");
                this.flags = resultSet.getInt("flags");
                this.subject = SubjectManager.getSubjectManager().getSubject(UUID.fromString(resultSet.getString("subject_uuid")));
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

}
