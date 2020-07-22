package com.gitee.qdbp.able.jdbc.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 带主键的实体对象
 *
 * @author zhaohuihua
 * @version 20200705
 */
public class PkEntity implements Serializable {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 主键值 **/
    private String primaryKey;
    /** 实体内容 **/
    private Map<String, Object> entity;

    public PkEntity() {
    }

    public PkEntity(String primaryKey, Map<String, Object> entity) {
        this.primaryKey = primaryKey;
        this.entity = entity;
    }

    /** 主键值 **/
    public String getPrimaryKey() {
        return primaryKey;
    }

    /** 主键值 **/
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    /** 实体内容 **/
    public Map<String, Object> getEntity() {
        return entity;
    }

    /** 实体内容 **/
    public void setEntity(Map<String, Object> entity) {
        this.entity = entity;
    }

}
