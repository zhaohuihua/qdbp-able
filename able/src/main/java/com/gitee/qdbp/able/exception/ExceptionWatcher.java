package com.gitee.qdbp.able.exception;

/**
 * 异常处理类<br>
 * 只记录警告日志的实现类: com.gitee.qdbp.tools.defaults.ExceptionLogger
 *
 * @author zhaohuihua
 * @version 190622
 */
public interface ExceptionWatcher {

    /**
     * 捕获到了异常
     * 
     * @param message 异常信息
     * @param e 异常对象
     * @return 是否继续
     */
    boolean onCaughtException(String message, Throwable e);
}
