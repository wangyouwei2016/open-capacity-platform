package com.open.capacity.ext.mvc;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.IOException;
import java.io.InputStream;

public class DecHttpInputMessage implements HttpInputMessage {
    private HttpInputMessage httpInputMessage;
    private InputStream bodyInputStream;

    public DecHttpInputMessage(HttpInputMessage httpInputMessage, InputStream bodyInputStream) {
        this.httpInputMessage = httpInputMessage;
        this.bodyInputStream = bodyInputStream;
    }

    @Override
    public InputStream getBody() throws IOException {
        return bodyInputStream;
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpInputMessage.getHeaders();
    }
}
