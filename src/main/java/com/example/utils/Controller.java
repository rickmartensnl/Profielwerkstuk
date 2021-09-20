package com.example.utils;

import com.example.api.APIVersion;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Controller {

    default HttpResponse notFound404() {
        return HttpResponse.notFound404().withJson("{\"message\":\"404: Not Found\",\"code\":0}");
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
