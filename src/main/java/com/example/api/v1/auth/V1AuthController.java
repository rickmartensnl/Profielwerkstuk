package com.example.api.v1.auth;

import com.example.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class V1AuthController implements Controller {

    public final Map<String, Controller> controllers = new HashMap<>();

    public V1AuthController() {
        controllers.put("login", new V1LoginController());
    }

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        String[] path = httpRequest.getRelativePath().toLowerCase().split("/");
        String controllerName;
        if (isVersioned(httpRequest)) {
            controllerName = path[2];
        } else {
            controllerName = path[1];
        }
        Controller controller = controllers.get(controllerName);

        if (controller != null) {
            if (!isAllowedMethod(httpRequest, controller)) {
                return methodNotAllowed405();
            }

            callMiddleware(httpRequest, controller);

            return controller.runRequest(httpRequest);
        }

        return notFound404("auth");
    }

}
