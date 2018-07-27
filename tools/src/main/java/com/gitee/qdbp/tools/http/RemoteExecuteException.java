package com.gitee.qdbp.tools.http;

/**
 * 远程调用失败
 *
 * @author zhaohuihua
 * @version 150923
 */
public class RemoteExecuteException extends HttpException {

    /** 版本序列号 **/
    private static final long serialVersionUID = 6193771880423109079L;

    public RemoteExecuteException() {
        super();
    }

    public RemoteExecuteException(String message) {
        super(message);
    }

    public RemoteExecuteException(String message, Throwable thrown) {
        super(message, thrown);
    }
}
