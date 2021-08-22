package com.metao.http;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OkHttpResponse extends HttpResponse {

    private final OkHttpClient okHttpClient;
    private final Call call;
    private final Response response;

    public OkHttpResponse(Call call, OkHttpClient okHttpClient) throws IOException {
        super();
        this.okHttpClient = okHttpClient;
        this.call = call;
        this.response = call.execute();
        this.statusCode = response.code();
    }

    @Override
    protected String getHeader(String name) {
        return null;
    }

    @Override
    protected Map<String, List<String>> getResponseHeadersMap() {
        return null;
    }
}
