package com.metao.http;

import java.util.Map;

public interface HttpClient {

    void defaultRequestHeaders(RequestHeader... reqHeaders);

    Map<String, String> getRequestHeaders();

    HttpResponse request(HttpRequest req, HttpResponseListener httpResponseListener) throws HttpRequestException;

    HttpResponse request(HttpRequest req) throws HttpRequestException;

    HttpResponse get(String url) throws HttpRequestException;

    HttpResponse get(String url, HttpParameter[] params, Authorization authorization, HttpResponseListener listener) throws HttpRequestException;

    HttpResponse post(String url) throws HttpRequestException;

    HttpResponse post(String url, HttpParameter[] params, Authorization authorization, HttpResponseListener listener) throws HttpRequestException;
}
