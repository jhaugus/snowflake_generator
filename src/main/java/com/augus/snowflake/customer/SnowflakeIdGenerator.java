package com.augus.snowflake.customer;

import java.sql.Time;
import java.util.Locale;

public class SnowflakeIdGenerator {
    // 基准时间戳（可以根据实际需要进行调整）
    private final static long twepoch = 1723409329878L;

    // 机器ID所占的位数
    private final static long workerIdBits = 5L;

    // 数据中心ID所占的位数
    private final static long datacenterIdBits = 5L;

    // 支持的最大机器ID，结果是31 (0b11111)
    private final static long maxWorkerId = -1L ^ (-1L << workerIdBits);

    // 支持的最大数据中心ID，结果是31 (0b11111)
    private final static long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    // 序列在ID中占的位数
    private final static long sequenceBits = 12L;

    // 机器ID向左移12位
    private final static long workerIdShift = sequenceBits;

    // 数据中心ID向左移17位
    private final static long datacenterIdShift = sequenceBits + workerIdBits;

    // 时间戳向左移22位
    private final static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 工作节点ID
    private long workerId;

    // 数据中心ID
    private long datacenterId;

    // 毫秒内序列(0~4095)
    private long sequence = 0L;

    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // 生成ID
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            // 如果在同一毫秒内生成多个ID
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 序列号溢出，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 如果是新的毫秒，重置序列号
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    // 等待下一毫秒
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    // 获取当前时间戳
    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);

        for (int i = 0; i < 10; i++) {
            System.out.println(idGenerator.nextId());
        }
    }
}
