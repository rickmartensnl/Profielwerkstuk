package nl.rickmartens.profielwerkstuk.utils;

import io.activej.http.HttpHeader;
import io.activej.http.HttpHeaders;

public class MyHeaders {

    public static final HttpHeader DO_CONNECTING_IP = HttpHeaders.register("do-connecting-ip");

}
