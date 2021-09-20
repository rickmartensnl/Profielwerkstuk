package com.example.api;

import io.activej.http.HttpResponse;

public enum APIVersion {

    INVALID_VERSION(0),
    V1(1);

    private final int version;

    APIVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public static APIVersion getCurrentVersion() {
        return V1;
    }

    public static APIVersion findByVersion(int version){
        for (APIVersion v : values()) {
            if (v.getVersion() == version) {
                return v;
            }
        }
        return INVALID_VERSION;
    }

    public static HttpResponse invalidApiVersion() {
        return HttpResponse.ofCode(400).withJson("{\"message\":\"Invalid API version\",\"code\":0}");
    }

}
