package com.example.api;

import com.example.api.v1.auth.V1AuthController;
import com.example.api.v1.chapters.V1ChaptersController;
import com.example.api.v1.subjects.V1SubjectsController;
import com.example.api.v1.users.V1UsersController;
import com.example.utils.Controller;
import com.example.utils.MyHeaders;
import io.activej.http.HttpHeaders;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import io.sentry.protocol.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class APIController implements Controller {

    public final Map<String, Map<APIVersion, Controller>> groups = new HashMap<>();

    public APIController() {
        HashMap<APIVersion, Controller> innerMapAuth = new HashMap<>();
        innerMapAuth.put(APIVersion.V1, new V1AuthController());
        groups.put("auth", innerMapAuth);

        HashMap<APIVersion, Controller> innerMapChapters = new HashMap<>();
        innerMapChapters.put(APIVersion.V1, new V1ChaptersController());
        groups.put("chapters", innerMapChapters);

        HashMap<APIVersion, Controller> innerMapSubjects = new HashMap<>();
        innerMapSubjects.put(APIVersion.V1, new V1SubjectsController());
        groups.put("subjects", innerMapSubjects);

        HashMap<APIVersion, Controller> innerMapUsers = new HashMap<>();
        innerMapUsers.put(APIVersion.V1, new V1UsersController());
        groups.put("users", innerMapUsers);
    }

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        String ip = httpRequest.getHeader(MyHeaders.DO_CONNECTING_IP);
        String userAgent = httpRequest.getHeader(HttpHeaders.USER_AGENT);

        Sentry.configureScope(scope -> {
            if (userAgent != null) {
                String[] splitedUserAgent = userAgent.split("/");
                HashMap<String, String> userAgentMap = new HashMap<>();
                if (splitedUserAgent.length >= 2) {
                    userAgentMap.put("name", splitedUserAgent[0]);
                    userAgentMap.put("version", splitedUserAgent[1]);
                    scope.setContexts("browser", userAgentMap);
                } else {
                    userAgentMap.put("name", splitedUserAgent[0]);
                    scope.setContexts("browser", userAgentMap);
                }
            }
            User sentryUser = new User();
            if (ip != null) {
                sentryUser.setIpAddress(ip);
            }
            Sentry.setUser(sentryUser);
        });

        String[] path = httpRequest.getRelativePath().toLowerCase().split("/");
        Controller controller = null;
        if (isVersioned(httpRequest)) {
            APIVersion apiVersion = getApiVersion(httpRequest);
            if (apiVersion == APIVersion.INVALID_VERSION) {
                return APIVersion.invalidApiVersion();
            }

            if (path.length < 2) {
                return notFound404("api_length");
            }

            Map<APIVersion, Controller> map = groups.get(path[1]);

            if (map != null) {
                controller = map.get(apiVersion);

                controller = getController(controller, map);
            }
        } else {
            Map<APIVersion, Controller> map = groups.get(path[0]);

            if (map != null) {
                controller = map.get(APIVersion.getCurrentVersion());

                controller = getController(controller, map);
            }
        }

        if (controller != null) {
            return controller.runRequest(httpRequest);
        }

        return notFound404("api_nocontroller");
    }

    @Nullable
    private Controller getController(Controller controller, Map<APIVersion, Controller> map) {
        if (controller == null) {
            int highestVersion = 0;
            Controller highestController = null;

            for (Map.Entry<APIVersion, Controller> entry : map.entrySet()) {
                if (entry.getKey().getVersion() > highestVersion) {
                    highestVersion = entry.getKey().getVersion();
                    highestController = entry.getValue();
                }
            }

            if (highestController != null) {
                controller = highestController;
            }
        }
        return controller;
    }

}
