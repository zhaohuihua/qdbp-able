package com.gitee.zhaohuihua.tools.http;

import com.gitee.zhaohuihua.core.result.IResultMessage;

/**
 * 远程业务异常类
 *
 * @author zhaohuihua
 * @version 150923
 */
public class RemoteServiceException extends HttpException implements IResultMessage {

    /** 版本序列号 **/
    private static final long serialVersionUID = 3860478565220222032L;

    /** 错误返回码 **/
    private String code;

    /**
     * 构造函数
     *
     * @param code 错误返回码
     */
    public RemoteServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数
     *
     * @param code 错误返回码
     * @param cause 引发异常的原因
     */
    public RemoteServiceException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /** 获取错误返回码 **/
    @Override
    public String getCode() {
        return code;
    }

}
