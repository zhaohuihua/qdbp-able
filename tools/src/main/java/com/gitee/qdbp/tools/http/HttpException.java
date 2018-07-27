package com.gitee.zhaohuihua.tools.http;

/**
 * HTTP请求执行失败
 *
 * @author zhaohuihua
 * @version 150923
 */
public class HttpException extends Exception {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1569942164454302456L;

    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable thrown) {
        super(message, thrown);
    }
}
