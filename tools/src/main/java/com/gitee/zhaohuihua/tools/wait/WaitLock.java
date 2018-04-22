package com.gitee.zhaohuihua.tools.wait;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 等待结果
 *
 * @author zhaohuihua
 * @version 170406
 */
public class WaitLock<T> {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private T result;

    private Checker<? extends T> checker;

    public WaitLock(Class<? extends T> clazz) {
        this.checker = new DefaultChecker<>(clazz);
    }

    public WaitLock(Checker<T> checker) {
        this.checker = checker;
    }

    /** 等待结果 **/
    public T await() {
        try {
            lock.lock(); // 获取锁
            condition.await(); // 等待
            return result;
        } catch (InterruptedException e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
            return result;
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    /**
     * 等待结果
     *
     * @param time 超时时间
     * @return 结果
     * @throws TimeoutException
     */
    public T await(long time) throws TimeoutException {
        try {
            lock.lock(); // 获取锁
            boolean signaled = condition.await(time, TimeUnit.MILLISECONDS); // 等待
            if (signaled) {
                return result;
            } else {
                throw new TimeoutException("Wait timeout.");
            }
        } catch (InterruptedException e) {
            // Restore the interrupted status
            Thread.currentThread().interrupt();
            return result;
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    /**
     * 尝试通知, 如果检查通过则通知等待者
     * 
     * @param result 结果
     * @return 检查是否通过
     */
    public boolean trySignal(Object result) {
        if (this.result != null) {
            return false;
        }
        T target = checker.check(result);
        if (target != null) {
            try {
                lock.lock(); // 获取锁
                this.result = target;
                condition.signal();
                return true;
            } finally {
                lock.unlock(); // 释放锁
            }
        }
        return false;
    }

    /**
     * 检查结果是否符合要求
     *
     * @author zhaohuihua
     * @version 170406
     */
    public static interface Checker<T> {

        T check(Object result);
    }

    /**
     * 检查结果的默认实现类
     *
     * @author zhaohuihua
     * @version 170406
     */
    private static class DefaultChecker<T> implements Checker<T> {

        private Class<T> clazz;

        public DefaultChecker(Class<T> clazz) {
            this.clazz = clazz;
        }

        public T check(Object result) {
            if (clazz.isAssignableFrom(result.getClass())) {
                @SuppressWarnings("unchecked")
                T target = (T) result;
                return target;
            } else {
                return null;
            }
        }
    }
}
