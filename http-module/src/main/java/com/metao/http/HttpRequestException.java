package com.metao.http;

public class HttpRequestException extends Exception {
    private int statusCode;

    public HttpRequestException(String message, Throwable e) {
        super(message, e);
    }

    public HttpRequestException(String message, Throwable e, int responseCode) {
        this(message, e);
        this.statusCode = responseCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
