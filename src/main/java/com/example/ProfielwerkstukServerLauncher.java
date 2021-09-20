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

    public ProfielwerkstukServerLauncher() {
        apiController = new APIController();
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
        ProfielwerkstukServerLauncher launcher = new ProfielwerkstukServerLauncher();
        launcher.launch(args);
    }

}
