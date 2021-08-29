package com.metao.http;

public class ResponseEvent {
    private final HttpRequest req;
    private final HttpResponse res;
    private HttpRequestException exception;

    private ResponseEvent(HttpRequest req, HttpResponse res) {
        this.req = req;
        this.res = res;
    }

    public ResponseEvent(HttpRequest req, HttpResponse res, HttpRequestException exception) {
        this(req, res);
        this.exception = exception;
    }

    public static ResponseEvent createResponseEvent(HttpRequest req, HttpResponse res) {
        return new ResponseEvent(req, res);
    }

    public static ResponseEvent createResponseEvent(HttpRequest req, HttpResponse res, HttpRequestException e) {
        return new ResponseEvent(req, res, e);
    }

    boolean ranExceptionally() {
        return exception != null;
    }
}
