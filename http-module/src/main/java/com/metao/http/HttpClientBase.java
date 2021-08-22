package com.metao.http;

import java.util.HashMap;
import java.util.Map;

public abstract class HttpClientBase implements HttpClient {
    private final Map<String, String> reqHeaders;

    protected HttpClientBase() {
        this.reqHeaders = new HashMap<>();
        this.reqHeaders.put("X-Twitter-Client-Version", Version.getVersion());
        this.reqHeaders.put("X-Twitter-Client", Version.getClient());
    }

    @Override
    public void defaultRequestHeaders(RequestHeader... reqHeaders) {
        for (RequestHeader reqHeader : reqHeaders) {
            this.reqHeaders.put(reqHeader.name(), reqHeader.value());
        }
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        return reqHeaders;
    }

    @Override
    public HttpResponse request(HttpRequest req, HttpResponseListener listener) {
        HttpResponse res = handleRequest(req);
        if (listener != null) {
            listener.onResponse(ResponseEvent.createResponseEvent(req, res));
        }
        return res;
    }

    @Override
    public HttpResponse request(HttpRequest req) {
        return handleRequest(req);
    }

    @Override
    public HttpResponse post(String url) {
        return request(new HttpRequest(url, RequestMethod.POST, null, this.reqHeaders, null));
    }

    @Override
    public HttpResponse post(String url, HttpParameter[] params, Authorization auth, HttpResponseListener listener) {
        return request(new HttpRequest(url, RequestMethod.POST, params, this.reqHeaders, auth), listener);
    }

    @Override
    public HttpResponse get(String url) {
        return request(new HttpRequest(url, RequestMethod.GET, null, this.reqHeaders, null));
    }

    @Override
    public HttpResponse get(String url, HttpParameter[] params, Authorization authorization, HttpResponseListener listener) {
        return request(new HttpRequest(url, RequestMethod.GET, params, reqHeaders, authorization), listener);
    }

    protected abstract HttpResponse handleRequest(HttpRequest req);
}
