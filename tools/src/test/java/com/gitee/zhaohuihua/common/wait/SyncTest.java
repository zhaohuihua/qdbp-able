package com.gitee.zhaohuihua.common.wait;

import java.util.HashMap;
import java.util.Map;

public class SyncTest {

    private static class SingleService {

        private boolean sync;

        private Map<String, Boolean> handled = new HashMap<>();

        public SingleService(boolean sync) {
            this.sync = sync;
        }

        public void doSomeThing(String id, int times) {
            System.out.println(id + "-" + times + " before synchronized");
            if (this.sync) {
                synchronized (id.intern()) {
                    handle(id, times);
                }
            } else {
                handle(id, times);
            }
            System.out.println(id + "-" + times + " after synchronized");
        }

        private void handle(String id, int times) {
            if (handled.containsKey(id)) {
                System.out.println(id + "-" + times + " <<<error>>>");
                return;
            }
            System.out.println(id + "-" + times + " before handle ...");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            System.out.println(id + "-" + times + " after handle ...");
            handled.put(id, true);
        }
    }

    private static class Runner extends Thread {

        private SingleService service;
        private String id;
        private int times;

        public Runner(SingleService service, String id, int times) {
            this.service = service;
            this.id = id;
            this.times = times;
        }

        public void run() {
            service.doSomeThing(id, times);
        }
    }

    public static void main(String[] args) {
        test(true);
    }

    private static void test(boolean sync) {

        SingleService service = new SingleService(sync);
        for (int i = 0; i < 3; i++) {
            String id = String.valueOf(1001 + i);
            for (int j = 0; j < 3; j++) {
                new Runner(service, id, j).start();
            }
        }
    }

}
