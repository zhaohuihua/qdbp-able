package com.gitee.qdbp.tools.excel.model;

import java.io.Serializable;

/**
 * 列信息
 *
 * @author zhaohuihua
 * @version 160302
 */
public class FieldInfo implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 字段名 **/
    private final String field;

    /** 是不是必填字段 **/
    private final boolean required;

    /** 第几列, 从1开始 **/
    private final Integer column;

    public FieldInfo(Integer column, String field, boolean required) {
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

    public String toString() {
        if (field == null) {
            return "null";
        } else {
            StringBuilder buffer = new StringBuilder();
            if (column != null) {
                buffer.append(column).append(":");
            }
            if (field.trim().length() == 0) {
                buffer.append("\"\"");
            } else {
                buffer.append(field);
            }
            if (required) {
                buffer.append("(*)");
            }
            return buffer.toString();
        }
    }

}
