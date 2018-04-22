package com.gitee.zhaohuihua.core.exception;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import com.gitee.zhaohuihua.core.result.IResultMessage;

/**
 * 业务异常类
 *
 * @author zhaohuihua
 * @version 150915
 */
public class ServiceException extends EditableRuntimeException implements IResultMessage {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 错误返回码 **/
    private String code;

    /** 错误描述 **/
    private String message;

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
        this.message = result.getMessage();
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
        this.message = result.getMessage();
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
     * 获取错误描述
     *
     * @return 错误描述
     */
    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * 设置错误描述
     * 
     * @param message 错误描述
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + code + "] " + this.getMessage();
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
