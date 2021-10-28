package com.example.api.v1.chapters.paragraphs;

import com.example.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class V1ParagraphsController implements Controller {

    public final Map<String, Controller> controllers = new HashMap<>();
    public final Class<V1GetParagraphsController> fallbackController = V1GetParagraphsController.class;

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        String[] path = httpRequest.getRelativePath().toLowerCase().split("/");
        String controllerName;
        if (isVersioned(httpRequest)) {
            if (path.length > 4) {
                controllerName = path[4];
            } else {
                controllerName = "";
            }
        } else {
            if (path.length > 3) {
                controllerName = path[3];
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
