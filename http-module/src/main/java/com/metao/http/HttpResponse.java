package com.metao.http;

import okhttp3.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class HttpResponse {

    protected int statusCode;
    protected InputStream stream;
    private String responseAsString;
    protected Protocol protocol;
    private JsonObject jsonObject;
    private boolean streamConsumed;

    protected abstract String getHeader(String name);

    protected abstract void storeResponseHeaders();

    protected abstract void storeResponseBody();

    public int getStatusCode() {
        return statusCode;
    }

    public String asString() throws HttpRequestException {
        if (responseAsString == null) {
            InputStream inputStream = asStream();
            if (inputStream == null) {
                return null;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                this.responseAsString = sb.toString();
                this.streamConsumed = true;
            } catch (IOException e) {
                throw new HttpRequestException(e.getMessage(), e, statusCode);
            }
        }
        return responseAsString;
    }

    public String getResponseAsString() throws HttpRequestException {
        return asString();
    }

    public InputStream asStream() {
        checkStreamStatus();
        return stream;
    }

    public JsonObject asJsonObject() throws HttpRequestException {
        if (jsonObject == null) {
            try {
                jsonObject = new JsonObject(asString());
            } catch (JsonException | HttpRequestException e) {
                if (responseAsString == null) {
                    throw new HttpRequestException(e.getMessage(), e);
                } else {
                    throw new HttpRequestException(e.getMessage() + ":" + responseAsString, e);
                }
            }
        }
        return jsonObject;
    }

    public void checkStreamStatus() {
        if (streamConsumed) {
            throw new IllegalStateException("stream already consumed for this http response.");
        }
    }
}
