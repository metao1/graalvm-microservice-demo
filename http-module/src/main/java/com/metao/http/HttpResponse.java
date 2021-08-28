package com.metao.http;

public abstract class HttpResponse {

    protected int statusCode;
    protected String responseAsString;

    protected abstract String getHeader(String name);

    protected abstract void storeResponseHeaders();

    protected abstract void storeResponseBody();

}
