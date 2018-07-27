package com.gitee.qdbp.tools.specialized;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TwitterSnowFlake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位符号位: 不用。二进制中最高位为1的都是负数, 但是我们生成的id一般都使用整数, 所以这个最高位固定是0<br>
 * 41位时间戳(毫秒级)<br>
 * -- 41位可以表示241−1个数字, <br>
 * -- 如果只用来表示正整数(计算机中正数包含0), 可以表示的数值范围是：0 至 241−1, 减1是因为可表示的数值范围是从0开始算的, 而不是1。<br>
 * -- 也就是说41位可以表示241−1个毫秒的值, 转化成单位年则是(241−1)/(1000∗60∗60∗24∗365)=69年<br>
 * 10位的机器id:<br>
 * -- 可以部署在210=1024个节点, 包括5位datacenterId和5位workerId<br>
 * -- 5位(bit)可以表示的最大正整数是25−1=31, 即可以用0、1、2、3、....31这32个数字, 来表示不同的datecenterId或workerId<br>
 * 12位序列号:<br>
 * -- 用来记录同毫秒内产生的不同id。<br>
 * -- 12位(bit)可以表示的最大正整数是212−1=4096, 即可以用0、1、2、3、....4095这4096个数字, 来表示同一机器同一时间戳(毫秒)内产生的4096个ID序号<br>
 * 加起来刚好64位, 在Java中即为一个Long型。<br>
 * 注意:<br>
 * -- 41位时间戳不是存储当前时间的时间戳, 而是存储时间戳的差值(当前时间戳-开始时间戳得到的值), <br>
 * -- 这里的的开始时间戳, 一般是我们的id生成器开始使用的时间(通过epoch属性指定)。<br>
 * SnowFlake可以保证: <br>
 * -- 所有生成的id按时间趋势递增<br>
 * -- 整个分布式系统内不会产生重复id(因为有datacenterId和workerId来做区分)<br>
 *
 * @author zhaohuihua copy from https://segmentfault.com/a/1190000011282426
 */
public class SnowFlakeIdWorker {

    /** 日志对象 **/
    private static final Logger log = LoggerFactory.getLogger(SnowFlakeIdWorker.class);

    private final long epoch;
    private long workerId;
    private long datacenterId;
    private long sequence;

    public SnowFlakeIdWorker(long workerId, long datacenterId) {
        // 2010-01-01T00:00Z[UTC]
        this(1262304000000L, workerId, datacenterId);
    }
    public SnowFlakeIdWorker(long epoch, long workerId, long datacenterId) {
        if (epoch < 0) {
            throw new IllegalArgumentException("epoch can't be less than 0");
        } else if (epoch > System.currentTimeMillis()) {
            throw new IllegalArgumentException("epoch can't be greater than current timestamp");
        }
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            String msg = String.format("worker Id can't be greater than %d or less than 0", maxWorkerId);
            throw new IllegalArgumentException(msg);
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            String msg = String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId);
            throw new IllegalArgumentException(msg);
        }
        if (log.isDebugEnabled()) {
            String msg = "SnowFlake Starting. timestamp left shift {}, datacenter id bits {}, worker id bits {}, sequence bits {}, workerid {}";
            log.debug(msg, timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);
        }

        this.epoch = epoch;
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    private long workerIdBits = 5L;
    private long datacenterIdBits = 5L;
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private long sequenceBits = 12L;

    private long workerIdShift = sequenceBits;
    private long datacenterIdShift = sequenceBits + workerIdBits;
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    public long getWorkerId() {
        return workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }
    
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            log.error("clock is moving backwards.  Rejecting requests until {}.", lastTimestamp);
            long millis = lastTimestamp - timestamp;
            String msg = String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", millis);
            throw new RuntimeException(msg);
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - epoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)  | (workerId << workerIdShift) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        long epoch = 1262304000000L; // 2010-01-01T00:00Z[UTC]
        SnowFlakeIdWorker worker = new SnowFlakeIdWorker(epoch, 0, 0);
        for (int i = 0; i < 30; i++) {
            System.out.println(worker.nextId());
        }
    }

}