package com.gitee.qdbp.able.jdbc.condition;

import java.util.List;
import com.gitee.qdbp.able.jdbc.base.DbCondition;
import com.gitee.qdbp.able.jdbc.base.WhereCondition;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 数据库Where条件容器<br>
 * <pre>
    DbWhere where = new DbWhere();
    // [SQL] AND ID = :$1
    where.on("id", "=", entity.getId());
    // [SQL] AND ID != :$1
    where.on("id", "!=", entity.getId());
    // [SQL] AND CREATE_TIME > :$1
    where.on("createTime", ">", new Date());
    // [SQL] AND CREATE_TIME >= :$1
    where.on("createTime", ">=", new Date());
    // [SQL] AND CREATE_TIME < :$1
    where.on("createTime", "<", new Date());
    // [SQL] AND CREATE_TIME <= :$1
    where.on("createTime", "<=", new Date());
    // [SQL] AND USER_STATE IS NULL
    where.on("userState", "is null");
    // [SQL] AND USER_STATE IS NOT NULL
    where.on("userState", "is not null");
    // [ORACLE/DB2] AND USER_NAME LIKE '%'||:$1||'%'
    // [MYSQL] AND USER_NAME LIKE CONCAT('%',:$1,'%')
    where.on("userName", "like", entity.getUserName());
    // [ORACLE/DB2] AND USER_NAME NOT LIKE '%'||:$1||'%'
    // [MYSQL] AND USER_NAME NOT LIKE CONCAT('%',:$1,'%')
    where.on("userName", "not like", entity.getUserName());
    // [ORACLE/DB2] AND PHONE LIKE :$1||'%'
    // [MYSQL] AND PHONE LIKE CONCAT(:$1,'%')
    where.on("phone", "starts", "139");
    // [ORACLE/DB2] AND PHONE LIKE '%'||:$1
    // [MYSQL] AND PHONE LIKE CONCAT('%',:$1)
    where.on("phone", "ends", "8888");
    // [SQL] AND USER_STATE IN (:$1, :$2, ...)
    where.on("userState", "in", UserState.NORMAL, UserState.LOCKED, ...);
    // [SQL] AND USER_STATE NOT IN (:$1, :$2, ...)
    where.on("userState", "not in", UserState.NORMAL, UserState.LOCKED, ...);
    // [SQL] AND CREATE_TIME BETWEEN :$1 AND :$2
    where.on("createTime", "between", entity.getStartTime(), entity.getEndTime());
    // [SQL] AND ( USER_NAME LIKE '%'||:$1||'%' OR REAL_NAME LIKE '%'||:$2||'%' OR ... )
    where.sub("or") // 子条件
        .on("userName", "like", entity.getKeyword())
        .on("realName", "like", entity.getKeyword());
 * </pre>
 *
 * @author zhaohuihua
 * @version 181221
 */
public class DbWhere extends DbItems {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 没有查询条件的空Where **/
    public static final DbWhere NONE = new ReadonlyWhere();

    /**
     * Where条件<br>
     * 字段名可以带表别名, 如where.on("u.id", "=", entity.getId());<br>
     * 
     * @param fieldName 字段名称
     * @param operate 目前支持如下操作:<br>
     *            =, !=, &lt;, &lt;=, &gt;, &gt;=, <br>
     *            Equals(equals), NotEquals(not equals), <br>
     *            LessThen(less then), LessEqualsThen(less equals then), <br>
     *            GreaterThen(greater then), GreaterEqualsThen(greater equals then), <br>
     *            IsNull(is null), IsNotNull(is not null), <br>
     *            Like(like), NotLike(not like), Starts(starts), Ends(ends), <br>
     *            In(in), NotIn(not in), Between(between)
     * @param fieldValues 字段值
     * @return 返回容器自身, 用于链式操作
     */
    public DbWhere on(String fieldName, String operate, Object... fieldValues) {
        DbField condition = parseField(fieldName, operate, fieldValues);
        this.put(condition);
        return this;
    }

    /** 增加自定义条件 **/
    public DbWhere on(WhereCondition condition) {
        super.put(condition);
        return this;
    }

    /**
     * 增加条件
     * 
     * @param condition 条件
     */
    protected void put(DbField condition) {
        VerifyTools.requireNonNull(condition, "condition");
        super.put(new Field(condition));
    }

    /**
     * 增加条件
     * 
     * @param condition 自定义条件
     */
    protected void put(DbCondition condition) {
        VerifyTools.requireNonNull(condition, "condition");
        if (condition instanceof DbField) {
            super.put(new Field((DbField) condition));
        } else {
            super.put(condition);
        }
    }

    protected Field parseField(String fieldName, String operate, Object... fieldValues) {
        VerifyTools.nvl(fieldName, "fieldName");
        Field condition = new Field();
        condition.setFieldName(fieldName);
        condition.setOperateType(operate);
        if (fieldValues != null && fieldValues.length > 0) {
            if (fieldValues.length == 1) {
                condition.setFieldValue(fieldValues[0]);
            } else {
                condition.setFieldValue(fieldValues);
            }
        }
        return condition;
    }

    /** 创建子查询条件 **/
    public SubWhere sub(String logicType) {
        return this.sub(logicType, true);
    }

    /** 创建子查询条件 **/
    public SubWhere sub(String logicType, boolean positive) {
        VerifyTools.nvl(logicType, "logicType");
        SubWhere sub = new SubWhere(this, logicType, positive);
        this.put(sub);
        return sub;
    }

    /**
     * 根据字段名称替换条件
     * 
     * @param fieldName 字段名称
     * @return 替换了几个条件
     */
    public int replace(String fieldName, String operate, Object... fieldValues) {
        DbField condition = parseField(fieldName, operate, fieldValues);
        int count = this.replace(condition);
        if (count == 0) {
            this.put(condition);
            count ++;
        }
        return count;
    }

    /**
     * 通过字段对象列表构造DbWhere对象
     * 
     * @param <T> DbWhere泛型
     * @param fields 字段对象
     * @param instanceType 实例类型
     * @return DbWhere对象
     */
    public static <T extends DbWhere> T ofFields(List<DbField> fields, Class<T> instanceType) {
        VerifyTools.requireNonNull(instanceType, "class");
        T instance;
        try {
            instance = instanceType.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to new instance for " + instanceType.getName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to new instance for " + instanceType.getName(), e);
        }
        if (fields != null && !fields.isEmpty()) {
            for (DbField field : fields) {
                instance.put(field);
            }
        }
        return instance;
    }

    public class Field extends DbField {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        private final DbWhere container;

        /** 构造函数 **/
        public Field() {
            this.container = DbWhere.this;
        }

        /** 构造函数 **/
        private Field(DbField field) {
            super(field.getOperateType(), field.getFieldName(), field.getFieldValue());
            this.container = DbWhere.this;
        }

        /** 构造函数 **/
        public Field(String fieldName, Object fieldValue) {
            super(fieldName, fieldValue);
            this.container = DbWhere.this;
        }

        /** 构造函数 **/
        public Field(String operateType, String fieldName, Object fieldValue) {
            super(operateType, fieldName, fieldValue);
            this.container = DbWhere.this;
        }

        public DbWhere getContainer() {
            return container;
        }
    }

    /**
     * 允许为空的查询条件
     *
     * @author zhaohuihua
     * @version 20200206
     */
    public static class EmptiableWhere extends DbWhere {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;
    }

    /**
     * 只读Where条件
     *
     * @author zhaohuihua
     * @version 190310
     */
    private static class ReadonlyWhere extends EmptiableWhere {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        private ReadonlyWhere() {
        }

        @Override
        protected void put(String fieldName, Object fieldValue) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        protected void put(String operateType, String fieldName, Object fieldValue) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        protected void put(DbField condition) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        protected void put(DbConditions fields) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        protected void put(DbCondition condition) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public SubWhere sub(String operate) {
            throw new UnsupportedOperationException("read only");
        }

    }
}
