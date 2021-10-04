package com.example.api.v1.users;

import com.example.api.v1.users.sessions.V1SessionController;
import com.example.database.impl.UserManager;
import com.example.exceptions.DatabaseOfflineException;
import com.example.middlewares.AuthMiddleware;
import com.example.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class V1GetUserController implements Controller {

    public final Map<String, Controller> controllers = new HashMap<>();
    private final UUID uuid;

    public V1GetUserController(String uuid, HttpRequest httpRequest) {
        controllers.put("sessions", new V1SessionController());

        System.out.println(uuid);
        if (uuid.equalsIgnoreCase("@me")) {
            this.uuid = AuthMiddleware.getSubject(httpRequest);
        } else {
            this.uuid = UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
        }
    }

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        try {
            if (uuid == null) {
                if (AuthMiddleware.getSubject(httpRequest) == null) {
                    return HttpResponse.ofCode(401).withJson("{\"message\":\"401: Unauthorized\",\"code\":\"no (valid) token\"}");
                } else {
                    return HttpResponse.ofCode(400).withJson("{\"message\":\"400: Bad Request\",\"code\":\"no uuid\"}");
                }
            }

            UserManager.User user = UserManager.getUserManager().getUser(uuid);

            if (user == null) {
                return HttpResponse.notFound404().withJson("{\"message\":\"404: Not Found\",\"code\":\"user not found\"}");
            }

            String[] path = httpRequest.getRelativePath().toLowerCase().split("/");
            String controllerName;
            if (isVersioned(httpRequest)) {
                controllerName = path.length >= 4 ? path[3] : null;
            } else {
                controllerName = path.length >= 3 ? path[2] : null;
            }

            Controller controller = controllers.get(controllerName);

            if (controller != null) {
                if (!isAllowedMethod(httpRequest, controller)) {
                    return methodNotAllowed405();
                }

                callMiddleware(httpRequest, controller);

                return controller.runRequest(httpRequest);
            } else {
                UUID authUUID = AuthMiddleware.getSubject(httpRequest);

                if (authUUID == null) {
                    return HttpResponse.ok200().withJson(user.getJsonObject());
                }

                if (user.getUuid().equals(authUUID)) {
                    return HttpResponse.ok200().withJson(user.getJsonObject(true));
                }

                return HttpResponse.ok200().withJson(user.getJsonObject());
            }
        } catch (DatabaseOfflineException exception) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"" + exception.getClass().getName() + "\"}");
        }
    }

}
