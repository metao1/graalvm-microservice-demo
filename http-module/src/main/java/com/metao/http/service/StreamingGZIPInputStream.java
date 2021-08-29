package com.metao.http.service;

import java.io.IOException;
import java.io.InputStream;

public class StreamingGZIPInputStream extends InputStream {

    private final InputStream is;

    public StreamingGZIPInputStream(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        return is.available();
    }
}
