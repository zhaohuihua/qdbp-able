package com.gitee.qdbp.tools.excel.model;

import java.io.Serializable;


/**
 * 列信息
 *
 * @author zhaohuihua
 * @version 160302
 */
public class ColumnInfo implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 字段名 **/
    private final String field;

    /** 是不是必填字段 **/
    private final boolean required;

    /** 第几列, 从1开始 **/
    private final Integer column;

    public ColumnInfo(Integer column, String field, boolean required) {
        this.column = column;
        this.field = field;
        this.required = required;
    }

    /** 获取字段名 **/
    public String getField() {
        return field;
    }

    /** 是不是必填字段 **/
    public boolean isRequired() {
        return required;
    }

    /** 获取第几列, 从1开始 **/
    public Integer getColumn() {
        return column;
    }

}
