package com.gitee.qdbp.able.jdbc.model;

/**
 * 数据库原生值(sysdate, CURRENT_TIMESTAMP等)
 *
 * @author zhaohuihua
 * @version 20200708
 */
public class DbRawValue {

    private String value;

    public DbRawValue(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
