package com.metao.http.service;

import com.metao.http.exception.HttpRequestException;
import com.metao.http.model.http.HttpParameter;
import com.metao.http.model.request.HttpRequest;
import com.metao.http.model.response.HttpResponse;
import com.metao.http.model.response.OkHttpResponse;
import com.metao.http.utils.HttpUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class HttpClientAgent extends HttpClientBase {

    private static final MediaType FORM_URL_ENCODED = MediaType.parse("application/x-www-form-urlencoded");
    private static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
    private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
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
    protected HttpResponse handleRequest(HttpRequest req) throws HttpRequestException {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(req.getUrl());
        requestBuilder.headers(getHeaders(req));

        switch (req.getRequestMethod()) {
            case GET -> requestBuilder.get();
            case POST -> requestBuilder.post(getRequestBody(req));
        }
        Request request = requestBuilder.build();
        Call call = okHttpClient.newCall(request);
        final OkHttpResponse res;
        int responseCode = -1;
        try {
            res = new OkHttpResponse(call, okHttpClient);
            responseCode = res.getStatusCode();
        } catch (IOException e) {
            throw new HttpRequestException(e.getMessage(), e, responseCode);
        }

        return res;
    }

    private RequestBody getRequestBody(HttpRequest req) {
        HttpParameter[] parameters = req.getParameters();
        boolean containsFile = HttpParameter.containsFile(parameters);
        if (containsFile) {
            final String fileBoundary = "----Http-Upload----" + System.currentTimeMillis();
            MultipartBody.Builder multipart = new MultipartBody.Builder(fileBoundary).setType(MultipartBody.FORM);
            for (HttpParameter parameter : parameters) {
                if (parameter.isFile()) {
                    Headers headers = Headers.of("Content-Disposition", "form-data; name=\"" + parameter.getName() + "\"; filename=\"" + parameter.getFile().getName() + "\"");
                    if (parameter.hasFileBody()) {
                        multipart.addPart(headers, HttpUtil.createRequestBodyFromInputStream(MediaType.parse(parameter.getContentType()), parameter.getFileBody()));
                    } else {
                        multipart.addPart(headers, RequestBody.create(parameter.getFile(), MediaType.parse(parameter.getContentType())));
                    }
                } else {
                    Headers headers = Headers.of("Content-Disposition", "form-data; name=\"" + parameter.getName() + "\"");
                    RequestBody requestBody = RequestBody.create(parameter.getValue().getBytes(StandardCharsets.UTF_8), TEXT);
                    multipart.addPart(headers, requestBody);
                }
            }
            return multipart.build();
        } else if (HttpParameter.containsJson(req.getParameters())) {
            String jsonStringFromParams = HttpUtil.createJsonStringFromParams(req.getParameters());
            return RequestBody.create(jsonStringFromParams, APPLICATION_JSON);
        } else {
            return RequestBody.create(HttpParameter.encodeParams(parameters), FORM_URL_ENCODED);
        }
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