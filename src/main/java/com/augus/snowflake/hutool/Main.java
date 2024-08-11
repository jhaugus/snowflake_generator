package com.augus.snowflake.hutool;

import cn.hutool.core.util.IdUtil;

import java.sql.Time;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        long id = IdUtil.getSnowflake(1, 1).nextId();
        System.out.println(id);
    }
}
