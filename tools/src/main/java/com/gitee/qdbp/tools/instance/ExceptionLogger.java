package com.gitee.qdbp.tools.instance;

import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.able.exception.ExceptionWatcher;

/**
 * 只记录警告日志的异常处理类<br>
 * 忽略异常, 只记录警告日志, 发生异常后继续处理
 *
 * @author zhaohuihua
 * @version 190622
 */
public class ExceptionLogger implements ExceptionWatcher {

    /** 记录简单日志的实例 **/
    public static final ExceptionLogger SIMPLE = new ExceptionLogger(false);
    /** 记录详细日志的实例 **/
    public static final ExceptionLogger DETAILS = new ExceptionLogger(true);

    /**
     * 生成一个只记录警告日志的实例
     * 
     * @param details 是否记录StackTrace
     * @return 异常处理类
     */
    public static ExceptionLogger newLogWatcher(boolean details) {
        return new ExceptionLogger(details);
    }

    /**
     * 生成一个只记录警告日志的实例
     * 
     * @param details 是否记录StackTrace
     * @param clazz the logger will be named after clazz
     * @return 异常处理类
     */
    public static ExceptionLogger newLogWatcher(boolean details, Class<?> clazz) {
        return new ExceptionLogger(details, clazz);
    }

    /**
     * 生成一个只记录警告日志的实例
     * 
     * @param details
     * @param loggerName
     * @return 异常处理类
     */
    public static ExceptionLogger newLogWatcher(boolean details, String loggerName) {
        return new ExceptionLogger(details, loggerName);
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

    /**
     * 生成一个记录警告日志并统计失败次数的实例
     * 
     * @param details 是否记录StackTrace
     * @param clazz the logger will be named after clazz
     * @return 异常处理类
     */
    public static CountLogWatcher newCountWatcher(boolean details, Class<?> clazz) {
        return new CountLogWatcher(details, clazz);
    }

    /**
     * 生成一个记录警告日志并统计失败次数的实例
     * 
     * @param details
     * @param loggerName
     * @return 异常处理类
     */
    public static CountLogWatcher newCountWatcher(boolean details, String loggerName) {
        return new CountLogWatcher(details, loggerName);
    }

    /** 默认日志对象 **/
    private static final Logger DEF_LOGGER = LoggerFactory.getLogger(ExceptionLogger.class);
    /** 日志对象 **/
    private final Logger log;
    /** 是否记录StackTrace **/
    private final boolean details;

    /**
     * 构造函数
     * 
     * @param details 是否记录StackTrace
     */
    protected ExceptionLogger(boolean details) {
        this.details = details;
        this.log = DEF_LOGGER;
    }

    /**
     * 构造函数
     * 
     * @param details 是否记录StackTrace
     * @param clazz the logger will be named after clazz
     */
    protected ExceptionLogger(boolean details, Class<?> clazz) {
        this.details = details;
        this.log = LoggerFactory.getLogger(clazz);
    }

    /**
     * 构造函数
     * 
     * @param details 是否记录StackTrace
     * @param loggerName the name of the logger
     */
    protected ExceptionLogger(boolean details, String loggerName) {
        this.details = details;
        this.log = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public boolean onCaughtException(String message, Throwable e) {
        if (log == null) {
            System.out.println(message);
            if (details) {
                e.printStackTrace();
            }
        } else {
            if (details) {
                log.warn(message, e); // 记录详细警告日志
            } else {
                log.warn(message); // 记录简要警告日志
            }
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
    public static class CountLogWatcher extends ExceptionLogger {

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

        /**
         * 构造函数
         * 
         * @param details 是否记录StackTrace
         * @param clazz the logger will be named after clazz
         */
        protected CountLogWatcher(boolean details, Class<?> clazz) {
            super(details, clazz);
        }

        /**
         * 构造函数
         * 
         * @param details 是否记录StackTrace
         * @param loggerName the name of the logger
         */
        protected CountLogWatcher(boolean details, String loggerName) {
            super(details, loggerName);
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
