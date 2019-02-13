package com.javatao.rest.client.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.javatao.rest.client.vo.RestRequest;

/**
 * REST请求模板调用
 * 
 * @author tao
 */
public abstract class RestUtils {
    private static final Log logger = LogFactory.getLog(RestUtils.class);
    private static final Integer connectTimeout = 1000 * 15;
    private static final Integer socketTimeout = 1000 * 60 * 20;

    @SuppressWarnings("unchecked")
    private static void addRestComm(Request request, RestRequest req, Map<String, Object> args) {
        request.connectTimeout(connectTimeout);
        request.userAgent("Mozilla");
        request.socketTimeout(socketTimeout);
        if (args.containsKey("header")) {
            Object object = args.get("header");
            if (object instanceof Map) {
                Map<String, String> mapHeader = (Map<String, String>) object;
                for (String o : mapHeader.keySet()) {
                    request.addHeader(o, mapHeader.get(o));
                }
            }
        }
        // 拓展参数
        Map<String, Object> config = req.getConfig();
        if (config != null) {
            Object ctime = config.get("connectTimeout");
            Object stime = config.get("socketTimeout");
            if (ctime != null) {
                request.connectTimeout(Integer.valueOf(ctime.toString()));
            }
            if (stime != null) {
                request.socketTimeout(Integer.valueOf(stime.toString()));
            }
        }
    }

    public static String doExe(String templatePathNames) {
        return doExe(templatePathNames, new HashMap<String, Object>(), new HashMap<String, String>());
    }

    public static String doExe(String templatePathNames, Map<String, Object> args, Map<String, String> responseHeader) {
        String s = FkUtils.processByPathName(templatePathNames, args);
        return doExeString(s, args, responseHeader);
    }
    
    public static String doExeString(String templateContent, Map<String, Object> args) {
        return doExeString(templateContent, args, new HashMap<String, String>());
    }
    
    @SuppressWarnings("unchecked")
    public static String doExeString(String templateContent, Map<String, Object> args, Map<String, String> responseHeader) {
        logger.info("### doExe args" + args);
        RestRequest req = JSON.parseObject(templateContent, RestRequest.class);
        try {
            String contentType = req.getContentType();
            if (contentType.contains("xml")) {
                logger.info("### doExe " + templateContent);
            } else {
                logger.info("### doExe " + jsonFormat(templateContent));
            }
            ResponseHandler<Object> handler = null;
            if (req.getCallblack() != null) {
                Constructor<?> constructor = req.getCallblack().getConstructors()[0];
                Object bk = null;
                if (req.getCallConstructors() != null) {
                    bk = constructor.newInstance(req.getCallConstructors());
                } else {
                    bk = constructor.newInstance();
                }
                if (bk instanceof ResponseHandler) {
                    handler = (ResponseHandler<Object>) bk;
                } else {
                    throw new RuntimeException(req.getCallblack() + " must implements ResponseHandler ");
                }
            }
            // 添加头信息
            Map<String, Object> header = req.getHeader();
            if (header != null) {
                Map<String, Object> hadr = (Map<String, Object>) args.get("header");
                if (hadr == null) {
                    hadr = new HashMap<>();
                }
                hadr.putAll(header);
                args.put("header", hadr);
            }
            String requestBody = req.getRequestBody();
            Map<?, ?> map = new HashMap<>();
            if (!requestBody.startsWith("[")) {
                map = JSON.parseObject(requestBody, Map.class);
            }
            if (map.containsKey("_xml") && map.size() == 1) {
                requestBody = map.values().iterator().next().toString();
            }
            Response response = null;
            String url = req.getUrl();
            String method = req.getMethod();
            if (method != null) {
                method = method.substring(0, 1).toUpperCase() + method.substring(1);
            }
            if ("Get".equalsIgnoreCase(method)) {
                if (isNotBlank(requestBody)) {
                    for (Object key : map.keySet()) {
                        String mapping = key + "=" + URLEncoder.encode(map.get(key).toString(), req.getChareset());
                        if (url.contains("?")) {
                            url = url.concat("&" + mapping);
                        } else {
                            url = url.concat("?" + mapping);
                        }
                    }
                }
                Request request = Request.Get(url);
                addRestComm(request, req, args);
                //response = request.execute();
                response =  HttpsUtils.exec(request, req);
            } else {
                if (contentType.contains("form")) {
                    NameValuePair[] data = new NameValuePair[map.size()];
                    int index = 0;
                    for (Object key : map.keySet()) {
                        Object val = map.get(key);
                        String value = URLEncoder.encode(val.toString(), "utf-8");
                        data[index] = new BasicNameValuePair(key.toString(), value);
                        index++;
                    }
                    Request request = Request.Post(req.getUrl()).bodyForm(data);
                    addRestComm(request, req, args);
                    //response = request.execute();
                    response =  HttpsUtils.exec(request, req);
                } else {
                    HttpEntity entity = null;
                    String fileParams = req.getFileParams();
                    if (isNotBlank(fileParams)) {
                        // 上传文件
                        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        for (String p : fileParams.split(",")) {
                            if (args.containsKey(p)) {
                                Object o = args.get(p);
                                if (o instanceof File) {
                                    FileBody fileBody = new FileBody((File) o, ContentType.APPLICATION_OCTET_STREAM);
                                    builder.addPart(p, fileBody);
                                }
                            }
                        }
                        for (Object key : map.keySet()) {
                            String ks = key.toString();
                            if (fileParams.contains(ks)) {
                                continue;
                            }
                            String val = map.get(key).toString();
                            if (isNotBlank(val) && val.contains("{")) {
                                StringBody sb = new StringBody(val, ContentType.APPLICATION_JSON);
                                builder.addPart(ks, sb);
                            } else {
                                builder.addTextBody(ks, val);
                            }
                        }
                        entity = builder.build();
                    } else {
                        entity = new StringEntity(requestBody, ContentType.create(contentType, req.getChareset()));
                    }
                    Request request = null;
                    Method methodPoxy = Request.class.getMethod(method, String.class);
                    Object invoke = methodPoxy.invoke(null, url);
                    if (invoke instanceof Request) {
                        request = (Request) invoke;
                        long length = entity.getContentLength();
                        if (length > 2) {
                            request.body(entity);
                        }
                    }
                    if (request == null) {
                        throw new RuntimeException("method is not support  ");
                    }
                    addRestComm(request, req, args);
                    //response = request.execute();
                    response =  HttpsUtils.exec(request, req);
                }
            }
            if (response == null) {
                throw new RuntimeException("response is null  ");
            }
            if (!req.getAsync()) {
                // String result = response.returnContent().asString(Charset.forName(req.getChareset()));
                HttpResponse returnResponse = response.returnResponse();
                logger.info("HttpResponse status:" + returnResponse.getStatusLine());
                // 返回头信息
                responseHeader.putAll(getResponseHeader(returnResponse));
                HttpEntity entity = returnResponse.getEntity();
                if(entity==null){
                    return null;
                }
                String result = EntityUtils.toString(entity, req.getChareset());
                logger.info(result);
                String splitKey = req.getResponseSplitKey();
                if (isNotBlank(splitKey)) {
                    Map<?, ?> parseObject = JSON.parseObject(result, Map.class);
                    Object obj = result;
                    for (String st : splitKey.split("[.]")) {
                        obj = parseObject.get(st);
                    }
                    result = JSON.toJSONString(obj);
                    logger.info(result);
                    return result;
                }
                return result;
            } else {
                if (handler != null) {
                    response.handleResponse(handler);
                } else {
                    HttpResponse returnResponse = response.returnResponse();
                    logger.info(returnResponse.getStatusLine());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static boolean isNotBlank(String requestBody) {
        if (requestBody == null || "".equals(requestBody.trim())) {
            return false;
        }
        return true;
    }

    /**
     * 添加返回头
     * 
     * @param returnResponse
     *            返回体
     * @return headers Map
     */
    private static Map<String, String> getResponseHeader(HttpResponse returnResponse) {
        Map<String, String> responseHeader = new HashMap<>();
        Header[] headers = returnResponse.getAllHeaders();
        if (headers != null) {
            for (Header hdr : headers) {
                String value = hdr.getValue();
                String name = hdr.getName();
                responseHeader.put(name, value);
            }
        }
        return responseHeader;
    }

    /**
     * 得到格式化json数据
     * 
     * @param jsonStr
     *            字符串
     * @return 格式化后字符
     */
    public static String jsonFormat(String jsonStr) {
        int level = 0;
        StringBuilder jsonForMatStr = new StringBuilder();
        jsonStr = jsonStr.replaceAll("\n|\t|\r| ", "").replace(",]", "]").replace(",}", "}").trim();
        int size = jsonStr.length();
        for (int i = 0; i < size; i++) {
            char c = jsonStr.charAt(i);
            char n = ' ';
            if (i < size - 1) {
                n = jsonStr.charAt(i + 1);
            }
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    if (n == '[' || n == '{') {
                        jsonForMatStr.append(c);
                    } else {
                        jsonForMatStr.append(c + "\n");
                    }
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c);
                    if (n != '{' && n != '[') {
                        jsonForMatStr.append("\n");
                    }
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }
        return jsonForMatStr.toString();
    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }
}