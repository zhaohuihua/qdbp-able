package com.gitee.qdbp.able.jdbc.condition;

import com.gitee.qdbp.able.jdbc.base.WhereCondition;
import com.gitee.qdbp.tools.utils.VerifyTools;

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

    protected SubWhere() {
    }

    public SubWhere(DbWhere parent, String logicType) {
        this(parent, logicType, true);
    }

    public SubWhere(DbWhere parent, String logicType, boolean positive) {
        VerifyTools.requireNotBlank(logicType, "logicType");
        this.parent = parent;
        this.positive = positive;

        if ("AND".equalsIgnoreCase(logicType)) {
            this.logicType = "AND";
        } else if ("OR".equalsIgnoreCase(logicType)) {
            this.logicType = "OR";
        } else {
            String msg = "Unsupported logic type: " + logicType + ". acceptable values are: 'AND', 'OR'.";
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public SubWhere on(String fieldName, String operate, Object... fieldValues) {
        super.on(fieldName, operate, fieldValues);
        return this;
    }

    /** 增加自定义条件 **/
    @Override
    public SubWhere on(WhereCondition condition) {
        super.put(condition);
        return this;
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

    /** {@inheritDoc} **/
    @Override
    public SubWhere copy() {
        SubWhere copies = new SubWhere();
        this.copyTo(copies);
        return copies;
    }

    protected SubWhere copy(DbWhere parent) {
        SubWhere copies = this.copy();
        copies.parent = parent;
        return copies;
    }

    protected void copyTo(SubWhere copies) {
        super.copyTo(copies);
        copies.parent = this.parent;
        copies.logicType = this.logicType;
        copies.positive = this.positive;
    }

    /** {@inheritDoc} **/
    @Override
    protected SubWhere newCopies() {
        if (this.getClass() == SubWhere.class) { // 当前类
            return new SubWhere();
        } else { // 子类
            try {
                return this.getClass().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("CloneNotSupported, FailedToInvokeDefaultConstructor.");
            }
        }
    }
}
