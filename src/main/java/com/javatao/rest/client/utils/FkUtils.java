package com.javatao.rest.client.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * 模板字符处理
 * 
 * @author tao
 */
public final class FkUtils {
    private final static String chareSet = "utf-8";

    private FkUtils() {}

    private static final Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
    /**
     * 字符串缓存
     */
    private static StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
    static {
        try {
            configuration.setClassForTemplateLoading(FkUtils.class, "/");
            configuration.setNumberFormat("###");
            configuration.setLocale(Locale.CHINESE);
            configuration.setLocalizedLookup(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加共用文件
     * 
     * @param include
     *            dir
     */
    public static void include(String include) {
        try {
            Class<?> class1 = FkUtils.class;
            String path = include + "/include/";
            URI url = class1.getResource(path).toURI();
            File fileInclude = new File(url);
            if (fileInclude.exists()) {
                for (File file : fileInclude.listFiles()) {
                    configuration.addAutoInclude(path + file.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加共享变量
     * 
     * @param params
     *            变量
     */
    public static void addSharedVariable(Map<String, Object> params) {
        if (params != null) {
            for (Entry<String, Object> entry : params.entrySet()) {
                try {
                    configuration.setSharedVariable(entry.getKey(), entry.getValue());
                } catch (TemplateModelException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 模板处理
     * 
     * @param template
     *            模板路径
     * @param args
     *            参数
     * @return
     */
    private static String process(Template template, Map<String, Object> args) {
        if (args == null) {
            args = new HashMap<String, Object>();
        }
        StringWriter stringWriter = new StringWriter();
        try {
            template.process(args, stringWriter);
            String rs = stringWriter.toString();
            return rs;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stringWriter.flush();
                stringWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isBlank(String dir) {
        if (dir == null || "".equals(dir.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 调用用模板明
     * 
     * @param templatePathName
     *            模板路径
     * @return 内容
     */
    public static String processByPathName(String templatePathName) {
        return processByPathName(templatePathName, null);
    }

    /**
     * 调用用模板明
     * 
     * @param templatePathName
     *            模板名字
     * @param args
     *            参数
     * @return 内容
     */
    public static String processByPathName(String templatePathName, Map<String, Object> args) {
        try {
            Template template = configuration.getTemplate(templatePathName, chareSet);
            return process(template, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param templateString
     *            模板字符串
     * @param args
     *            参数
     * @return 内容
     */
    public static String process(String templateString, Map<String, Object> args) {
        try {
            if (templateString == null) {
                return null;
            }
            String key = "template_" + templateString.hashCode();
            if (stringTemplateLoader.findTemplateSource(key) == null) {
                stringTemplateLoader.putTemplate(key, templateString);
            }
            Template mytpl = configuration.getTemplate(key, chareSet);
            return process(mytpl, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置全局参数
     * 
     * @param name
     *            名字
     * @param value
     *            值
     * @param env
     *            环境变量
     * @throws TemplateException
     *             TemplateException
     */
    public static void setVariable(String name, Object value, Environment env) throws TemplateException {
        if (value instanceof TemplateModel) {
            env.setVariable(name, (TemplateModel) value);
        } else {
            env.setVariable(name, configuration.getObjectWrapper().wrap(value));
        }
    }

    /**
     * 拿到静态Class的Model
     * 
     * @param className
     *            类路径
     * @return TemplateModel
     * @throws TemplateModelException
     *             TemplateException
     */
    public static TemplateModel useClass(String className) throws TemplateModelException {
        BeansWrapper wrapper = (BeansWrapper) configuration.getObjectWrapper();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        return staticModels.get(className);
    }

    /**
     * 拿到目标对象的model
     * 
     * @param target
     *            目标
     * @return TemplateModel
     * @throws TemplateModelException
     *             TemplateModelException
     */
    public static TemplateModel useObjectModel(Object target) throws TemplateModelException {
        ObjectWrapper wrapper = configuration.getObjectWrapper();
        TemplateModel model = wrapper.wrap(target);
        return model;
    }

    /**
     * 拿到目标对象某个方法的Model
     * 
     * @param target
     *            目标
     * @param methodName
     *            方法名
     * @return TemplateModel
     * @throws TemplateModelException
     *             TemplateModelException
     */
    public static TemplateModel useObjectMethod(Object target, String methodName) throws TemplateModelException {
        TemplateHashModel model = (TemplateHashModel) useObjectModel(target);
        return model.get(methodName);
    }
}
