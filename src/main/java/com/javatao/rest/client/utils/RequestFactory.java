package com.javatao.rest.client.utils;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpDelete;

/**
 * 请求工厂-拓展
 * 
 * @author tao
 */
public class RequestFactory {
    public static Request Get(final URI uri) {
        return Request.Get(uri);
    }

    public static Request Get(final String uri) {
        return Request.Get(uri);
    }

    public static Request Head(final URI uri) {
        return Request.Head(uri);
    }

    public static Request Head(final String uri) {
        return Request.Head(uri);
    }

    public static Request Post(final URI uri) {
        return Request.Post(uri);
    }

    public static Request Post(final String uri) {
        return Request.Post(uri);
    }

    public static Request Patch(final URI uri) {
        return Request.Patch(uri);
    }

    public static Request Patch(final String uri) {
        return Request.Patch(uri);
    }

    public static Request Put(final URI uri) {
        return Request.Put(uri);
    }

    public static Request Put(final String uri) {
        return Request.Put(uri);
    }

    public static Request Trace(final URI uri) {
        return Request.Trace(uri);
    }

    public static Request Trace(final String uri) {
        return Request.Trace(uri);
    }

    public static Request Delete(final URI uri) {
        try {
            // 反射实体对象
            Class<?> internalEntityEnclosingHttpRequest = Class.forName("org.apache.http.client.fluent.InternalEntityEnclosingHttpRequest");
            Constructor<?> httpRequest = internalEntityEnclosingHttpRequest.getConstructor(String.class, URI.class);
            httpRequest.setAccessible(true);
            Object instance = httpRequest.newInstance(HttpDelete.METHOD_NAME, uri);
            // 包装请求对象
            Constructor<?>[] constructors = Request.class.getDeclaredConstructors();
            Constructor<?> constructor = constructors[0];
            constructor.setAccessible(true);
            return (Request) constructor.newInstance(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Request Delete(final String uri) {
        try {
            return Delete(new URI(uri));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Request Options(final URI uri) {
        return Request.Options(uri);
    }

    public static Request Options(final String uri) {
        return Request.Options(uri);
    }
}
