package com.gitee.zhaohuihua.tools.wait;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单条件等待锁
 *
 * @author zhaohuihua
 * @version 170815
 */
public class SingleLock {
    
    private static final Logger log = LoggerFactory.getLogger(SingleLock.class);

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    /** 等待 **/
    public void await() {
        try {
            lock.lock(); // 获取锁
            condition.await(); // 等待
        } catch (InterruptedException e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    /**
     * 等待
     *
     * @param time 超时时间
     * @return false if the waiting time detectably elapsed before return from the method, else true
     */
    public boolean await(long time) {
        try {
            lock.lock(); // 获取锁
            return condition.await(time, TimeUnit.MILLISECONDS); // 等待
        } catch (InterruptedException e) {
            log.error("InterruptedException", e);
            // Restore the interrupted status
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    /** 唤醒 **/
    public void signal() {
        try {
            lock.lock(); // 获取锁
            condition.signal();
        } finally {
            lock.unlock(); // 释放锁
        }
    }
}
