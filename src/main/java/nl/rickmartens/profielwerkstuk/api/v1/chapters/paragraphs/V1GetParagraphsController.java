package nl.rickmartens.profielwerkstuk.api.v1.chapters.paragraphs;

import nl.rickmartens.profielwerkstuk.database.impl.ParagraphManager;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import nl.rickmartens.profielwerkstuk.utils.AllowMethods;
import nl.rickmartens.profielwerkstuk.utils.Controller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
public class V1GetParagraphsController implements Controller {

    public final Map<String, Controller> controllers = new HashMap<>();
    private UUID uuid;

    public V1GetParagraphsController(@Nullable String uuid, HttpRequest httpRequest) {
        if (uuid != null && !uuid.equalsIgnoreCase("")) {
            this.uuid = UUID.fromString(uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
        }
    }

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        try {
            String[] path = httpRequest.getRelativePath().toLowerCase().split("/");

            if (uuid == null) {
                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();

                String controllerName;
                if (isVersioned(httpRequest)) {
                    controllerName = path[2];
                } else {
                    controllerName = path[1];
                }

                return HttpResponse.ok200().withJson(gson.toJson(ParagraphManager.getParagraphManager().getAllParagraphsByChapter(UUID.fromString(controllerName.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")))));
            }

            ParagraphManager.Paragraph paragraph = ParagraphManager.getParagraphManager().getParagraph(uuid);

            if (paragraph == null) {
                return HttpResponse.notFound404().withJson("{\"message\":\"404: Not Found\",\"code\":\"paragraph not found\"}");
            }

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
                return HttpResponse.ok200().withJson(paragraph.getJsonObject());
            }
        } catch (DatabaseOfflineException exception) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }
    }

}
