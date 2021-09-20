package com.example.api;

import com.example.api.v1.auth.V1AuthController;
import com.example.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class APIController implements Controller {

    public final Map<String, Map<APIVersion, Controller>> groups = new HashMap<>();

    public APIController() {
        HashMap<APIVersion, Controller> innerMap = new HashMap<>();

        innerMap.put(APIVersion.V1, new V1AuthController());
        groups.put("auth", innerMap);
    }

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        String[] path = httpRequest.getRelativePath().toLowerCase().split("/");
        Controller controller = null;
        if (isVersioned(httpRequest)) {
            APIVersion apiVersion = getApiVersion(httpRequest);
            if (apiVersion == APIVersion.INVALID_VERSION) {
                return APIVersion.invalidApiVersion();
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

        return notFound404();
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
