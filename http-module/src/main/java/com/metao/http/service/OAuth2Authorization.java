package com.metao.http.service;

import com.metao.http.model.request.HttpRequest;

public class OAuth2Authorization implements Authorization {

    @Override
    public String getAuthorizationHeader(HttpRequest req) {
        return null;
    }

    @Override
    public boolean isAuthorized() {
        return false;
    }
}
