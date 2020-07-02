package com.gitee.qdbp.able.exception;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import com.gitee.qdbp.able.result.IResultMessage;

/**
 * 业务异常类
 *
 * @author zhaohuihua
 * @version 150915
 */
public class ServiceException extends RuntimeException implements IResultMessage {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 错误返回码 **/
    private String code;
    /** 错误详情 **/
    private String details;

    /** 默认构造函数 **/
    public ServiceException() {
    }

    /**
     * 构造函数
     *
     * @param result 错误返回码
     */
    public ServiceException(IResultMessage result) {
        super(result.getMessage());
        this.code = result.getCode();
    }

    /**
     * 构造函数
     *
     * @param result 错误返回码
     * @param details 错误详情
     */
    public ServiceException(IResultMessage result, String details) {
        super(result.getMessage());
        this.code = result.getCode();
        this.details = details;
    }

    /**
     * 构造函数
     *
     * @param result 错误返回码
     * @param cause 引发异常的原因
     */
    public ServiceException(IResultMessage result, Throwable cause) {
        super(result.getMessage(), cause);
        this.code = result.getCode();
    }

    /**
     * 构造函数
     *
     * @param result 错误返回码
     * @param details 错误详情
     * @param cause 引发异常的原因
     */
    public ServiceException(IResultMessage result, String details, Throwable cause) {
        super(result.getMessage(), cause);
        this.code = result.getCode();
        this.details = details;
    }

    /**
     * 获取错误返回码
     *
     * @return 错误返回码
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * 设置错误返回码
     * 
     * @param code 错误返回码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取错误详情
     *
     * @return 错误详情
     */
    public String getDetails() {
        return details;
    }

    /**
     * 设置错误详情
     * 
     * @param details 错误详情
     */
    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(getClass().getSimpleName());
        buffer.append('[').append(code).append(']');
        buffer.append(getMessage());
        if (details != null && details.length() > 0) {
            buffer.append('(').append(details).append(')');
        }
        return buffer.toString();
    }

    /** 从exception.cause中查找ServiceException **/
    public static void throwWhenServiceException(Throwable e) throws ServiceException {
        Throwable unwrapped = e;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                break;
            }
        }

        Throwable cause = unwrapped;
        while (cause != null) {
            if (cause instanceof ServiceException) {
                throw (ServiceException) cause;
            }
            cause = cause.getCause();
        }
    }
}
