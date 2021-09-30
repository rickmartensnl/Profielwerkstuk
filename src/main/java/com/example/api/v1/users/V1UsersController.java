package com.example.api.v1.users;

import com.example.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class V1UsersController implements Controller {

    public final Map<String, Controller> controllers = new HashMap<>();
    public final Class<V1GetUserController> fallbackController = V1GetUserController.class;

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
        } else {
            try {
                Controller myController = fallbackController.getDeclaredConstructor(String.class, HttpRequest.class).newInstance(controllerName, httpRequest);

                callMiddleware(httpRequest, myController);

                return myController.runRequest(httpRequest);
            } catch (Exception ignored) {
            }
        }

        return notFound404("users");
    }

}
