package com.gitee.zhaohuihua.tools.http;

import com.gitee.zhaohuihua.tools.files.PathTools;

/**
 * 远程请求的URL和HTTP请求方法<br>
 * HttpUrl hurl = new HostUrl(host).post(uri); // 用于单一主机<br>
 * HttpUri huri = HttpUri.post(uri); HttpUrl hurl = huri.to(host); // 用于多主机<br>
 *
 * @author zhaohuihua
 */
public class HttpUrl {

    /** 远程请求的URL **/
    private final String url;

    /** HTTP请求方法 **/
    private final HttpMethod method;

    /**
     * 构造函数
     *
     * @param url 远程请求的URL
     * @param method HTTP请求方法
     */
    protected HttpUrl(HttpMethod method, String url) {
        this.url = url;
        this.method = method;
    }

    /** 获取远程请求URL **/
    public String getUrl() {
        return url;
    }

    /** 获取HTTP请求方法 **/
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return method + " " + url;
    }

    public static class HostUrl {

        private final String host;

        public HostUrl(String host) {
            this.host = host;
        }

        public HttpUrl get(String uri) {
            return new HttpUrl(HttpMethod.POST, PathTools.concat(host, uri));
        }

        public HttpUrl post(String uri) {
            return new HttpUrl(HttpMethod.POST, PathTools.concat(host, uri));
        }
    }

    public static class HttpUri {

        /** HTTP请求方法 **/
        private final HttpMethod method;

        private final String uri;

        public HttpUri(HttpMethod method, String uri) {
            this.uri = uri;
            this.method = method;
        }

        public static HttpUri get(String uri) {
            return new HttpUri(HttpMethod.GET, uri);
        }

        public static HttpUri post(String uri) {
            return new HttpUri(HttpMethod.POST, uri);
        }

        /** 获取远程请求URL **/
        public String getUrl(String host) {
            return PathTools.concat(host, uri);
        }

        /** 获取HTTP请求方法 **/
        public HttpMethod getMethod() {
            return method;
        }

        public HttpUrl to(String host) {
            return new HttpUrl(method, PathTools.concat(host, uri));
        }
    }
}
