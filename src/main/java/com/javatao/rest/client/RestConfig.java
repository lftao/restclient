package com.javatao.rest.client;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

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
        init(apiClass, "/",null);
    }

    /**
     * 获取实例
     * 
     * @param apiClass
     *            接口类
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
     * @return T 实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> apiClass, String basedir) {
        if (apiClass.isInterface()) {
            return (T) Proxy.newProxyInstance(RestConfig.class.getClassLoader(), new Class<?>[] { apiClass }, new IfaceProxy(basedir));
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
    public static void init(Class<?> apiClass, String basedir,Object instance) {
        Field[] fields = apiClass.getDeclaredFields();
        Set<Class<?>> ifacesClass = new HashSet<>();
        Set<Field> fieldIface = new HashSet<>();
        for (Field field : fields) {
            Class<?> type = field.getType();
            if (type.isInterface()) {
                ifacesClass.add(type);
                fieldIface.add(field);
            }
        }
        /**
         * 初始化代理类
         */
        Object proxy = Proxy.newProxyInstance(RestConfig.class.getClassLoader(), ifacesClass.toArray(new Class<?>[] {}), new IfaceProxy(basedir));
        for (Field field : fieldIface) {
            try {
                field.setAccessible(true);
                field.set(instance, proxy);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}