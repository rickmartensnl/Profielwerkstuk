/*
 * Copyright (c) 2022 Rick Martens - All rights not expressly granted herein are reserved
 *
 * This material is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 */

package nl.rickmartens.profielwerkstuk.utils;

import io.activej.http.HttpHeader;
import io.activej.http.HttpHeaders;

public class MyHeaders {

    public static final HttpHeader DO_CONNECTING_IP = HttpHeaders.register("do-connecting-ip");

}
