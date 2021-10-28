package com.example.api.v1.chapters;

import com.example.api.v1.chapters.paragraphs.V1ParagraphsController;
import com.example.database.impl.ChapterManager;
import com.example.exceptions.DatabaseOfflineException;
import com.example.utils.AllowMethods;
import com.example.utils.Controller;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllowMethods({ HttpMethod.GET })
public class V1GetChaptersController implements Controller {

    public final Map<String, Controller> controllers = new HashMap<>();
    private UUID uuid;

    public V1GetChaptersController(@Nullable String uuid, HttpRequest httpRequest) {
        if (uuid != null && !uuid.equalsIgnoreCase("")) {
            this.uuid = UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
        }
    }

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        try {
            if (uuid == null) {
                return HttpResponse.ofCode(400).withJson("{\"message\":\"400: Bad Request\",\"code\":\"Chapter uuid required\"}");
            }

            ChapterManager.Chapter chapter = ChapterManager.getChapterManager().getChapter(uuid);

            if (chapter == null) {
                return HttpResponse.notFound404().withJson("{\"message\":\"404: Not Found\",\"code\":\"chapter not found\"}");
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

                HttpResponse res = callMiddleware(httpRequest, controller);

                if (res == null) {
                    return controller.runRequest(httpRequest);
                } else {
                    return res;
                }
            } else {
                return HttpResponse.ok200().withJson(chapter.getJsonObject());
            }
        } catch (DatabaseOfflineException exception) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }
    }

}
