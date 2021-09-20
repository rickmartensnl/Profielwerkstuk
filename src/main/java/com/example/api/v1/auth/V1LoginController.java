package com.example.api.v1.auth;

import com.example.utils.AllowMethods;
import com.example.utils.Controller;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

@AllowMethods({HttpMethod.POST})
public class V1LoginController implements Controller {

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        return HttpResponse.ok200().withJson("{\"token\":null}");
    }

}
