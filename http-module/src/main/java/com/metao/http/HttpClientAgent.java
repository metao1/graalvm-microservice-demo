package com.metao.http;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class HttpClientAgent extends HttpClientBase {

    private static final MediaType FORM_URL_ENCODED = MediaType.parse("application/x-www-form-urlencoded");
    private static final long TIMEOUT = 10000;
    private static final long CACHE_TTL = 10;
    private static final int MAX_CONNECTIONS = 5;
    private static final long KEEP_ALIVE_DURATION_MS = 300;

    private final OkHttpClient okHttpClient;
    private Authorization authorization;

    public HttpClientAgent() {
        prepareCache();
        this.okHttpClient = buildOkHttpClient();
    }

    public HttpClientAgent(Authorization authorization) {
        this();
        this.authorization = authorization;
    }

    @Override
    protected HttpResponse handleRequest(HttpRequest req) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(req.getUrl());
        requestBuilder.headers(getHeaders(req));

        switch (req.getRequestMethod()) {
            case GET -> requestBuilder.get();
            case POST -> requestBuilder.post(getRequestBody(req));
        }
        Request request = requestBuilder.build();
        Call call = okHttpClient.newCall(request);
        OkHttpResponse res = null;
        try {
            res = new OkHttpResponse(call, okHttpClient);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    private RequestBody getRequestBody(HttpRequest req) {
        HttpParameter[] parameters = req.getParameters();
        RequestBody requestBody = RequestBody.create(HttpParameter.encodeParams(parameters), FORM_URL_ENCODED);
        return requestBody;
    }

    private Headers getHeaders(@NonNull HttpRequest req) {
        Headers.Builder headersBuilder = new Headers.Builder();
        Authorization authorization = req.getAuthorization();
        if (authorization != null && authorization.getAuthorizationHeader(req) != null) {
            String authorizationHeader = authorization.getAuthorizationHeader(req);
            getRequestHeaders().put("Authorization", authorizationHeader);
        }
        getRequestHeaders().forEach(headersBuilder::add);
        return headersBuilder.build();
    }

    private Cache cache;

    private void prepareCache() {
        try {
            cache = new Cache(File.createTempFile(".cache", "http3-cache" + Thread.currentThread()), CACHE_TTL);
        } catch (IOException e) {
            log.error("creating cache file was denied:{}", e.getMessage());
        }
    }

    private OkHttpClient buildOkHttpClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        //set protocols
        List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        protocols.add(Protocol.HTTP_2);
        okHttpClient.protocols(protocols);

        //connectionPool setup
        okHttpClient.connectionPool(new ConnectionPool(MAX_CONNECTIONS, KEEP_ALIVE_DURATION_MS, TimeUnit.MILLISECONDS));

        //redirect disable
        okHttpClient.followSslRedirects(false);
        okHttpClient.retryOnConnectionFailure(true);
        return okHttpClient
                .callTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .cache(cache)
                .build();

    }
}