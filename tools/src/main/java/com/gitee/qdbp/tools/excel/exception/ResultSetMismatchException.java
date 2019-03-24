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

    public ResultSetMismatchException(String message) {
        super(message);
    }
}
