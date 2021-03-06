package com.gitee.qdbp.tools.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.beans.KeyString;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResponseMessage;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * HTTP请求工具类
 *
 * @author zhaohuihua
 */
public abstract class HttpTools {

    private static final Logger log = LoggerFactory.getLogger(HttpTools.class);
    public static final HttpTools form = new HttpFormImpl();

    public static final HttpTools json = new HttpJsonImpl();

    protected IHttpHandler httpHandler;
    protected Charset charset = Consts.UTF_8;
    protected ContentType contentType = ContentType.create("text/plain", charset);

    public HttpTools() {
        this.httpHandler = new BaseHttpHandler();
    }

    public HttpTools(IHttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public void setHttpHandler(IHttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    /**
     * 执行远程请求
     *
     * @param hurl 请求地址和请求方法
     * @return 响应结果
     * @throws HttpException
     */
    public ResponseMessage execute(HttpUrl hurl) throws HttpException {
        return execute(hurl, null);
    }

    /**
     * 执行远程请求
     *
     * @param hurl 请求地址和请求方法
     * @param params 请求参数
     * @return 响应结果
     * @throws HttpException
     */
    public <P> ResponseMessage execute(HttpUrl hurl, Map<String, P> params) throws HttpException {

        Map<String, Object> map = fillBaseParams(hurl, params);
        String string;
        if (hurl.getMethod() == HttpMethod.POST) {
            string = post(hurl.getUrl(), map);
        } else {
            string = get(hurl.getUrl(), map);
        }

        if (VerifyTools.isBlank(string)) {
            ResultCode rc = ResultCode.REMOTE_SERVICE_ERROR;
            throw new RemoteServiceException(rc.getCode(), rc.getMessage());
        }

        ResponseMessage result;
        try {
            result = parseResult(hurl, string);
        } catch (RemoteServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ResultParseException("Http request success, but JSON.parseObject error. " + hurl, e);
        }

        return result;
    }

    /**
     * 执行远程请求并返回指定对象
     *
     * @param hurl 请求地址和请求方法
     * @param type 结果类型
     * @return 响应结果对象
     * @throws HttpException
     */
    public <T> T query(HttpUrl hurl, Class<T> type) throws HttpException {
        return query(hurl, null, type);
    }

    /**
     * 执行远程请求并返回指定对象
     *
     * @param hurl 请求地址和请求方法
     * @param params 请求参数
     * @param type 结果类型
     * @return 响应结果对象
     * @throws HttpException
     */
    public <T, P> T query(HttpUrl hurl, Map<String, P> params, Class<T> type) throws HttpException {
        ResponseMessage result = this.execute(hurl, params);

        Object body = result.getBody();

        if (VerifyTools.isBlank(body)) {
            return null;
        }

        try {
            return TypeUtils.castToJavaBean(body, type);
        } catch (Exception e) {
            throw new ResultParseException("Http request success, but TypeUtils.castToJavaBean error. " + hurl, e);
        }
    }

    /**
     * 执行远程请求并返回对象列表
     *
     * @param hurl 请求地址和请求方法
     * @param type 结果类型
     * @return 对象列表
     * @throws HttpException
     */
    public <T> List<T> list(HttpUrl hurl, Class<T> type) throws HttpException {
        return list(hurl, type);
    }

    /**
     * 执行远程请求并返回对象列表
     *
     * @param hurl 请求地址和请求方法
     * @param params 请求参数
     * @param type 结果类型
     * @return 对象列表
     * @throws HttpException
     */
    public <T, P> List<T> list(HttpUrl hurl, Map<String, Object> params, Class<T> type) throws HttpException {
        ResponseMessage result = this.execute(hurl, params);

        Object body = result.getBody();

        if (VerifyTools.isBlank(body)) {
            return null;
        }

        if (body instanceof String) {
            try {
                return JSON.parseArray((String) body, type);
            } catch (Exception e) {
                throw new ResultParseException("Http request success, but JSON.parseArray error. " + hurl, e);
            }
        }

        List<Object> objects = new ArrayList<>();
        if (body.getClass().isArray()) {
            Object[] array = (Object[]) body;
            for (Object item : array) {
                objects.add(item);
            }
        } else if (body instanceof List) {
            objects.addAll((List<?>) body);
        } else if (body instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) body;
            for (Object item : iterable) {
                objects.add(item);
            }
        } else {
            throw new ResultParseException("Object can't convert to List. " + hurl);
        }

        List<T> results = new ArrayList<>();
        for (Object item : objects) {
            try {
                results.add(TypeUtils.castToJavaBean(item, type));
            } catch (Exception e) {
                throw new ResultParseException("Http request success, but TypeUtils.castToJavaBean error. " + hurl, e);
            }
        }

        return results;
    }

    protected ResponseMessage parseResult(HttpUrl hurl, String string) throws RemoteServiceException, Exception {
        return httpHandler.parseResult(hurl, string);
    }

    /**
     * 填充基础参数, 如填充配置信息在的公共参数/计算摘要等操作
     * 
     * @param hurl 请求地址
     * @param params 业务参数
     * @return 业务参数+基础参数
     */
    protected <P> Map<String, Object> fillBaseParams(HttpUrl hurl, Map<String, P> params) {
        return httpHandler.fillBaseParams(hurl, params);
    }

    /**
     * 发送GET请求<br>
     * 注意, 这里的请求参数和响应报文都没有经过HttpHandler处理!
     *
     * @param url 请求地址
     * @return 响应字符串
     * @throws ServiceException
     */
    public String get(String url) throws HttpException {
        return get(url, null);
    }

    /**
     * 发送POST请求<br>
     * 注意, 这里的请求参数和响应报文都没有经过HttpHandler处理!
     *
     * @param url 请求地址
     * @return 响应字符串
     * @throws ServiceException
     */
    public String post(String url) throws HttpException {
        return post(url, null);
    }

    /**
     * 发送GET请求<br>
     * 注意, 这里的请求参数和响应报文都没有经过HttpHandler处理!
     *
     * @param url 请求地址
     * @param params 请求参数
     * @return 响应字符串
     * @throws ServiceException
     */
    public <P> String get(String url, Map<String, P> params) throws HttpException {

        URI uri;
        try {
            URIBuilder builder = new URIBuilder(url);
            if (params != null && !params.isEmpty()) {
                setGetParams(builder, params);
            }
            uri = builder.build();
        } catch (URISyntaxException e) {
            throw new HttpException("Http request parameter error, GET " + url, e);
        }

        // 创建HttpClientBuilder
        HttpClientBuilder builder = HttpClientBuilder.create();
        // HttpClient
        try (CloseableHttpClient client = builder.build();) {
            HttpGet get = new HttpGet(uri);

            onBeforeExecute(get); // 发送请求前设置header参数等操作
            try (CloseableHttpResponse response = client.execute(get);) {
                onAfterExecute(get, response); // 发送请求后的操作
                String string = responseToString(response);

                StatusLine status = response.getStatusLine();
                int code = status.getStatusCode();

                if (code != HttpStatus.SC_OK) {
                    if (log.isWarnEnabled()) {
                        log.warn("Http response status code is {}.\n\t{}\n\t{}", code, uri, string);
                    }
                    throw new RemoteExecuteException("Http response status code is " + code + ", GET " + url);
                } else if (log.isTraceEnabled()) {
                    log.trace("Http response status code is {}.\n\t{}\n\t{}", code, uri, string);
                }
                return string;
            }
        } catch (IOException e) {
            throw new RemoteExecuteException("Http request error, GET " + url, e);
        }

    }

    /**
     * 发送POST请求<br>
     * 注意, 这里的请求参数和响应报文都没有经过HttpHandler处理!
     *
     * @param url 请求地址
     * @param params 请求参数
     * @return 响应字符串
     * @throws ServiceException
     */
    public <P> String post(String url, Map<String, P> params) throws HttpException {

        // 创建HttpClientBuilder
        HttpClientBuilder builder = HttpClientBuilder.create();
        // HttpClient
        try (CloseableHttpClient client = builder.build();) {
            HttpPost post = new HttpPost(url);
            List<KeyString> logs = new ArrayList<>();
            if (params != null && !params.isEmpty()) {
                setPostParams(post, params, logs);
            }

            onBeforeExecute(post); // 发送请求前设置header参数等操作
            try (CloseableHttpResponse response = client.execute(post);) {
                onAfterExecute(post, response); // 发送请求后的操作
                String string = responseToString(response);

                StatusLine status = response.getStatusLine();
                int code = status.getStatusCode();

                if (code != HttpStatus.SC_OK) {
                    if (log.isWarnEnabled()) {
                        String fmt = "Http response status code is {}.\n\t{}\n\t{}\n\t{}";
                        log.warn(fmt, code, url, toParamString(logs), string);
                    }
                    throw new RemoteExecuteException("Http response status code is " + code + ", POST " + url);
                } else if (log.isTraceEnabled()) {
                    String fmt = "Http response status code is {}.\n\t{}\n\t{}\n\t{}";
                    log.trace(fmt, code, url, toParamString(logs), string);
                }
                return string;
            }
        } catch (IOException e) {
            throw new RemoteExecuteException("Http request error, POST " + url, e);
        }

    }

    /**
     * 文件上传<br>
     * 注意, 这里的请求参数和响应报文都没有经过HttpHandler处理!
     *
     * @param url 请求地址
     * @param files 文件
     * @param params 请求参数
     * @return 响应字符串
     * @throws ServiceException
     */
    public <P> String upload(String url, Map<String, P> params) throws HttpException {
        // 创建HttpClientBuilder
        HttpClientBuilder builder = HttpClientBuilder.create();
        // HttpClient
        try (CloseableHttpClient client = builder.build();) {
            HttpPost post = new HttpPost(url);
            List<KeyString> logs = new ArrayList<>();
            if (params != null && !params.isEmpty()) {
                setUploadParams(post, params, logs);
            }

            onBeforeExecute(post); // 发送请求前设置header参数等操作
            try (CloseableHttpResponse response = client.execute(post);) {
                onAfterExecute(post, response); // 发送请求后的操作
                String string = responseToString(response);

                StatusLine status = response.getStatusLine();
                int code = status.getStatusCode();

                if (code != HttpStatus.SC_OK) {
                    if (log.isWarnEnabled()) {
                        String fmt = "Http response status code is {}.\n\t{}\n\t{}\n\t{}";
                        log.warn(fmt, code, url, toParamString(logs), string);
                    }
                    throw new RemoteExecuteException("Http response status code is " + code + ", POST " + url);
                } else if (log.isTraceEnabled()) {
                    String fmt = "Http response status code is {}.\n\t{}\n\t{}\n\t{}";
                    log.trace(fmt, code, url, toParamString(logs), string);
                }
                return string;
            }
        } catch (IOException e) {
            throw new RemoteExecuteException("Http upload error, POST " + url, e);
        }

    }

    /**
     * 文件上传
     *
     * @param hurl 请求地址和请求方法
     * @param params 请求参数
     * @return 响应结果
     * @throws HttpException
     */
    public <P> ResponseMessage upload(HttpUrl hurl, Map<String, P> params) throws HttpException {

        if (hurl.getMethod() != HttpMethod.POST) {
            throw new IllegalArgumentException("File upload request method must be POST. " + hurl);
        }

        Map<String, Object> map = fillBaseParams(hurl, params);
        String string = upload(hurl.getUrl(), map);

        if (VerifyTools.isBlank(string)) {
            ResultCode rc = ResultCode.REMOTE_SERVICE_ERROR;
            throw new RemoteServiceException(rc.getCode(), rc.getMessage());
        }

        ResponseMessage result;
        try {
            result = parseResult(hurl, string);
        } catch (Exception e) {
            throw new ResultParseException("Http request success, but JSON.parseObject error. " + hurl, e);
        }

        return result;
    }

    /**
     * 设置GET参数
     *
     * @param builder
     * @param params
     */
    protected abstract <P> void setGetParams(URIBuilder builder, Map<String, P> params);

    /**
     * 设置POST参数
     *
     * @param method
     * @param params
     */
    protected abstract <P> void setPostParams(HttpPost method, Map<String, P> params, List<KeyString> logs);

    /**
     * 设置文件上传参数<br>
     * 这种方式提交的参数, Filter无法获取到, 只有通过Controller才能获取到, 因为是以二进制流的方式提交的
     *
     * @param method
     * @param params
     */
    protected abstract <P> void setUploadParams(HttpPost method, Map<String, P> params, List<KeyString> logs);

    protected void addParam(MultipartEntityBuilder builder, String key, Object value, List<KeyString> logs) {
        String string;
        if (value instanceof InputStream) {
            builder.addBinaryBody(key, (InputStream) value);
            string = value.getClass().getSimpleName();
        } else if (value instanceof File) {
            builder.addBinaryBody(key, (File) value);
            string = "File[" + ((File) value).getAbsolutePath() + "]";
        } else if (value instanceof CharSequence) {
            string = value.toString();
            builder.addTextBody(key, string, contentType);
        } else {
            string = JSON.toJSONString(value);
            builder.addTextBody(key, string, contentType);
        }
        if (logs != null) {
            logs.add(new KeyString(key, string));
        }
    }

    protected String toParamString(List<KeyString> logs) {
        if (VerifyTools.isBlank(logs)) {
            return null;
        }
        if (logs.size() == 1 && logs.get(0).getKey() == null) {
            Object value = logs.get(0).getValue();
            return value == null ? null : value.toString();
        }

        StringBuilder buffer = new StringBuilder();
        for (KeyString i : logs) {
            if (buffer.length() > 0) {
                buffer.append("&");
            }
            buffer.append(i.getKey()).append("=").append(i.getValue());
        }
        return buffer.toString();
    }

    protected String toLogString(String key, Object value, List<KeyString> logs) {
        String string;
        if (value instanceof File) {
            string = ((File) value).getAbsolutePath();
        } else if (value instanceof CharSequence) {
            string = value.toString();
        } else {
            string = JSON.toJSONString(value);
        }
        if (logs != null) {
            logs.add(new KeyString(key, string));
        }
        return string;
    }

    /** 发送请求前设置header参数等操作 **/
    protected void onBeforeExecute(HttpMessage hm) {
        Header[] allHeaders = httpHandler.getAllHeaders();
        if (VerifyTools.isNotBlank(allHeaders)) {
            hm.setHeaders(allHeaders);
        }
    }

    /** 发送请求后的操作 **/
    protected void onAfterExecute(HttpMessage hm, HttpResponse resp) {
    }

    /** 获取响应文本 **/
    protected String responseToString(HttpResponse resp) throws ParseException, IOException {
        HttpEntity entity = resp.getEntity();
        return EntityUtils.toString(entity, charset);
    }

    /**
     * 以application/json的方式提交请求参数
     *
     * @author zhaohuihua
     * @version 160224
     */
    public static class HttpJsonImpl extends HttpTools {

        public HttpJsonImpl() {
            super();
        }

        public HttpJsonImpl(IHttpHandler httpHandler) {
            super(httpHandler);
        }

        public <P> String get(String url, Map<String, P> params) throws HttpException {
            throw new RuntimeException("GET request method is not supported!");
        }

        public <P> String upload(String url, Map<String, P> params) throws HttpException {
            throw new RuntimeException("UPLOAD request method is not supported!");
        }

        protected <P> void setGetParams(URIBuilder builder, Map<String, P> params) {
            throw new RuntimeException("GET request method is not supported!");
        }

        protected <P> void setUploadParams(HttpPost method, Map<String, P> params, List<KeyString> logs) {
            throw new RuntimeException("UPLOAD request method is not supported!");
        }

        @Override
        protected <P> void setPostParams(HttpPost method, Map<String, P> params, List<KeyString> logs) {
            String json = JSON.toJSONString(params);
            if (logs != null) {
                logs.add(new KeyString(null, json));
            }
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            method.setEntity(entity);
        }
    }

    /**
     * 以application/x-www-form-urlencoded的方式提交请求参数
     *
     * @author zhaohuihua
     * @version 160224
     */
    public static class HttpFormImpl extends HttpTools {

        public HttpFormImpl() {
            super();
        }

        public HttpFormImpl(IHttpHandler httpHandler) {
            super(httpHandler);
        }

        /**
         * 设置GET参数
         *
         * @param builder
         * @param params
         */
        protected <P> void setGetParams(URIBuilder builder, Map<String, P> params) {

            builder.setCharset(charset);
            Set<Entry<String, P>> sets = params.entrySet();
            for (Entry<String, P> entry : sets) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key == null || value == null) {
                    continue;
                }
                if (value instanceof Object[]) {
                    Object[] objects = (Object[]) value;
                    for (Object object : objects) {
                        builder.addParameter(key, toLogString(key, object, null));
                    }
                } else if (value instanceof Iterable) {
                    Iterable<?> iterator = (Iterable<?>) value;
                    for (Object object : iterator) {
                        builder.addParameter(key, toLogString(key, object, null));
                    }
                } else {
                    builder.addParameter(key, toLogString(key, value, null));
                }
            }
        }

        /**
         * 设置POST参数
         *
         * @param method
         * @param params
         */
        protected <P> void setPostParams(HttpPost method, Map<String, P> params, List<KeyString> logs) {
            Set<Entry<String, P>> sets = params.entrySet();
            List<NameValuePair> pairs = new ArrayList<>();
            for (Entry<String, P> entry : sets) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key == null || value == null) {
                    continue;
                }
                if (value instanceof Object[]) {
                    Object[] objects = (Object[]) value;
                    for (Object object : objects) {
                        pairs.add(new BasicNameValuePair(key, toLogString(key, object, logs)));
                    }
                } else if (value instanceof Iterable) {
                    Iterable<?> iterator = (Iterable<?>) value;
                    for (Object object : iterator) {
                        pairs.add(new BasicNameValuePair(key, toLogString(key, object, logs)));
                    }
                } else {
                    pairs.add(new BasicNameValuePair(key, toLogString(key, value, logs)));
                }
            }
            HttpEntity entity = new UrlEncodedFormEntity(pairs, charset);
            method.setEntity(entity);
        }

        /**
         * 设置文件上传参数<br>
         * 这种方式提交的参数, Filter无法获取到, 只有通过Controller才能获取到, 因为是以二进制流的方式提交的
         *
         * @param method
         * @param params
         */
        protected <P> void setUploadParams(HttpPost method, Map<String, P> params, List<KeyString> logs) {
            Set<Entry<String, P>> sets = params.entrySet();
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (Entry<String, P> entry : sets) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key == null || value == null) {
                    continue;
                }
                if (value instanceof Iterable) {
                    Iterable<?> iterator = (Iterable<?>) value;
                    for (Object object : iterator) {
                        addParam(builder, key, object, logs);
                    }
                } else if (value instanceof Object[]) {
                    Object[] values = (Object[]) value;
                    for (Object object : values) {
                        addParam(builder, key, object, logs);
                    }
                } else {
                    addParam(builder, key, value, logs);
                }
            }
            method.setEntity(builder.build());
        }
    }
}
