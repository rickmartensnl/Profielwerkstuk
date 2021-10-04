package com.example.api.v1.users.sessions;

import com.example.middlewares.AuthMiddleware;
import com.example.utils.AllowMethods;
import com.example.utils.Controller;
import com.example.utils.UseMiddleware;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

@UseMiddleware({ AuthMiddleware.class })
@AllowMethods({ HttpMethod.GET, HttpMethod.POST, HttpMethod.PATCH })
public class V1SessionController implements Controller {

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {

        return HttpResponse.ok200().withPlainText(httpRequest.getRelativePath() + httpRequest.getQueryParameters());
    }

}
