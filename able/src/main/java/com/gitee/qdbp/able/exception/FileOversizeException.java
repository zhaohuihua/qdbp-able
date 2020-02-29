package com.gitee.qdbp.able.exception;

import java.io.IOException;

/**
 * 文件超出大小限制
 *
 * @author zhaohuihua
 * @version 20200119
 */
public class FileOversizeException extends IOException {

    private static final long serialVersionUID = 1L;

    public FileOversizeException() {
        super();
    }

    public FileOversizeException(String message) {
        super(message);
    }

    public FileOversizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileOversizeException(Throwable cause) {
        super(cause);
    }
}