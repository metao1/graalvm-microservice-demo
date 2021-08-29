package com.metao.http.service;

import com.metao.http.model.request.HttpRequest;

public interface Authorization {

    String getAuthorizationHeader(HttpRequest req);

    boolean isAuthorized();
}
