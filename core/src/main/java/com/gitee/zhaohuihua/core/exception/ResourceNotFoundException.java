package com.gitee.zhaohuihua.core.exception;

import com.gitee.zhaohuihua.core.exception.EditableRuntimeException;

/**
 * 资源未找到
 *
 * @author zhaohuihua
 * @version 170624
 */
public class ResourceNotFoundException extends EditableRuntimeException {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
