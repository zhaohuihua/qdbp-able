package com.gitee.qdbp.tools.excel.exception;

import com.gitee.qdbp.able.exception.EditableException;

/**
 * 结果集不匹配
 *
 * @author zhaohuihua
 * @version 190324
 */
public class ResultSetMismatchException extends EditableException {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public ResultSetMismatchException(String message) {
        super(message);
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 异常原因
     */
    public ResultSetMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
