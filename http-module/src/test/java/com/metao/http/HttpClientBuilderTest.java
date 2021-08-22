package com.metao.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpClientBuilderTest {

    @Test
    void authorization() {
    }

    @Test
    void onlyGet() {
        HttpClientAgent clientAgent = new HttpClientBuilder()
                .build();
        HttpResponse httpResponse = clientAgent.get("https://google.com");
        assertEquals(200, httpResponse.statusCode);
    }

    @Test
    void getWithParam() {
        HttpClientAgent clientAgent = new HttpClientBuilder()
                .build();
        HttpResponse httpResponse = clientAgent.get("https://www.google.com/search", new HttpParameter[]{new HttpParameter("q", "test")}, null, null);
        assertEquals(200, httpResponse.statusCode);
    }
}