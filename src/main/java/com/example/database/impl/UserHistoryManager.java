package com.example.database.impl;

import com.example.ProfielwerkstukServerLauncher;
import com.example.database.Model;
import com.example.exceptions.DatabaseOfflineException;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserHistoryManager {

    @Getter private static UserHistoryManager userHistoryManager;

    public UserHistoryManager() {
        userHistoryManager = this;
    }

    public UserHistory createNewUserHistory(UserManager.User user) throws DatabaseOfflineException {
        try {
            return new UserHistory(user, null, null, null, UUID.fromString("8630feb6-1bde-11ec-9e40-2e399d554045"));
        } catch (SQLException exception) {
            throw new DatabaseOfflineException();
        }
    }

    public UserHistory getUnfinished(UserManager.User user) throws DatabaseOfflineException {
        try {
            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT uuid FROM `users_play_history` WHERE flags = ? AND user_uuid = ?;");
            preparedStatement.setInt(1, 1);
            preparedStatement.setString(2, user.getUuid().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return new UserHistory(UUID.fromString(resultSet.getString("uuid")));
            }

            return null;
        } catch (SQLException exception) {
            throw new DatabaseOfflineException();
        }
    }

    public static class UserHistory implements Model {

        @Expose @Getter private final UUID uuid;
        @Getter @Setter private String answer;
        @Getter @Setter private int flags;
        @Getter @Setter private double correctPercentage;
        @Getter @Setter private QuestionManager.QuestionVariable[] variableValues;
        @Getter @Setter private QuestionManager.Question question;
        @Getter @Setter private UserManager.User user;

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
                this.variableValues = new Gson().fromJson(resultSet.getString("variableValues"), QuestionManager.QuestionVariable[].class);
                this.user = UserManager.getUserManager().getUser(UUID.fromString(resultSet.getString("user_uuid")));
            }
        }

        public UserHistory(UserManager.User user, @Nullable UUID subjectId, @Nullable UUID chapterId, @Nullable UUID paragraphId, @Nullable UUID questionId) throws SQLException {
            this.uuid = UUID.randomUUID();
            this.answer = null;
            this.flags = 1;
            this.correctPercentage = -1D;
            this.variableValues = null;
            this.user = user;

            if (questionId != null) {
                PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT * FROM `questions` WHERE uuid = ?;");
                preparedStatement.setString(1, questionId.toString());
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    this.question = QuestionManager.getQuestionManager().getQuestion(UUID.fromString(resultSet.getString("uuid")));
                }
            }

            try {
                PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("INSERT INTO `users_play_history` (uuid, answer, flags, correctPercentage, variableValues, question_uuid, user_uuid) VALUES (?, ?, ?, ?, ?, ?, ?);");
                preparedStatement.setString(1, this.uuid.toString());
                preparedStatement.setString(2, new Gson().toJson(this.answer));
                preparedStatement.setInt(3, this.flags);
                preparedStatement.setNull(4, 3);
                preparedStatement.setString(5, new Gson().toJson(this.variableValues));
                preparedStatement.setString(6, this.question.toString());
                preparedStatement.setString(7, this.user.getUuid().toString());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getJsonObject() {
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .create();
            return gson.toJson(this);
        }

    }

}
