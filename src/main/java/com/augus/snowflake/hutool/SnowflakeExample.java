package com.augus.snowflake.hutool;

import cn.hutool.core.util.IdUtil;

public class SnowflakeExample {
    public static void main(String[] args) {
        // 创建一个Snowflake实例，传入数据中心ID和机器ID
        long workerId = 1;  // 工作节点ID (0-31)
        long datacenterId = 1;  // 数据中心ID (0-31)
        cn.hutool.core.lang.Snowflake snowflake = IdUtil.getSnowflake(workerId, datacenterId);

        // 生成一个唯一的ID
        long id = snowflake.nextId();
        System.out.println("Generated ID: " + id);

        // 生成另一个唯一的ID
        long anotherId = snowflake.nextId();
        System.out.println("Generated Another ID: " + anotherId);
    }
}
