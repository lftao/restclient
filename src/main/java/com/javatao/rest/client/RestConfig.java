package com.javatao.rest.client;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import com.javatao.rest.client.proxy.DoInterceptors;
import com.javatao.rest.client.proxy.IfaceProxy;

/**
 * @author TLF
 */
public class RestConfig {
    /**
     * basedir : default /
     * 初始化参数
     * 
     * @param apiClass
     *            接口类
     */
    public static void init(Class<?> apiClass) {
        init(apiClass, "/", null);
    }

    /**
     * 获取实例
     * 
     * @param apiClass
     *            接口类
     * @param <T>
     *            接口类泛型
     * @return T 实例
     */
    public static <T> T getBean(Class<T> apiClass) {
        return getBean(apiClass, "/");
    }

    /**
     * 获取实例
     * 
     * @param apiClass
     *            接口类
     * @param basedir
     *            模板路径
     * @param <T>
     *            接口类泛型
     * @return T 实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> apiClass, String basedir) {
        if (apiClass.isInterface()) {
            return (T) Proxy.newProxyInstance(RestConfig.class.getClassLoader(), new Class<?>[] { apiClass }, new IfaceProxy(basedir, null));
        } else {
            throw new RuntimeException(apiClass.getName() + "is not isInterface");
        }
    }

    /**
     * 初始化参数 ;
     * 
     * @param apiClass
     *            接口类
     * @param basedir
     *            模板目录
     */
    public static void init(Class<?> apiClass, String basedir) {
        init(apiClass, basedir, null);
    }

    /**
     * 初始化参数 ;
     * 
     * @param apiClass
     *            接口类
     * @param basedir
     *            模板目录
     * @param instance
     *            实例对象
     */
    public static void init(Class<?> apiClass, String basedir, Object instance) {
        init(instance.getClass(), basedir, instance, null);
    }

    /**
     * 初始化参数 ;
     * 
     * @param apiClass
     *            接口类
     * @param basedir
     *            模板目录
     * @param interceptors
     *            接口
     */
    public static void init(Class<?> apiClass, String basedir, DoInterceptors interceptors) {
        init(apiClass, basedir, null, interceptors);
    }

    /**
     * 初始化参数 ;
     * 
     * @param basedir
     *            模板目录
     * @param instance
     *            实例对象
     * @param interceptors
     *            接口
     */
    public static void init(Object instance, String basedir, DoInterceptors interceptors) {
        init(instance.getClass(), basedir, instance, interceptors);
    }

    /**
     * 初始化参数 ;
     * 
     * @param apiClass
     *            接口类
     * @param basedir
     *            模板目录
     * @param instance
     *            实例对象
     * @param interceptors
     *            接口
     */
    public static void init(Class<?> apiClass, String basedir, Object instance, DoInterceptors interceptors) {
        Field[] fields = apiClass.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            if (type.isInterface()) {
                try {
                    /** 初始化代理类 */
                    IfaceProxy ifaceProxy = new IfaceProxy(basedir, interceptors);
                    Object proxy = Proxy.newProxyInstance(RestConfig.class.getClassLoader(), new Class<?>[] { type }, ifaceProxy);
                    field.setAccessible(true);
                    field.set(instance, proxy);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
