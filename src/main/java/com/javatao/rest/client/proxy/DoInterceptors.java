package com.javatao.rest.client.proxy;

import java.lang.reflect.Method;
import java.util.Map;

public interface DoInterceptors {
    /**
     * 调用之前参数封装
     * @param proxy
     *            代理对象
     * @param method
     *            代理方法
     * @param datas
     *            参数
     * @return Object 不为空直接返回
     */
    Object before(Object proxy, Method method,Map<String, Object> datas);

    /**
     * 返回结果
     * @param proxy
     *            代理对象
     * @param method
     *            代理方法
     * @param resutl
     *            结果
     * @param headers
     *            头信息
     * @return 对象
     */
    Object after(Object proxy, Method method,String resutl, Map<String, String> headers);

    /**
     * 返回结果
     * 
     * @param result
     *            返回结果
     * @param proxy
     *            代理对象
     * @param method
     *            代理方法
     * @param args
     *            代理参数
     * @return 返回结果
     */
    Object finalReturn(Object result, Object proxy, Method method, Object[] args);
}
