package com.javatao.rest.client.proxy;

import java.util.Map;

public class DoAdapter implements DoInterceptors {
    @Override
    public Object before(Map<String, Object> datas) {
        return null;
    }

    @Override
    public Object after(String resutl, Map<String, String> headers) {
        return null;
    }

    @Override
    public Object finalReturn(Object object) {
        return object;
    }
}