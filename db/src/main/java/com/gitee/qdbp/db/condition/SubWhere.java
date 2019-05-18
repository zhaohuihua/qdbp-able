package com.gitee.qdbp.db.condition;

import com.gitee.qdbp.able.utils.VerifyTools;

/**
 * 子查询条件
 *
 * @author zhaohuihua
 * @version 190205
 */
public class SubWhere extends DbWhere {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 所属上级 **/
    private DbWhere parent;
    /** 逻辑联连类型: AND / OR **/
    private String logicType;
    /** 是否为肯定条件 **/
    private boolean positive;

    public SubWhere(DbWhere parent, String logicType) {
        this(parent, logicType, true);
    }

    public SubWhere(DbWhere parent, String logicType, boolean positive) {
        this.parent = parent;
        this.positive = positive;
        if (VerifyTools.isBlank(logicType)) {
            throw new IllegalArgumentException("Logic type can't be blank.");
        }

        if ("AND".equalsIgnoreCase(logicType)) {
            this.logicType = "AND";
        } else if ("OR".equalsIgnoreCase(logicType)) {
            this.logicType = "OR";
        } else {
            String msg = "Unsupported logic type: " + logicType + ". acceptable values are: 'AND', 'OR'.";
            throw new IllegalArgumentException(msg);
        }
    }

    /** 返回上级 **/
    public DbWhere end() {
        return this.parent;
    }

    /** 逻辑联连类型: AND / OR **/
    public String getLogicType() {
        return logicType;
    }

    /** 逻辑联连类型: AND / OR **/
    public void setLogicType(String logicType) {
        this.logicType = logicType;
    }

    /** 是否为肯定条件 **/
    public boolean isPositive() {
        return positive;
    }

    /** 是否为肯定条件 **/
    public void setPositive(boolean positive) {
        this.positive = positive;
    }
}
