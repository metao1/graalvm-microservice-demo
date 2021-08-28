package com.metao.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        HttpResponse httpResponse = clientAgent.get("https://api.twitter.com/1/statuses/oembed.json", new HttpParameter[]{new HttpParameter("id", "1424535306225831944")}, null, null);
        assertEquals(200, httpResponse.statusCode);
        assertNotNull(httpResponse.responseAsString);
    }
}