package com.gitee.qdbp.able.jdbc.model;

import java.io.Serializable;
import com.gitee.qdbp.able.jdbc.condition.DbUpdate;

/**
 * 带主键的更新对象
 *
 * @author zhaohuihua
 * @version 20200705
 */
public class PkUpdate implements Serializable {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 主键 **/
    private String primaryKey;
    /** 更新的内容 **/
    private DbUpdate update;

    public PkUpdate() {
    }

    public PkUpdate(String primaryKey, DbUpdate update) {
        this.primaryKey = primaryKey;
        this.update = update;
    }

    /** 主键 **/
    public String getPrimaryKey() {
        return primaryKey;
    }

    /** 主键 **/
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    /** 更新的内容 **/
    public DbUpdate getUpdate() {
        return update;
    }

    /** 更新的内容 **/
    public void setUpdate(DbUpdate update) {
        this.update = update;
    }

}
