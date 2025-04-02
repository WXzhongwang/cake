package com.rany.framework.config.utils;

import com.google.common.collect.Sets;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 16:56
 * @slogon 找到银弹
 */
public class HttpUtils {

    public static final Set<Integer> DEFAULT_GET_acceptStatus = Sets.newHashSet(200);
    public static final Set<Integer> DEFAULT_POST_acceptStatus = Sets.newHashSet(200);
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
    private final static CloseableHttpClient httpClient;
    private static final int MAX_TIMEOUT = 20000;
    private static final RequestConfig requestConfig;

    static {
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
        ConnectionSocketFactory plainSF = new PlainConnectionSocketFactory();
        registryBuilder.register("http", plainSF);
        // 指定信任密钥存储对象和连接套接字工厂
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // 信任任何链接
            TrustStrategy anyTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            };
            SSLContext sslContext = SSLContexts.custom().useProtocol("TLS")
                    .loadTrustMaterial(trustStore, anyTrustStrategy).build();

            LayeredConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslContext,
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            registryBuilder.register("https", sslSF);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Registry<ConnectionSocketFactory> registry = registryBuilder.build();
        // 设置连接管理器
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        // 设置连接池大小

        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(connManager.getMaxTotal());
        // connManager.setValidateAfterInactivity(true);
        // connManager.setDefaultConnectionConfig(connConfig);
        // connManager.setDefaultSocketConfig(socketConfig);

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        requestConfig = configBuilder.build();
        // 构建客户端
        httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();
    }

    /**
     * @return the httpClient
     */
    public static CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static String doGet(String url) {
        return doGet(url, Collections.emptyMap(), Collections.emptyMap(), DEFAULT_GET_acceptStatus);
    }

    public static String doGet(String url, Map<String, String> headers) {
        return doGet(url, Collections.emptyMap(), headers, DEFAULT_GET_acceptStatus);
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, Object> params, Map<String, String> headers,
                               Set<Integer> acceptStatus) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0)
                param.append("?");
            else
                param.append("&");
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = getHttpClient();
        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            for (Map.Entry<String, String> kv : headers.entrySet()) {
                httpGet.addHeader(kv.getKey(), kv.getValue());
            }
            httpGet.setConfig(requestConfig);
            response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (!acceptStatus.contains(statusCode)) {
                throw new HttpException("error http code:" + statusCode, statusCode);
            } else {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (IOException e) {
            throw new HttpException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LOG.error(apiUrl, e);
                }
            }
        }
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     *
     * @param apiUrl
     * @return
     */
    public static String doPost(String apiUrl) {
        return doPost(apiUrl, Collections.emptyMap());
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPost(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = getHttpClient();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            LOG.error(apiUrl, e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LOG.error(apiUrl, e);
                }
            }
        }
        return httpStr;
    }

    public static String doPost(String apiUrl, Map<String, Object> params, Set<Integer> acceptStatus) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (!acceptStatus.contains(statusCode)) {
                throw new HttpException("error http code:" + statusCode, statusCode);
            } else {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            throw new HttpException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LOG.error(apiUrl, e);
                }
            }
        }
    }

    public static String doPost(String apiUrl, Map<String, String> headers, byte[] body, Set<Integer> acceptStatus) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new ByteArrayEntity(body));

            for (Map.Entry<String, String> kv : headers.entrySet()) {
                httpPost.addHeader(kv.getKey(), kv.getValue());
            }
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (!acceptStatus.contains(statusCode)) {
                throw new HttpException("error http code:" + statusCode, statusCode);
            } else {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            throw new HttpException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LOG.error(apiUrl, e);
                }
            }
        }
    }

    public static String doPost(String apiUrl, Map<String, String> headers, String body, Set<Integer> acceptStatus) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new StringEntity(body, "UTF-8"));

            for (Map.Entry<String, String> kv : headers.entrySet()) {
                httpPost.addHeader(kv.getKey(), kv.getValue());
            }
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (!acceptStatus.contains(statusCode)) {
                throw new HttpException("error http code:" + statusCode, statusCode);
            } else {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            throw new HttpException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LOG.error(apiUrl, e);
                }
            }
        }
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param apiUrl
     * @param json   json对象
     * @return
     */
    public static String doPost(String apiUrl, Object json) {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");// 解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            if (!DEFAULT_POST_acceptStatus.contains(statusCode)) {
                throw new HttpException("error http code:" + statusCode, statusCode);
            } else {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            throw new HttpException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LOG.error(apiUrl, e);
                }
            }
        }
    }

    public static String doDelete(String url, Map<String, Object> params, Map<String, String> headers,
                                  Set<Integer> acceptStatus) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0)
                param.append("?");
            else
                param.append("&");
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = getHttpClient();
        try {
            HttpDelete httpDelete = new HttpDelete(apiUrl);
            for (Map.Entry<String, String> kv : headers.entrySet()) {
                httpDelete.addHeader(kv.getKey(), kv.getValue());
            }
            httpDelete.setConfig(requestConfig);
            response = httpclient.execute(httpDelete);
            int statusCode = response.getStatusLine().getStatusCode();
            if (!acceptStatus.contains(statusCode)) {
                throw new HttpException("error http code:" + statusCode, statusCode);
            } else {
                if (response.getEntity() == null) {
                    return null;
                }
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (IOException e) {
            throw new HttpException(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LOG.error(apiUrl, e);
                }
            }
        }
    }

    public static class HttpException extends RuntimeException {

        /**
         *
         */
        private static final long serialVersionUID = 5957164118152886994L;
        int statusCode = -1;

        public HttpException() {
            super();
            // TODO Auto-generated constructor stub
        }

        public HttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
            // TODO Auto-generated constructor stub
        }

        public HttpException(String message, Throwable cause) {
            super(message, cause);
            // TODO Auto-generated constructor stub
        }

        public HttpException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
            // TODO Auto-generated constructor stub
        }

        public HttpException(String message) {
            super(message);
            // TODO Auto-generated constructor stub
        }

        public HttpException(Throwable cause) {
            super(cause);
            // TODO Auto-generated constructor stub
        }

        public int getStatusCode() {
            return statusCode;
        }

    }
}
