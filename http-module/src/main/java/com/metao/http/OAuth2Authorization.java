package com.metao.http;

public class OAuth2Authorization implements Authorization{

    @Override
    public String getAuthorizationHeader(HttpRequest req) {
        return null;
    }

    @Override
    public boolean isAuthorized() {
        return false;
    }
}
