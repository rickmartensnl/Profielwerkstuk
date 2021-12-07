package nl.rickmartens.profielwerkstuk.middlewares;

import nl.rickmartens.profielwerkstuk.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;

public class Middleware {

    public HttpResponse handle(HttpRequest httpRequest, Controller controller) {
        try {
            return controller.runRequest(httpRequest);
        } catch (Exception exception) {
            Sentry.captureException(exception);
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }
    }

}
