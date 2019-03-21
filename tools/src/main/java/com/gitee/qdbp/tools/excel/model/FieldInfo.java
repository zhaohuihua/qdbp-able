package com.gitee.qdbp.tools.excel.model;

import java.io.Serializable;
import com.gitee.qdbp.able.utils.VerifyTools;

/**
 * 字段信息
 *
 * @author zhaohuihua
 * @version 160302
 */
public class FieldInfo implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 字段名 **/
    private String field;

    /** 是不是必填字段 **/
    private boolean required;

    /** 列序号, 从1开始 **/
    private Integer column;

    protected FieldInfo() {
    }

    public FieldInfo(Integer column, String field, boolean required) {
        this.column = column;
        this.field = field;
        this.required = required;
    }

    /** 设置字段名 **/
    public void setField(String field) {
        this.field = field;
    }

    /** 获取字段名 **/
    public String getField() {
        return field;
    }

    /** 设置是不是必填字段 **/
    public void setRequired(boolean required) {
        this.required = required;
    }

    /** 是不是必填字段 **/
    public boolean isRequired() {
        return required;
    }

    /** 设置列序号, 从1开始 **/
    public void setColumn(Integer column) {
        this.column = column;
    }

    /** 获取列序号, 从1开始 **/
    public Integer getColumn() {
        return column;
    }

    /**
     * 将当前对象转换为子类对象
     *
     * @param clazz 目标类型
     * @return 目标对象
     */
    public <T extends FieldInfo> T to(Class<T> clazz) {
        T instance;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create " + clazz.getSimpleName() + " instance.", e);
        }

        instance.setField(this.getField()); // 字段名
        instance.setColumn(this.getColumn()); // 列序号
        instance.setRequired(this.isRequired()); // 是否必填
        return instance;
    }

    public String toString() {
        if (field == null) {
            return "null";
        } else {
            StringBuilder buffer = new StringBuilder();
            if (VerifyTools.isBlank(this.getField())) {
                buffer.append("{UNKNOWN}");
            } else {
                buffer.append(this.getField());
            }
            if (this.getColumn() != null) {
                buffer.append('[').append(this.getColumn()).append("]");
            }
            if (this.isRequired()) {
                buffer.append("(*)");
            }
            return buffer.toString();
        }
    }

}
