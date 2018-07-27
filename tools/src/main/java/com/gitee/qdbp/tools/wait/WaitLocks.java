package com.gitee.zhaohuihua.tools.wait;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeoutException;
import com.gitee.zhaohuihua.tools.wait.WaitLock.Checker;

/**
 * 等待结果
 *
 * @author zhaohuihua
 * @version 170406
 */
public class WaitLocks {

    protected List<WaitLock<?>> waiters;

    public WaitLocks() {
        this.waiters = new Vector<>();
    }

    /**
     * 等待结果
     *
     * @param clazz 结果的类型
     * @param time 超时时间
     * @return 结果
     * @throws TimeoutException
     */
    public <T> T await(Class<? extends T> clazz, long time) throws TimeoutException {
        return this.await(new WaitLock<T>(clazz), time);
    }

    /**
     * 等待结果
     *
     * @param checker 检查结果的检查器
     * @param time 超时时间
     * @return 结果
     * @throws TimeoutException
     */
    public <T> T await(Checker<T> checker, long time) throws TimeoutException {
        return this.await(new WaitLock<T>(checker), time);
    }

    protected <T> T await(WaitLock<T> waiter, long time) throws TimeoutException {
        this.waiters.add(waiter);
        try {
            return waiter.await(time);
        } finally {
            this.waiters.remove(waiter);
        }
    }

    /**
     * 尝试通知, 如果检查通过则通知等待者
     * 
     * @param result 结果
     */
    public void trySignal(Object result) {
        synchronized (waiters) {
            Iterator<WaitLock<?>> iterator = waiters.iterator();
            while (iterator.hasNext()) {
                WaitLock<?> i = iterator.next();
                i.trySignal(result);
            }
        }
    }

    public int size() {
        return this.waiters.size();
    }
}
