package com.metao.http.service;

import com.metao.http.model.response.ResponseEvent;

public interface HttpResponseListener {
    void onResponse(ResponseEvent resEvent);
}
