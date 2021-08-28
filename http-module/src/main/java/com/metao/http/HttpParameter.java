package com.metao.http;

import lombok.Data;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
public class HttpParameter {
    private String name;
    private String value;
    private JsonObject jsonObject;
    private File file;
    private InputStream fileBody;
    private String contentType;

    public HttpParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public HttpParameter(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public HttpParameter(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public static String encodeParams(HttpParameter[] parameters) {
        if (parameters == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder(parameters.length * 2);
        for (int i = 0; i < parameters.length; i++) {
            if (i != 0) {
                stringBuilder.append("&");
            }
            String name = encode(parameters[i].name);
            String value = encode(parameters[i].value);
            stringBuilder.append(name).append("=").append(value);
        }
        return stringBuilder.toString();
    }

    public static boolean containsFile(HttpParameter[] parameters) {
        if (parameters == null) {
            return false;
        }
        for (HttpParameter parameter : parameters) {
            if (parameter.isFile()) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsJson(HttpParameter[] parameters) {
        for (HttpParameter parameter : parameters) {
            return parameter.getJsonObject() != null;
        }
        return false;
    }

    public boolean isFile() {
        return file != null;
    }

    public boolean hasFileBody() {
        return fileBody != null;
    }

    private static String encode(final String val) {
        String encodedVal = URLEncoder.encode(val, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder(encodedVal.length());
        for (int i = 0; i < encodedVal.length(); i++) {
            char c = encodedVal.charAt(i);
            switch (c) {
                case '*' -> stringBuilder.append("%2A");
                case '+' -> stringBuilder.append("%20");
                default -> stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

}
