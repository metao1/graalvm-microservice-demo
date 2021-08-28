package com.metao.http;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class HttpClientBase implements HttpClient {
    private final Map<String, String> reqHeaders;
    private final Map<String, List<String>> resHeaders;

    protected HttpClientBase() {
        this.reqHeaders = new HashMap<>();
        this.resHeaders = new LinkedHashMap<>();
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

    protected void storeResponseHeader(Map<String, List<String>> responseHeadersMap) {
        resHeaders.putAll(responseHeadersMap);
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
