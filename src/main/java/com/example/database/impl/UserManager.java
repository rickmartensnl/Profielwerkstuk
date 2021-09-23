package com.example.database.impl;

import com.example.ProfielwerkstukServerLauncher;
import com.example.database.Model;
import com.example.exceptions.DatabaseOfflineException;
import com.example.utils.AuthenticationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    @Getter private static UserManager userManager;

    public Map<UUID, User> userMap;

    public UserManager() {
        userManager = this;
        userMap = new HashMap<>();
    }

    public User getUser(UUID uuid) throws DatabaseOfflineException {
        if (userMap.get(uuid) != null) {
            userMap.get(uuid).setCached(true);
            return userMap.get(uuid);
        }

        try {
            User user = new User(uuid);
            userMap.put(uuid, user);

            return user;
        } catch (SQLException exception) {
            throw new DatabaseOfflineException();
        }
    }

    public static class User implements Model {

        @Expose @Getter private final UUID uuid;
        @Expose @Getter @Setter private String username;
        @Getter @Setter private String email;
        protected transient String password;
        @Getter @Setter private String locale;
        @Getter @Setter private int flags;
        @Expose @Getter private int public_flags;
        @Getter @Setter private transient Timestamp token_nbf;

        @Getter @Setter private transient boolean cached;

        public User(UUID uuid) throws SQLException {
            this.uuid = uuid;

            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT * FROM `users` WHERE uuid = ?;");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                this.username = resultSet.getString("username");
                this.email = resultSet.getString("email");
                this.password = resultSet.getString("password");
                this.locale = resultSet.getString("locale");
                this.flags = resultSet.getInt("flags");
                this.public_flags = calculatePublicFlags();
                this.token_nbf = resultSet.getTimestamp("token_nbf");
            }
        }

        public void setPassword(String newPassword) {
            this.password = AuthenticationUtil.hashPassword(newPassword);
        }

        public int calculatePublicFlags() {
            int publicFlags = 0;

            // Administrator
            if ((flags & 1<<3) == 1<<3) {
                publicFlags = publicFlags + (1<<3);
            }

            // Publisher
            if ((flags & 1<<4) == 1<<4) {
                publicFlags = publicFlags + (1<<4);
            }

            // Teacher
            if ((flags & 1<<5) == 1<<5) {
                publicFlags = publicFlags + (1<<5);
            }

            return publicFlags;
        }

        @Override
        public String getJsonObject() {
            return getJsonObject(false);
        }

        public String getJsonObject(boolean authenticated) {
            Gson gson;
            if (authenticated) {
                gson = new Gson();
            } else {
                gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();
            }

            return gson.toJson(this);
        }

    }

}
