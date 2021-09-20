package com.example;

import com.example.api.APIController;
import io.activej.http.*;
import io.activej.inject.annotation.Provides;
import io.activej.launcher.Launcher;
import io.activej.launchers.http.MultithreadedHttpServerLauncher;
import io.activej.worker.annotation.Worker;
import io.activej.worker.annotation.WorkerId;

import java.util.concurrent.Executor;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public final class ProfielwerkstukServerLauncher extends MultithreadedHttpServerLauncher {

    public APIController apiController;

    @Provides
    Executor executor() {
        return newSingleThreadExecutor();
    }

    public ProfielwerkstukServerLauncher(String databaseIp, String databasePassword, String databaseUsername, String databaseName) {
        apiController = new APIController();
        System.out.println(databaseIp);
    }

    @Provides
    @Worker
    AsyncServlet servlet(@WorkerId int workerId, Executor executor) {
        return RoutingServlet.create()
                .map("/api/*", apiController::runRequest)
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
