package com.example;

import com.example.api.APIController;
import com.example.database.impl.UserManager;
import io.activej.http.AsyncServlet;
import io.activej.http.RoutingServlet;
import io.activej.http.StaticServlet;
import io.activej.inject.annotation.Provides;
import io.activej.launchers.http.MultithreadedHttpServerLauncher;
import io.activej.worker.annotation.Worker;
import io.activej.worker.annotation.WorkerId;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public final class ProfielwerkstukServerLauncher extends MultithreadedHttpServerLauncher {

    public APIController apiController;
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection.isClosed()) {
            startConnection();
        }
        return connection;
    }

    public static void startConnection() throws SQLException {
        String databaseIp = System.getenv("DB_IP");
        String databasePassword = System.getenv("DB_PASSWORD");
        String databaseUsername = System.getenv("DB_USERNAME");
        String databaseName = System.getenv("DB_NAME");

        connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", databaseIp, databaseName), databaseUsername, databasePassword);
    }

    @Provides
    Executor executor() {
        return newSingleThreadExecutor();
    }

    public ProfielwerkstukServerLauncher() {
        try {
            startConnection();
            new UserManager();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        apiController = new APIController();
    }

    @Provides
    @Worker
    AsyncServlet servlet(@WorkerId int workerId, Executor executor) {
        return RoutingServlet.create()
                .map("/api/*", request -> request.loadBody().map($ -> apiController.runRequest(request)))
                .map("/*", StaticServlet.ofClassPath(executor, "build/")
                        .withMappingNotFoundTo("index.html"));
    }

    public static void main(String[] args) throws Exception {
        ProfielwerkstukServerLauncher launcher = new ProfielwerkstukServerLauncher();
        launcher.launch(args);
    }

}
