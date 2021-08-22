package com.metao.http;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClientBuilder {

    private Authorization authorization;

    public HttpClientBuilder() {

    }

    public HttpClientBuilder authorization(Authorization authorization) {
        this.authorization = authorization;
        return this;
    }

    public HttpClientAgent build() {
        return new HttpClientAgent(this.authorization);
    }
}
