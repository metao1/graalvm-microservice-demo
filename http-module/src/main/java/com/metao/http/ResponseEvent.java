package com.metao.http;

public class ResponseEvent {
    private final HttpRequest req;
    private final HttpResponse res;

    private ResponseEvent(HttpRequest req, HttpResponse res) {
        this.req = req;
        this.res = res;
    }

    public static ResponseEvent createResponseEvent(HttpRequest req, HttpResponse res) {
        return new ResponseEvent(req, res);
    }
}
