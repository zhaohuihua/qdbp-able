package com.gitee.zhaohuihua.common.wait;

import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.tools.wait.WaitLock;

/**
 * WaitLock测试
 *
 * @author zhaohuihua
 * @version 170406
 */
public class WaitLockTest {

    private static Logger log = LoggerFactory.getLogger(WaitLockTest.class);

    public static void main(String[] args) {
        Object value = 1234.56;
        WaitLock<Number> lock = new WaitLock<>(Number.class);
        new Factory(lock, "string x", 1000).start();
        new Factory(lock, "string y", 1200).start();
        new Factory(lock, value, 2000).start();
        new Factory(lock, "string z", 2500).start();

        log.trace("await ...");
        Number result = lock.await();
        log.trace("result === " + result);
        assert result == value;

        try {
            long time = 5000;
            log.trace("await {} ...", time);
            lock.await(time);
        } catch (Exception e) {
            log.trace(e.toString());
            assert TimeoutException.class == e.getClass();
        }
    }

    private static class Factory extends Thread {

        private static Logger log = LoggerFactory.getLogger(WaitLockTest.class);

        private WaitLock<?> lock;
        private Object result;
        private Integer wait;

        public Factory(WaitLock<?> lock, Object result, int wait) {
            this.lock = lock;
            this.result = result;
            this.wait = wait;
        }

        public void run() {
            try {
                Thread.sleep(wait);
                log.trace("signal --> " + result);
                lock.trySignal(result);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
