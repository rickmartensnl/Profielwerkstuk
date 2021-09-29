package com.example.api.v1.users;

import com.example.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import org.jetbrains.annotations.NotNull;

public class V1GetUserController implements Controller {

    public V1GetUserController(String uuid) {
        System.out.println(uuid);
    }

    @Override
    public @NotNull HttpResponse runRequest(HttpRequest httpRequest) {
        return HttpResponse.ofCode(204);
    }

}
