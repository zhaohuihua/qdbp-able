package com.gitee.zhaohuihua.tools.http;

/**
 * 结果解析失败
 *
 * @author zhaohuihua
 * @version 150923
 */
public class ResultParseException extends HttpException {

    /** 版本序列号 **/
    private static final long serialVersionUID = -3710084836446122984L;

    public ResultParseException() {
        super();
    }

    public ResultParseException(String message) {
        super(message);
    }

    public ResultParseException(String message, Throwable thrown) {
        super(message, thrown);
    }
}
