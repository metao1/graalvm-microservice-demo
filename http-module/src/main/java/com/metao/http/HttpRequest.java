package com.metao.http;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
public final class HttpRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 4723681292312310L;
    private final String url;
    private final RequestMethod requestMethod;
    private final HttpParameter[] parameters;
    private final Map<String, String> requestHeaders;
    private final Authorization authorization;

    public HttpRequest(String url, RequestMethod requestMethod, HttpParameter[] parameters, Map<String, String> requestHeaders, Authorization authorization) {
        this.requestMethod = requestMethod;
        if (this.requestMethod != RequestMethod.POST && parameters != null && parameters.length > 0) {
            this.url = url.concat("?").concat(HttpParameter.encodeParams(parameters));
            this.parameters = new HttpParameter[0];
        } else {
            this.url = url;
            this.parameters = parameters;
        }
        this.requestHeaders = requestHeaders;
        this.authorization = authorization;
    }
}
