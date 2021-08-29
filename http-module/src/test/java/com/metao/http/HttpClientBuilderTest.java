package com.metao.http;

import com.metao.http.exception.HttpRequestException;
import com.metao.http.model.http.HttpParameter;
import com.metao.http.model.response.HttpResponse;
import com.metao.http.model.http.JsonObject;
import com.metao.http.service.HttpClientAgent;
import com.metao.http.service.HttpClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class HttpClientBuilderTest {

    @Test
    void authorization() {
    }

    @Test
    void onlyGet() throws HttpRequestException {
        HttpClientAgent clientAgent = new HttpClientBuilder()
                .build();
        HttpResponse httpResponse = clientAgent.get("https://google.com");
        assertEquals(200, httpResponse.getStatusCode());
    }

    @Test
    void getWithParam() throws HttpRequestException {
        HttpClientAgent clientAgent = new HttpClientBuilder()
                .build();
        HttpResponse httpResponse = clientAgent.get("https://api.twitter.com/1/statuses/oembed.json", new HttpParameter[]{new HttpParameter("id", "1424535306225831944")}, null, null);
        assertEquals(200, httpResponse.getStatusCode());
        assertNotNull(httpResponse.getResponseAsString());
        log.info(httpResponse.getResponseAsString());
    }

    @Test
    void getJsonObject() throws HttpRequestException {
        HttpClientAgent clientAgent = new HttpClientBuilder()
                .build();
        HttpResponse httpResponse = clientAgent.get("https://api.twitter.com/1/statuses/oembed.json", new HttpParameter[]{new HttpParameter("id", "1424535306225831944")}, null, null);
        assertEquals(200, httpResponse.getStatusCode());
        assertNotNull(httpResponse.asJsonObject());
        JsonObject jsonObject = httpResponse.asJsonObject();
        log.info(jsonObject.toString());
    }
}