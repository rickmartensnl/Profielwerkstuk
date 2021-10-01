package com.example.middlewares;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.exceptions.TokenVerifyException;
import com.example.utils.AuthenticationUtil;
import com.example.utils.Controller;
import io.activej.http.HttpHeaders;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AuthMiddleware extends Middleware {

    @Override
    public HttpResponse handle(HttpRequest httpRequest, Controller controller) {
        System.out.println("Test");

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
