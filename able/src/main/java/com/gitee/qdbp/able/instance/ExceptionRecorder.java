package com.gitee.qdbp.able.instance;

import java.util.concurrent.atomic.AtomicInteger;
import com.gitee.qdbp.able.exception.ExceptionWatcher;

/**
 * 只记录警告日志的异常处理类<br>
 * 忽略异常, 只记录警告日志, 发生异常后继续处理
 *
 * @author zhaohuihua
 * @version 190622
 */
public class ExceptionRecorder implements ExceptionWatcher {

    /** 记录简单日志的实例 **/
    public static final ExceptionRecorder SIMPLE = new ExceptionRecorder(false);
    /** 记录详细日志的实例 **/
    public static final ExceptionRecorder DETAILS = new ExceptionRecorder(true);

    /**
     * 生成一个只记录警告日志的实例
     * 
     * @param details 是否记录StackTrace
     * @return 异常处理类
     */
    public static ExceptionRecorder newLogWatcher(boolean details) {
        return new ExceptionRecorder(details);
    }

    /**
     * 生成一个记录警告日志并统计失败次数的实例
     * 
     * @param details 是否记录StackTrace
     * @return 异常处理类
     */
    public static CountLogWatcher newCountWatcher(boolean details) {
        return new CountLogWatcher(details);
    }

    /** 是否记录StackTrace **/
    private final boolean details;

    /**
     * 构造函数
     * 
     * @param details 是否记录StackTrace
     */
    protected ExceptionRecorder(boolean details) {
        this.details = details;
    }

    @Override
    public boolean onCaughtException(String message, Throwable e) {
        System.out.println(message);
        if (details) {
            e.printStackTrace(); // 记录详细警告日志
        }
        return true; // 发生异常后继续处理
    }

    /**
     * 记录警告日志并统计失败次数的异常处理类<br>
     * 忽略异常, 记录警告日志并统计失败次数, 发生异常后继续处理
     *
     * @author zhaohuihua
     * @version 190622
     */
    public static class CountLogWatcher extends ExceptionRecorder {

        /** 计数器 **/
        private AtomicInteger adder = new AtomicInteger();

        /**
         * 构造函数
         * 
         * @param details 是否记录StackTrace
         */
        protected CountLogWatcher(boolean details) {
            super(details);
        }

        @Override
        public boolean onCaughtException(String message, Throwable e) {
            adder.getAndIncrement();
            return super.onCaughtException(message, e);
        }

        /** 获取失败次数 **/
        public int getFailedTimes() {
            return adder.get();
        }
    }

}
