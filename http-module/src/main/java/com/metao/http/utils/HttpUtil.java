package com.metao.http.utils;

import com.metao.http.model.http.HttpParameter;
import com.metao.http.exception.JsonException;
import com.metao.http.model.http.JsonObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class HttpUtil {

    public static RequestBody createRequestBodyFromInputStream(MediaType mediaType, InputStream fileBody) {
        return new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public long contentLength() {
                try {
                    return fileBody.available();
                } catch (IOException ex) {
                    return 0;
                }
            }

            @Override
            public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                bufferedSink.writeAll(Okio.source(fileBody));
            }
        };
    }

    public static String checkNameNullability(String name) {
        if (name == null || name.length() == 0) {
            throw new JsonException("name must be non-null not-empty value");
        }
        return name;
    }

    public static String createJsonStringFromParams(HttpParameter[] parameters) {
        StringBuilder sb = new StringBuilder();
        for (HttpParameter param : parameters) {
            JsonObject jsonObject = param.getJsonObject();
            jsonObject.put(param.getName(), param.getValue());
            sb.append(jsonObject);
        }
        return sb.toString();
    }
}
