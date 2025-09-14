package com.camping.admin.helper;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH;

    public static HttpMethod fromString(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }
}