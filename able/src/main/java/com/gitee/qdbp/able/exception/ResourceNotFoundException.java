package com.gitee.qdbp.able.exception;

/**
 * 资源未找到
 *
 * @author zhaohuihua
 * @version 170624
 */
public class ResourceNotFoundException extends EditableException {

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
