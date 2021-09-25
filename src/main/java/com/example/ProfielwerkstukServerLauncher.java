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
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public final class ProfielwerkstukServerLauncher extends MultithreadedHttpServerLauncher {

    public APIController apiController;
    @Getter private static Connection connection;

    @Provides
    Executor executor() {
        return newSingleThreadExecutor();
    }

    public ProfielwerkstukServerLauncher(String databaseIp, String databasePassword, String databaseUsername, String databaseName) {
        try {
            connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s", databaseIp, databaseName), databaseUsername, databasePassword);
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
        String databaseIp = System.getenv("DB_IP");
        String databasePassword = System.getenv("DB_PASSWORD");
        String databaseUsername = System.getenv("DB_USERNAME");
        String databaseName = System.getenv("DB_NAME");

        ProfielwerkstukServerLauncher launcher = new ProfielwerkstukServerLauncher(databaseIp, databasePassword, databaseUsername, databaseName);
        launcher.launch(args);
    }

}
