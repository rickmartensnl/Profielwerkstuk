package nl.rickmartens.profielwerkstuk;

import nl.rickmartens.profielwerkstuk.api.APIController;
import io.activej.http.AsyncServlet;
import io.activej.http.RoutingServlet;
import io.activej.http.StaticServlet;
import io.activej.inject.annotation.Provides;
import io.activej.launchers.http.MultithreadedHttpServerLauncher;
import io.activej.worker.annotation.Worker;
import io.activej.worker.annotation.WorkerId;
import io.sentry.Sentry;
import nl.rickmartens.profielwerkstuk.database.impl.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public final class ProfielwerkstukServerLauncher extends MultithreadedHttpServerLauncher {

    public APIController apiController;
    private static Connection connection;
    private static long lastConnectionRequest;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            return startConnection();
        }

        if (System.currentTimeMillis() >= lastConnectionRequest) {
            connection.prepareStatement("SELECT 1;").executeQuery();
        }

        lastConnectionRequest = System.currentTimeMillis() + 60L * 60L * 1000L;

        return connection;
    }

    public static Connection startConnection() throws SQLException {
        String databaseIp = System.getenv("DB_IP");
        String databasePassword = System.getenv("DB_PASSWORD");
        String databaseUsername = System.getenv("DB_USERNAME");
        String databaseName = System.getenv("DB_NAME");
        String formattedConnString = String.format("jdbc:mysql://%s/%s?autoReconnect=true&useUnicode=yes", databaseIp, databaseName);

        connection = DriverManager.getConnection(formattedConnString, databaseUsername, databasePassword);
        return connection;
    }

    @Provides
    Executor executor() {
        return newSingleThreadExecutor();
    }

    public ProfielwerkstukServerLauncher() {
        try {
            startConnection();

            Timer timer = new Timer();
            timer.schedule(new DatabaseKeepAliveTask(), 60000);

            new UserManager();
            new UserHistoryManager();
            new ParagraphManager();
            new SubjectManager();
            new ChapterManager();
            new QuestionManager();
        } catch (SQLException exception) {
            Sentry.captureException(exception);
        }

        apiController = new APIController();
    }

    private static class DatabaseKeepAliveTask extends TimerTask {
        @Override
        public void run() {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT uuid FROM users WHERE uuid=1");
                preparedStatement.executeQuery();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
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
        Sentry.init(options -> {
            options.setDsn(System.getenv("SENTRY_DSN"));
        });
        ProfielwerkstukServerLauncher launcher = new ProfielwerkstukServerLauncher();
        launcher.launch(args);
    }

}
