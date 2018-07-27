package com.gitee.zhaohuihua.common.wait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.tools.wait.SingleLock;

/**
 * WaitLock测试
 *
 * @author zhaohuihua
 * @version 170406
 */
public class SingleLockTest {

    private static Logger log = LoggerFactory.getLogger(SingleLockTest.class);

    public static void main(String[] args) {
        SingleLock lock = new SingleLock();
        new Factory(lock, 1000).start();

        log.trace("await start ...");
        lock.await();
        log.trace("await end ...");

        long time = 5000;
        log.trace("await {} ...", time);
        boolean signaled = lock.await(time);
        log.trace("await {} {} ...", time, signaled ? "end" : "timeout");
    }

    private static class Factory extends Thread {

        private static Logger log = LoggerFactory.getLogger(SingleLockTest.class);

        private SingleLock lock;
        private Integer wait;

        public Factory(SingleLock lock, int wait) {
            this.lock = lock;
            this.wait = wait;
        }

        public void run() {
            try {
                Thread.sleep(wait);
                log.trace("signal");
                lock.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
