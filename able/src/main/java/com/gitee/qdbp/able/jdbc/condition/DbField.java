package com.gitee.qdbp.able.jdbc.condition;

import java.io.Serializable;
import com.gitee.qdbp.able.beans.Copyable;
import com.gitee.qdbp.able.jdbc.base.DbCondition;
import com.gitee.qdbp.able.jdbc.utils.FieldTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 数据库字段
 *
 * @author zhaohuihua
 * @version 181221
 */
public class DbField implements DbCondition, Copyable, Serializable {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 字段名 **/
    private String fieldName;
    /** 字段值 **/
    private Object fieldValue;
    /** 操作类型 **/
    private String operateType;

    /** 构造函数 **/
    public DbField() {
    }

    @Override
    public boolean isEmpty() {
        return VerifyTools.isBlank(fieldName);
    }

    /** 构造函数 **/
    public DbField(String fieldName, Object fieldValue) {
        VerifyTools.requireNotBlank(fieldName, "fieldName");
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /** 构造函数 **/
    public DbField(String operateType, String fieldName, Object fieldValue) {
        VerifyTools.requireNotBlank(fieldName, "fieldName");
        this.operateType = operateType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /** 字段名 **/
    public String getFieldName() {
        return fieldName;
    }

    /** 字段名 **/
    public void setFieldName(String fieldName) {
        VerifyTools.requireNotBlank(fieldName, "fieldName");
        this.fieldName = fieldName;
    }

    /** 字段值 **/
    public Object getFieldValue() {
        return fieldValue;
    }

    /** 字段值 **/
    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    /** 操作类型 **/
    public String getOperateType() {
        return operateType;
    }

    /** 操作类型 **/
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * 与目标字段是否匹配<br>
     * 如果当前字段名或目标字段名没有表别名, 只要字段名匹配即为匹配<br>
     * 如果当前字段名和目标字段名都有表别名, 则需要表别名和字段名同时匹配
     * 
     * @param fieldName 目标字段名, 可带表别名, 如: u.userName
     * @return 是否匹配
     */
    public boolean matchesWithField(String fieldName) {
        return FieldTools.matches(this.fieldName, fieldName);
    }

    @Override
    public DbField copy() {
        DbField newer = new DbField();
        newer.setFieldName(this.getFieldName());
        newer.setOperateType(this.getOperateType());
        newer.setFieldValue(this.getFieldValue());
        return newer;
    }
}
