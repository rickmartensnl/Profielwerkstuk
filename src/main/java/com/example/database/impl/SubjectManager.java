package com.example.database.impl;

import com.example.ProfielwerkstukServerLauncher;
import com.example.database.Model;
import com.example.exceptions.DatabaseOfflineException;
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

public class SubjectManager {

    @Getter private static SubjectManager subjectManager;

    public SubjectManager() {
        subjectManager = this;
    }

    public Subject getSubject(UUID uuid) throws DatabaseOfflineException {
        try {
            return new Subject(uuid);
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }

    public ArrayList<Subject> getAllSubjects() throws DatabaseOfflineException {
        ArrayList<Subject> subjects = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT `uuid` FROM `subjects`;");
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                subjects.add(new Subject(UUID.fromString(resultSet.getString("uuid"))));
            }

        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }

        return subjects;
    }

    public class Subject implements Model {

        @Expose @Getter private final UUID uuid;
        @Expose @Getter private String name;
        @Expose @Getter private int flags;
        @Expose @Getter private UserManager.User creator;

        public Subject(UUID uuid) throws SQLException, DatabaseOfflineException {
            this.uuid = uuid;

            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT * FROM `subjects` WHERE uuid = ?;");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.name = resultSet.getString("name");
                this.flags = resultSet.getInt("flags");
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
