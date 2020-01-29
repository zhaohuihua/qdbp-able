package com.gitee.qdbp.able.jdbc.model;

import java.io.Serializable;

/**
 * 字段名<br>
 * 通常情况下, WHERE条件的运算符后面是字段值, 指定字段名/运算符/字段值的关系<br>
 * 有些情况需要指定字段名与另一个字段名的关系<br>
 * 例如, 查询实际完成时间大于计划完成时间的记录:<br>
 * where.on("actualCompleteTime", "&gt;", new DbFieldName("plannedCompleteTime"));
 *
 * @author zhaohuihua
 * @version 200123
 */
public class DbFieldName implements Serializable {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;
    private String fieldName;

    public DbFieldName() {
    }

    public DbFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String toString() {
        return fieldName;
    }
}
