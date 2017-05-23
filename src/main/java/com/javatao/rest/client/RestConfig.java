package com.javatao.rest.client;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import com.javatao.rest.client.proxy.IfaceProxy;
import com.javatao.rest.client.utils.FkUtils;

/**
 * @author TLF
 */
public class RestConfig {
    /**
     * 初始化参数 <br/>
     * basedir : default /
     * 
     * @return
     */
    public static void init(Class<?> apiClass) {
        init(apiClass, "/");
    }


    /**
     * 初始化参数 ;
     * 
     * @return
     */
    public static void init(Class<?> apiClass, String basedir) {
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
                field.set(null, proxy);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        // 添加通用文件
        FkUtils.include(basedir);
    }
}
