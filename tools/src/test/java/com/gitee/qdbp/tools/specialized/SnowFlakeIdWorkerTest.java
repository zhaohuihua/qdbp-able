package com.gitee.qdbp.tools.specialized;


public class SnowFlakeIdWorkerTest {

    public static void main(String[] args) {
        SnowFlakeIdWorker worker = new SnowFlakeIdWorker(0, 0);
        for (int i = 0; i < 30; i++) {
            System.out.println(worker.nextId());
        }
    }
}
