package com.javatao.rest.client.proxy;

import java.util.Map;

public interface DoInterceptors {
    /**
     * 调用之前参数封装
     * 
     * @param datas
     *            参数
     * @return Object 不为空直接返回
     */
    Object before(Map<String, Object> datas);

    /**
     * 返回结果
     * 
     * @param resutl
     *            结果
     * @param header
     *            头信息
     * @return 对象
     */
    Object after(String resutl, Map<String, String> headers);
    
    /**
     * 返回结果
     * 
     * @param object
     *            结果
     * @return 对象
     */
    Object finalReturn(Object object);
}
