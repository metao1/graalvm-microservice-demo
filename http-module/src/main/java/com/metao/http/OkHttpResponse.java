package com.metao.http;

import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class OkHttpResponse extends HttpResponse {

    private final OkHttpClient okHttpClient;
    private final Call call;
    private final Response response;
    private InputStream is;
    private Map<String, List<String>> headerFields;

    public OkHttpResponse(Call call, OkHttpClient okHttpClient) throws IOException {
        super();
        this.okHttpClient = okHttpClient;
        this.call = call;
        this.response = call.execute();
        this.statusCode = response.code();
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
            is = body.byteStream();
            String compressHeaderValue = response.header("Content-Encoding");
            if (compressHeaderValue != null && compressHeaderValue.equals("gzip")) {
                is = new StreamingGZIPInputStream(is);
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
