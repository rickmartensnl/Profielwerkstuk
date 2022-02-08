/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

package nl.rickmartens.profielwerkstuk.database.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import nl.rickmartens.profielwerkstuk.ProfielwerkstukServerLauncher;
import nl.rickmartens.profielwerkstuk.database.Model;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import nl.rickmartens.profielwerkstuk.exceptions.DuplicateEmailException;
import nl.rickmartens.profielwerkstuk.exceptions.InvalidSyntaxException;
import nl.rickmartens.profielwerkstuk.exceptions.TokenCreateException;
import nl.rickmartens.profielwerkstuk.utils.AuthenticationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import io.sentry.Sentry;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    @Getter private static UserManager userManager;

    public Map<UUID, User> userMap;

    public UserManager() {
        userManager = this;
        userMap = new HashMap<>();
    }

    public User createUser(String username, String email, String password, String locale) throws DatabaseOfflineException, InvalidSyntaxException, DuplicateEmailException {
        if (!AuthenticationUtil.isValidEmail(email)) {
            throw new InvalidSyntaxException();
        }

        if (getUserByEmail(email) != null) {
            throw new DuplicateEmailException();
        }

        try {
            User user = new User(username, email, password, locale);
            userMap.put(user.getUuid(), user);

            return user;
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }

    public @Nullable User getUser(UUID uuid) throws DatabaseOfflineException {
        return getUser(uuid, false);
    }

    public @Nullable User getUser(UUID uuid, boolean noCache) throws DatabaseOfflineException {
        if (userMap.get(uuid) != null && !noCache) {
            userMap.get(uuid).setCached(true);
            return userMap.get(uuid);
        } else if (userMap.get(uuid) != null) {
            userMap.remove(uuid);
        }

        try {
            User user = new User(uuid);
            userMap.put(uuid, user);

            return user;
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }

    public @Nullable User getUserByEmail(String email) throws InvalidSyntaxException, DatabaseOfflineException {
        if (!AuthenticationUtil.isValidEmail(email)) {
            throw new InvalidSyntaxException();
        }

        try {
            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("SELECT uuid FROM `users` WHERE email = ?;");
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                return getUser(UUID.fromString(resultSet.getString("uuid")), true);
            }

            return null;
        } catch (SQLException exception) {
            Sentry.captureException(exception);
            throw new DatabaseOfflineException();
        }
    }

    public static class User implements Model {

        @Expose @Getter private final UUID uuid;
        @Expose @Getter @Setter private String username;
        @Getter @Setter private String email;
        protected transient String password;
        @Getter @Setter private String locale;
        @Getter private int flags;
        @Expose @Getter private int public_flags;
        @Getter @Setter private transient Timestamp token_nbf;

        @Getter @Setter private transient boolean cached;
        private transient Auth auth;

        public User(String username, String email, String password, String locale) throws SQLException {
            this.uuid = UUID.randomUUID();
            this.username = username;
            this.email = email;
            this.password = AuthenticationUtil.hashPassword(password);
            this.locale = locale;
            this.flags = 0;
            this.public_flags = calculatePublicFlags();
            this.token_nbf = new Timestamp(System.currentTimeMillis() - 1);

            PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("INSERT INTO `users` (`uuid`, `username`, `email`, `password`, `locale`, `flags`, `token_nbf`) VALUES (?,?,?,?,?,?,?);");
            preparedStatement.setString(1, this.uuid.toString());
            preparedStatement.setString(2, this.username);
            preparedStatement.setString(3, this.email);
            preparedStatement.setString(4, this.password);
            preparedStatement.setString(5, this.locale);
            preparedStatement.setInt(6, this.flags);
            preparedStatement.setTimestamp(7, this.token_nbf);
            preparedStatement.executeUpdate();
        }

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

        public boolean isValidPassword(String password) {
            return AuthenticationUtil.verifyPassword(password, this.password);
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
        
        public void setFlags(int flags) {
            this.flags = flags;
            this.public_flags = calculatePublicFlags();
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

        public void save() throws DatabaseOfflineException {
            try {
                PreparedStatement preparedStatement = ProfielwerkstukServerLauncher.getConnection().prepareStatement("UPDATE `users` SET `username`=?, `email`=?, `password`=?, `locale`=?, `flags`=?, `token_nbf`=? WHERE `uuid`=?;");
                preparedStatement.setString(1, this.username);
                preparedStatement.setString(2, this.email);
                preparedStatement.setString(3, this.password);
                preparedStatement.setString(4, this.locale);
                preparedStatement.setInt(5, this.flags);
                preparedStatement.setTimestamp(6, this.token_nbf);
                preparedStatement.setString(7, this.uuid.toString());
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                Sentry.captureException(exception);
                throw new DatabaseOfflineException();
            }
        }

        public String getAuthObject() throws TokenCreateException {
            this.auth = new Auth(uuid);

            return auth.getJsonObject();
        }

    }

    public static class Auth implements Model {

        private String token;
        private boolean mfa = false;

        public Auth(UUID uuid) throws TokenCreateException {
            try {
                this.token = JWT.create()
                        .withAudience("https://pws.rickmartens.nl")
                        .withIssuer("https://pws.rickmartens.nl")
                        .withIssuedAt(new Date())
                        .withSubject(uuid.toString())
                        .sign(AuthenticationUtil.getAlgorithm());
            } catch (JWTCreationException exception) {
                Sentry.captureException(exception);
                throw new TokenCreateException();
            }
        }

        @Override
        public String getJsonObject() {
            return new Gson().toJson(this);
        }

    }

}
