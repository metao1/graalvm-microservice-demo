package com.metao.http;

import java.util.List;
import java.util.Map;

public abstract class HttpResponse {

    protected int statusCode;
    protected String responseAsString;

    protected abstract String getHeader(String name);

    protected abstract Map<String, List<String>> getResponseHeadersMap();

}
