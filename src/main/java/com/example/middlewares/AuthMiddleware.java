package com.example.middlewares;

import com.example.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;

public class AuthMiddleware extends Middleware {

    @Override
    public HttpResponse handle(HttpRequest httpRequest, Controller controller) {
        System.out.println("Test");

        return super.handle(httpRequest, controller);
    }
}
