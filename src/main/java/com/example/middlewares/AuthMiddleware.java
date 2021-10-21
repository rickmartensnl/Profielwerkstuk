package com.example.middlewares;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.database.impl.UserManager;
import com.example.exceptions.DatabaseOfflineException;
import com.example.exceptions.TokenVerifyException;
import com.example.utils.AuthenticationUtil;
import com.example.utils.Controller;
import com.example.utils.MyHeaders;
import com.example.utils.UserController;
import io.activej.http.HttpHeader;
import io.activej.http.HttpHeaders;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import io.sentry.protocol.User;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class AuthMiddleware extends Middleware {

    @Override
    public HttpResponse handle(HttpRequest httpRequest, Controller controller) {
        UUID uuid = getSubject(httpRequest);

        if (uuid == null) {
            return HttpResponse.ofCode(401).withJson("{\"message\":\"401: Unauthorized\",\"code\":0}");
        }

        try {
            UserManager.User myUser = UserManager.getUserManager().getUser(uuid);

            if (myUser == null) {
                return HttpResponse.ofCode(401).withJson("{\"message\":\"401: Unauthorized\",\"code\":0}");
            }

            if (controller instanceof UserController) {
                UserController userController = (UserController) controller;

                userController.setUser(myUser);
            }

            Sentry.configureScope(scope -> {
                User sentryUser = scope.getUser();
                assert sentryUser != null;
                sentryUser.setEmail(myUser.getEmail());
                sentryUser.setId(myUser.getUuid().toString());
                sentryUser.setUsername(myUser.getUsername());
                Sentry.setUser(sentryUser);
            });

            Sentry.captureException(new Exception());
        } catch (DatabaseOfflineException e) {
            return HttpResponse.ofCode(500).withJson("{\"message\":\"500: Internal Server Error\",\"code\":\"" + Sentry.getLastEventId() + "\"}");
        }

        return super.handle(httpRequest, controller);
    }

    public static @Nullable UUID getSubject(HttpRequest httpRequest) {
        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            DecodedJWT decodedJWT = AuthenticationUtil.tokenToBody(authHeader);

            return decodedJWT != null ? UUID.fromString(decodedJWT.getSubject()) : null;
        } catch (TokenVerifyException ignored) {
        }

        return null;
    }

}
