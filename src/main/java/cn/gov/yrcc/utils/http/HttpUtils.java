package cn.gov.yrcc.utils.http;

import cn.gov.yrcc.internal.error.BusinessException;
import cn.gov.yrcc.utils.json.JsonUtils;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class HttpUtils {

    // 重试次数，解决在KeepAlive边界失效时获取到Http连接，发送请求报错NoHttpResponseException
    private static final int RETRY_TIMES = 3;
    private final static HttpClientBuilder httpClientBuilder = HttpClients.custom();

    static {
        int maxConnect = 10_000; // 最大连接数
        int maxPerRoute = 5_000; // 每个路由最大连接数
        int connectTimeout = 60_000; // 创建链接（TCP协议的三次握手）超时时间
        int socketTimeout = 60_000; // 获取响应内容超时时间
        int connectRequestTimeout = 30_000; // 从链接池获取链接的超时时间

        SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(
                SSLContexts.createSystemDefault(),
                new String[]{"TLSv1", "TLSv1.2"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslFactory)
                .build();
        PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager(registry);
        pool.setMaxTotal(maxConnect);
        pool.setDefaultMaxPerRoute(maxPerRoute);
        httpClientBuilder.setConnectionManager(pool);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectRequestTimeout)
                .build();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        httpClientBuilder.setConnectionManagerShared(true); // 设置连接池为共享的
        httpClientBuilder.setRetryHandler((exception, executionCount, context) -> executionCount <= RETRY_TIMES && exception instanceof NoHttpResponseException);
    }

    /**
     * 发送post请求
     *
     * @param url     URL
     * @param bodyMap request body
     * @return response
     */
    public String post(String url, Map<String, Object> bodyMap) {
        try {
            return innerPost(url, bodyMap);
        } catch (IOException e) {
            log.error("[HttpUtils] post() called with Params: url = {}, bodyMap = {}, Error message = {}",
                    url, bodyMap, Throwables.getStackTraceAsString(e));
            throw new RuntimeException("发送POST请求异常：IOException");
        }
    }

    /**
     * inner post
     *
     * @param url     url
     * @param bodyMap body map
     * @return response
     * @throws IOException IOException
     */
    public String innerPost(String url, Map<String, Object> bodyMap) throws IOException {
        try (CloseableHttpClient http = httpClientBuilder.build()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(Objects.requireNonNull(JsonUtils.toJsonString(bodyMap)), ContentType.APPLICATION_JSON));
            try (CloseableHttpResponse response = http.execute(httpPost)) {
                int status = response.getStatusLine().getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    throw new RuntimeException("发送POST请求失败：状态码" + status);
                }
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("发送POST请求失败：返回为空");
                }
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("HttpUtils post called with url = {}, body = {} occurred exception. Error message: {}",
                        url, bodyMap, Throwables.getStackTraceAsString(e));
                throw new RuntimeException("发送POST请求异常");
            }
        }
    }

    /**
     * 发送post请求
     *
     * @param url     URL
     * @param body    request body
     * @param headers request headers
     * @return string response
     * @throws IOException IOException
     */
    public String post(String url, String body, Map<String, String> headers) {
        try {
            return innerPost(url, body, headers, null, null);
        } catch (IOException e) {
            log.error("[HttpUtils] post() called with Params: url = {}, body = {}, headers = {}, Error message = {}",
                    url, body, headers, Throwables.getStackTraceAsString(e));
            throw new RuntimeException("发送POST请求异常：IOException");
        }
    }

    /**
     * 发送post请求
     *
     * @param url  URL
     * @param body request body
     * @return string response
     * @throws IOException IOException
     */
    public String post(String url, String body, Pair<String, String> basicAuth, Integer expectedCode) {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json;charset=UTF-8");
            return innerPost(url, body, headers, basicAuth, expectedCode);
        } catch (IOException e) {
            log.error("[HttpUtils] post() called with Params: url = {}, body = {}, Error message = {}",
                    url, body, Throwables.getStackTraceAsString(e));
            throw new RuntimeException("发送POST请求异常：IOException");
        }
    }

    /**
     * 发送post请求
     *
     * @param url     URL
     * @param body    request body
     * @param headers request headers
     * @return string response
     * @throws IOException IOException
     */
    public String innerPost(String url, String body, Map<String, String> headers, Pair<String, String> basicAuth, Integer expectedCode) throws IOException {
        if (expectedCode == null) {
            expectedCode = HttpStatus.SC_OK;
        }
        try (CloseableHttpClient http = httpClientBuilder.build()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(Objects.requireNonNull(body), ContentType.APPLICATION_JSON));
            if (basicAuth != null) {
                headers.put("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((basicAuth.getLeft() + ":" + basicAuth.getRight()).getBytes(StandardCharsets.UTF_8)));
            }
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(httpPost::setHeader);
            }
            try (CloseableHttpResponse response = http.execute(httpPost)) {
                int status = response.getStatusLine().getStatusCode();
                if (status != expectedCode) {
                    throw new RuntimeException("发送POST请求失败：状态码" + status);
                }
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("发送POST请求失败：返回为空");
                }
                if (expectedCode == HttpStatus.SC_CREATED) {
                    return null;
                }
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("HttpUtils post called with url = {}, body = {}, headers = {} occurred exception. Error message: {}",
                        url, body, headers, Throwables.getStackTraceAsString(e));
                throw new RuntimeException("发送POST请求异常");
            }
        }
    }

    /**
     * 发送get请求
     *
     * @param url     url
     * @param headers headers
     * @return response
     */
    public String get(String url, Map<String, String> headers, Pair<String, String> basicAuth) {
        try {
            return innerGet(url, headers, basicAuth);
        } catch (IOException e) {
            log.error("[HttpUtils] get() called with Params: url = {}, headers = {}, Error message = {}",
                    url, headers, Throwables.getStackTraceAsString(e));
            throw new RuntimeException("发送GET请求异常：IOException");
        }
    }

    /**
     * 发送get请求
     *
     * @param url url
     * @return response
     */
    public String get(String url, Pair<String, String> basicAuth) {
        try {
            return innerGet(url, new HashMap<>(), basicAuth);
        } catch (IOException e) {
            log.error("[HttpUtils] get() called with Params: url = {}, Error message = {}", url, Throwables.getStackTraceAsString(e));
            throw new RuntimeException("发送GET请求异常：IOException");
        }
    }

    /**
     * 发送get请求
     *
     * @param url     url
     * @param headers headers
     * @return response
     * @throws IOException IOException
     */
    public String innerGet(String url, Map<String, String> headers, Pair<String, String> basicAuth) throws IOException {
        try (CloseableHttpClient http = httpClientBuilder.build()) {
            HttpGet httpGet = new HttpGet(url);
            //
            if (basicAuth != null) {
                headers.put("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((basicAuth.getLeft() + ":" + basicAuth.getRight()).getBytes(StandardCharsets.UTF_8)));
            }
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(httpGet::setHeader);
            }
            try (CloseableHttpResponse response = http.execute(httpGet)) {
                int status = response.getStatusLine().getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    throw new RuntimeException("发送GET请求失败：状态码" + status);
                }
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("发送GET请求失败：返回为空");
                }
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("HttpUtils post called with url = {}, headers = {} occurred exception. Error message: {}",
                        url, headers, Throwables.getStackTraceAsString(e));
                throw new RuntimeException("发送GET请求异常");
            }
        }
    }

    public String delete(String url, Pair<String, String> basicAuth, Integer expectedCode) {
        try {
            return innerDelete(url, new HashMap<>(), basicAuth, expectedCode);
        } catch (IOException e) {
            log.error("[HttpUtils] delete() called with Params: url = {}, basicAuth = {}, expectedCode = {}, Error message = {}",
                    url, basicAuth, expectedCode, Throwables.getStackTraceAsString(e));
            throw new BusinessException("发送DELETE请求异常：IOException");
        }
    }

    private String innerDelete(String url, Map<String, String> headers, Pair<String, String> basicAuth, Integer expectedCode) throws IOException {
        if (expectedCode == null) {
            expectedCode = HttpStatus.SC_OK;
        }
        try (CloseableHttpClient http = httpClientBuilder.build()) {
            HttpDelete httpDelete = new HttpDelete(url);
            if (basicAuth != null) {
                headers.put("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString((basicAuth.getLeft() + ":" + basicAuth.getRight()).getBytes(StandardCharsets.UTF_8)));
            }
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(httpDelete::setHeader);
            }
            try (CloseableHttpResponse response = http.execute(httpDelete)) {
                int status = response.getStatusLine().getStatusCode();
                if (status != expectedCode) {
                    throw new RuntimeException("发送DELETE请求失败：状态码" + status);
                }
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("发送DELETE请求失败：返回为空");
                }
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("HttpUtils delete called with url = {}, headers = {} occurred exception. Error message: {}",
                        url, headers, Throwables.getStackTraceAsString(e));
                throw new RuntimeException("发送DELETE请求异常");
            }
        }
    }
}
