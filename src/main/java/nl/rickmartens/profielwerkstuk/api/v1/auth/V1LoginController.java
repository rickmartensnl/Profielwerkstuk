/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

package nl.rickmartens.profielwerkstuk.api.v1.auth;

import nl.rickmartens.profielwerkstuk.database.impl.UserManager;
import nl.rickmartens.profielwerkstuk.exceptions.DatabaseOfflineException;
import nl.rickmartens.profielwerkstuk.exceptions.InvalidSyntaxException;
import nl.rickmartens.profielwerkstuk.exceptions.TokenCreateException;
import nl.rickmartens.profielwerkstuk.utils.AllowMethods;
import nl.rickmartens.profielwerkstuk.utils.Controller;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

@AllowMethods({ HttpMethod.POST })
public class V1LoginController implements Controller {

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        LoginBody loginBody = new Gson().fromJson(JsonParser.parseString(httpRequest.getBody().getString(StandardCharsets.UTF_8)), LoginBody.class);

        try {
            UserManager.User user = UserManager.getUserManager().getUserByEmail(loginBody.getEmail());

            if (user == null) {
                return HttpResponse.ofCode(401).withJson("{\"message\":\"401: Unauthorized\",\"code\":0}");
            }

            if (!user.isValidPassword(loginBody.getPassword())) {
                return HttpResponse.ofCode(401).withJson("{\"message\":\"401: Unauthorized\",\"code\":0}");
            }

            return HttpResponse.ok200().withJson(user.getAuthObject());
        } catch (InvalidSyntaxException exception) {
            return HttpResponse.ofCode(400).withJson("{\"message\":\"400: Bad Request\",\"code\":0}");
        } catch (DatabaseOfflineException | TokenCreateException exception) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }
    }

    @Getter
    private class LoginBody {

        protected final String email;
        protected final String password;

        public LoginBody(String email, String password) {
            this.email = email;
            this.password = password;
        }

    }

}
