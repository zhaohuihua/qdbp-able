package com.gitee.zhaohuihua.common.wait;

import java.util.concurrent.TimeoutException;

import com.gitee.zhaohuihua.tools.utils.RandomTools;
import com.gitee.zhaohuihua.tools.wait.WaitLock;
import com.gitee.zhaohuihua.tools.wait.WaitLocks;

/**
 * WaitLock测试
 *
 * @author zhaohuihua
 * @version 170406
 */
public class WaitLocksTest {

    public static void main(String[] args) {

        final WaitLocks locks = new WaitLocks();

        for (int i = 1; i <= 5; i++) {
            new Wait(locks, -i).start();
        }

        for (int i = 0; i < 100; i++) {
            new Wait(locks, i).start();
        }

        new Thread() {

            public void run() {
                for (int i = 0; i < 100; i += 2) {
                    locks.trySignal(i);
                    try {
                        Thread.sleep(RandomTools.generateNumber(10, 99));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }.start();

        new Thread() {

            public void run() {
                for (int i = 1; i < 100; i += 2) {
                    locks.trySignal(i);
                    try {
                        Thread.sleep(RandomTools.generateNumber(10, 99));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }.start();

        new Thread() {

            public void run() {
                for (int i = 1; i < 150; i++) {
                    int size = locks.size();
                    System.out.println("locks.size() = " + size);
                    if (size == 0) {
                        break;
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }.start();
    }

    private static class Wait extends Thread {

        private WaitLocks locks;
        private Integer index;

        public Wait(WaitLocks locks, int index) {
            this.locks = locks;
            this.index = index;
        }

        public void run() {
            long s = System.currentTimeMillis();
            try {
                Number v = locks.await(new Equals(index), 5000);
                long t = System.currentTimeMillis() - s;
                System.out.println("SUCC, vlaue: " + v + ", time:" + t + "ms.");
            } catch (TimeoutException e) {
                long t = System.currentTimeMillis() - s;
                System.out.println("ERROR, index: " + index + ", time:" + t + "ms. " + e.getMessage());
            }
        }
    }

    private static class Equals implements WaitLock.Checker<Integer> {

        private Integer result;

        public Equals(Integer result) {
            this.result = result;
        }

        @Override
        public Integer check(Object result) {
            return this.result.equals(result) ? (Integer) result : null;
        }

    }
}
