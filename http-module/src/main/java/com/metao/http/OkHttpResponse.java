package com.metao.http;

import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class OkHttpResponse extends HttpResponse {

    private final OkHttpClient okHttpClient;
    private final Call call;
    private final Response response;
    private Map<String, List<String>> headerFields;

    public OkHttpResponse(Call call, OkHttpClient okHttpClient) throws IOException {
        super();
        this.headerFields = new HashMap<>();
        this.okHttpClient = okHttpClient;
        this.call = call;
        this.response = call.execute();
        this.statusCode = response.code();
        this.protocol = response.protocol();
        storeResponseHeaders();
        storeResponseBody();
    }

    @Override
    protected String getHeader(String name) {
        return response.header(name);
    }

    @Override
    protected void storeResponseBody() {
        ResponseBody body = response.body();
        if (body != null) {
            stream = body.byteStream();
            String compressHeaderValue = response.header("Content-Encoding");
            if (compressHeaderValue != null && compressHeaderValue.equals("gzip")) {
                stream = new StreamingGZIPInputStream(stream);
            }
        }
    }

    @Override
    protected void storeResponseHeaders() {
        Headers headers = response.headers();
        for (String name : headers.names()) {
            headerFields.put(name, headers.values(name));
        }
    }
}
