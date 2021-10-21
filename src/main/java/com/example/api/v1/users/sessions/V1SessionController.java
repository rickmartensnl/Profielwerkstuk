package com.example.api.v1.users.sessions;

import com.example.database.impl.UserHistoryManager;
import com.example.database.impl.UserManager;
import com.example.exceptions.DatabaseOfflineException;
import com.example.middlewares.AuthMiddleware;
import com.example.utils.AllowMethods;
import com.example.utils.UseMiddleware;
import com.example.utils.UserController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@UseMiddleware({ AuthMiddleware.class })
@AllowMethods({ HttpMethod.GET, HttpMethod.POST, HttpMethod.PATCH })
public class V1SessionController implements UserController {

    @Getter @Setter public UserManager.User user;

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        SessionRequestType sessionRequestType = SessionRequestType.getFromQuery(httpRequest);

        try {
            if (sessionRequestType == SessionRequestType.NEW) {
                return HttpResponse.ok201().withJson(UserHistoryManager.getUserHistoryManager().createNewUserHistory(getUser()).getJsonObject());
            } else {
                UserHistoryManager.UserHistory[] userHistory = UserHistoryManager.getUserHistoryManager().getUnfinished(getUser());

                if (userHistory.length == 0) {
                    return HttpResponse.ok201().withJson(UserHistoryManager.getUserHistoryManager().createNewUserHistory(getUser()).getJsonObject());
                }

                Gson gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create();

                return HttpResponse.ok200().withJson(gson.toJson(userHistory));
            }
        } catch (DatabaseOfflineException exception) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }
    }

    enum SessionRequestType {

        NEW,
        UNFINISHED;

        public static SessionRequestType getFromQuery(HttpRequest httpRequest) {
            String queryTypeParam = httpRequest.getQueryParameter("type");

            if (queryTypeParam == null) {
                return SessionRequestType.UNFINISHED;
            }

            try {
                return SessionRequestType.valueOf(queryTypeParam.toUpperCase());
            } catch (IllegalArgumentException exception) {
                return SessionRequestType.UNFINISHED;
            }
        }

    }

}
