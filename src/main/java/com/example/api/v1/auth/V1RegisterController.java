package com.example.api.v1.auth;

import com.example.database.impl.UserManager;
import com.example.exceptions.DatabaseOfflineException;
import com.example.exceptions.DuplicateEmailException;
import com.example.exceptions.InvalidSyntaxException;
import com.example.exceptions.TokenCreateException;
import com.example.utils.AllowMethods;
import com.example.utils.Controller;
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
public class V1RegisterController implements Controller {

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        RegisterBody registerBody = new Gson().fromJson(JsonParser.parseString(httpRequest.getBody().getString(StandardCharsets.UTF_8)), RegisterBody.class);

        try {
            if (!registerBody.isTerms()) {
                throw new InvalidSyntaxException();
            }

            UserManager.User user = UserManager.getUserManager().createUser(registerBody.getUsername(), registerBody.getEmail(), registerBody.getPassword(), "nl");

            return HttpResponse.ok201().withJson(user.getAuthObject());
        } catch (DuplicateEmailException | InvalidSyntaxException exception) {
            return HttpResponse.ofCode(400).withJson("{\"message\":\"400: Bad Request\",\"code\":0}");
        } catch (DatabaseOfflineException | TokenCreateException exception) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }
    }

    @Getter
    private class RegisterBody {

        protected final String username;
        protected final String email;
        protected final String password;
        protected final boolean terms;

        public RegisterBody(String username, String email, String password, boolean terms) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.terms = terms;
        }

    }

}
