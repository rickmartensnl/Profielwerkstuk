package com.example.api.v1.auth;

import com.example.database.impl.UserManager;
import com.example.exceptions.DatabaseOfflineException;
import com.example.utils.AllowMethods;
import com.example.utils.Controller;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@AllowMethods({HttpMethod.POST})
public class V1LoginController implements Controller {

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        try {
            UserManager.User user = UserManager.getUserManager().getUser(UUID.fromString(Objects.requireNonNull(httpRequest.getQueryParameter("uuid"))));

            return HttpResponse.ok200().withJson(user.getJsonObject());
        } catch (DatabaseOfflineException exception) {
            return HttpResponse.ofCode(500).withJson("{}");
        }
    }

}
