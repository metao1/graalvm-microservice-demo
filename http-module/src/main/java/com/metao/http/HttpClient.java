package com.metao.http;

import java.util.Map;

public interface HttpClient {

    public void defaultRequestHeaders(RequestHeader... reqHeaders);

    public Map<String, String> getRequestHeaders();

    public HttpResponse request(HttpRequest req, HttpResponseListener httpResponseListener);

    public HttpResponse request(HttpRequest req);

    public HttpResponse get(String url);

    public HttpResponse get(String url, HttpParameter[] params, Authorization authorization, HttpResponseListener listener);

    public HttpResponse post(String url);

    public HttpResponse post(String url, HttpParameter[] params, Authorization authorization, HttpResponseListener listener);
}
