package com.example.middlewares;

import com.example.utils.Controller;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;

public class Middleware {

    public HttpResponse handle(HttpRequest httpRequest, Controller controller) {
        try {
            return controller.runRequest(httpRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResponse.ofCode(500).withJson("{}");
        }
    }

}
