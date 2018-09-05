package com.javatao.rest.client.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ApiResponse implements Serializable {
    private static final long serialVersionUID = 4876224394091925143L;
    private String body;
    private Map<String, String> header = new HashMap<>();

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}