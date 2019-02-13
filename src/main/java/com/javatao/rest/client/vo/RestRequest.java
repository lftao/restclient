package com.javatao.rest.client.vo;

import java.util.Map;

import org.apache.http.client.ResponseHandler;

public class RestRequest {
    private String url;
    private String method;
    private Boolean async = false;
    private String chareset = "UTF-8";
    private String requestBody;
    private String contentType = "application/json";
    /**
     * 回调方法
     */
    private Class<? extends ResponseHandler<Object>> callblack;
    /**
     * 回调构造方法传参数
     */
    private Object[] callConstructors;
    private String fileParams;
    private Class<?> responseClassType;
    private String responseSplitKey;
    private String responseType = "json";
    private Map<String, Object> header;
    private Map<String, Object> config;
    // 认证方式
    private String authorization;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public String getRequestBody() {
        if (requestBody == null) {
            return "{}";
        }
        return requestBody.trim();
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Class<? extends ResponseHandler<Object>> getCallblack() {
        return callblack;
    }

    public void setCallblack(Class<? extends ResponseHandler<Object>> callblack) {
        this.callblack = callblack;
    }

    public Object[] getCallConstructors() {
        return callConstructors;
    }

    public void setCallConstructors(Object[] callConstructors) {
        this.callConstructors = callConstructors;
    }

    public String getChareset() {
        return chareset;
    }

    public void setChareset(String chareset) {
        this.chareset = chareset;
    }

    public Class<?> getResponseClassType() {
        return responseClassType;
    }

    public void setResponseClassType(Class<?> responseClassType) {
        this.responseClassType = responseClassType;
    }

    public String getResponseSplitKey() {
        return responseSplitKey;
    }

    public void setResponseSplitKey(String responseSplitKey) {
        this.responseSplitKey = responseSplitKey;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getFileParams() {
        return fileParams;
    }

    public void setFileParams(String fileParams) {
        this.fileParams = fileParams;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, Object> getHeader() {
        return this.header;
    }

    public void setHeader(Map<String, Object> header) {
        this.header = header;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}