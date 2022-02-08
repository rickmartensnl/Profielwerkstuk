/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

package nl.rickmartens.profielwerkstuk.api.v1.chapters;

import nl.rickmartens.profielwerkstuk.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class V1ChaptersController implements Controller {

    public final Map<String, Controller> controllers = new HashMap<>();
    public final Class<V1GetChaptersController> fallbackController = V1GetChaptersController.class;

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        String[] path = httpRequest.getRelativePath().toLowerCase().split("/");
        String controllerName;
        if (isVersioned(httpRequest)) {
            if (path.length > 2) {
                controllerName = path[2];
            } else {
                controllerName = "";
            }
        } else {
            if (path.length > 1) {
                controllerName = path[1];
            } else {
                controllerName = "";
            }
        }

        Controller controller = controllers.get(controllerName);

        if (controller != null) {
            if (!isAllowedMethod(httpRequest, controller)) {
                return methodNotAllowed405();
            }

            HttpResponse res = callMiddleware(httpRequest, controller);

            if (res == null) {
                return controller.runRequest(httpRequest);
            } else {
                return res;
            }
        } else {
            try {
                Controller myController = fallbackController.getDeclaredConstructor(String.class, HttpRequest.class).newInstance(controllerName, httpRequest);

                HttpResponse res = callMiddleware(httpRequest, myController);

                if (res == null) {
                    return myController.runRequest(httpRequest);
                } else {
                    return res;
                }
            } catch (Exception exception) {
                Sentry.captureException(exception);
                return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
            }
        }
    }

}
