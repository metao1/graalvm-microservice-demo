package com.metao.http;

public interface Authorization {

    String getAuthorizationHeader(HttpRequest req);

    boolean isAuthorized();
}
