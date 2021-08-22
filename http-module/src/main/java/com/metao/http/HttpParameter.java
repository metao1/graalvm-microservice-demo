package com.metao.http;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpParameter {
    private final String name;
    private final String value;

    public HttpParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static String encodeParams(HttpParameter[] parameters) {
        if (parameters == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
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

    private static String encode(final String val) {
        String encodedVal = URLEncoder.encode(val, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder(encodedVal);
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
