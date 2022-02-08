/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

package nl.rickmartens.profielwerkstuk.utils;

import nl.rickmartens.profielwerkstuk.api.APIVersion;
import nl.rickmartens.profielwerkstuk.middlewares.Middleware;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.sentry.Sentry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Controller {

    default HttpResponse notFound404(String code) {
        return HttpResponse.notFound404().withJson("{\"message\":\"404: Not Found\",\"code\":\"" + code + "\"}");
    }

    default HttpResponse methodNotAllowed405() {
        return HttpResponse.ofCode(405).withJson("{\"message\":\"405: Method Not Allowed\",\"code\":0}");
    }

    default HttpResponse notImplemented501() {
        return HttpResponse.ofCode(501).withJson("{\"message\":\"501: Not Implemented\",\"code\":0}");
    }

    @NotNull HttpResponse runRequest(HttpRequest httpRequest);

    default boolean isAllowedMethod(HttpRequest httpRequest, Controller controller) {
        Class<? extends Controller> controllerClass = controller.getClass();
        if (!controllerClass.isAnnotationPresent(AllowMethods.class)) {
            return true;
        }

        return Arrays.stream(controllerClass.getAnnotation(AllowMethods.class).value()).anyMatch(httpMethod -> httpMethod == httpRequest.getMethod());
    }

    default @Nullable HttpResponse callMiddleware(HttpRequest httpRequest, Controller controller) {
        Class<? extends Controller> controllerClass = controller.getClass();
        if (!controllerClass.isAnnotationPresent(UseMiddleware.class)) {
            return null;
        }

        Class[] middlewares = controllerClass.getAnnotation(UseMiddleware.class).value();

        for (Class middlewareClass : middlewares) {
            try {
                Middleware middleware = (Middleware) middlewareClass.newInstance();
                return middleware.handle(httpRequest, controller);
            } catch (Exception exception) {
                Sentry.captureException(exception);
            }
        }

        return null;
    }

    default boolean isVersioned(HttpRequest httpRequest) {
        String[] path = httpRequest.getRelativePath().split("/");
        Matcher matcher = Pattern.compile("[vV]([0-9]*)$").matcher(path[0]);
        return matcher.find();
    }

    default APIVersion getApiVersion(HttpRequest httpRequest) {
        String[] path = httpRequest.getRelativePath().split("/");
        Matcher matcher = Pattern.compile("[vV]([0-9]*)$").matcher(path[0]);

        if (matcher.find()) {
            return APIVersion.findByVersion(Integer.parseInt(matcher.group(1)));
        }

        return APIVersion.getCurrentVersion();
    }

}
