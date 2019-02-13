package com.javatao.rest.client.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import com.javatao.rest.client.vo.RestRequest;

public class HttpsUtils {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static CloseableHttpClient client;
    static {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" }, null,
                    NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register(HTTP, new PlainConnectionSocketFactory()).register(HTTPS, sslsf).build();
            PoolingHttpClientConnectionManager CONNMGR = new PoolingHttpClientConnectionManager(registry);
            CONNMGR.setMaxTotal(200);
            CONNMGR.setDefaultMaxPerRoute(100);
            CONNMGR.setValidateAfterInactivity(1000);
            client = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(CONNMGR).setConnectionManagerShared(true).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 -CloseableHttpClient
     * 
     * @return CloseableHttpClient
     */
    public static CloseableHttpClient getClient() {
        return client;
    }

    /**
     * 执行请求
     * @param request HTTP 请求
     * @param req 参数
     * @return 结果
     * @throws Exception 异常
     */
    public static Response exec(Request request, RestRequest req) throws Exception {
        Executor executor = Executor.newInstance(client);
        String authorization = req.getAuthorization();
        if (authorization != null && authorization.length() > 0) {
            String[] aus = authorization.trim().split(" ");
            // 暂时只解析基本认证
            if ("Basic".equals(aus[0])) {
                String base64 = aus[1];
                String accountString = new String(Base64.decodeBase64(base64.getBytes()));
                String account[] = accountString.split(":");
                if (account.length == 2) {
                    executor = executor.auth(account[0], account[1]);
                }
            }
        }
        return executor.execute(request);
    }
}
