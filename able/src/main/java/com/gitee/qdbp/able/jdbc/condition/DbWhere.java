package com.gitee.qdbp.able.jdbc.condition;

import java.util.Map;
import com.gitee.qdbp.able.jdbc.base.DbCondition;
import com.gitee.qdbp.able.jdbc.base.WhereCondition;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 数据库Where条件容器<br>
 * <pre>
    DbWhere where = new DbWhere();
    // [SQL] AND ID = :$1$Id
    where.on("id", "=", entity.getId());
    // [SQL] AND ID != :$1$Id
    where.on("id", "!=", entity.getId());
    // [SQL] AND CREATE_TIME > :$1$CreateTime
    where.on("createTime", ">", new Date());
    // [SQL] AND CREATE_TIME >= :$1$CreateTime
    where.on("createTime", ">=", new Date());
    // [SQL] AND CREATE_TIME < :$1$CreateTime
    where.on("createTime", "<", new Date());
    // [SQL] AND CREATE_TIME <= :$1$CreateTime
    where.on("createTime", "<=", new Date());
    // [SQL] AND USER_STATE IS NULL
    where.on("userState", "is null");
    // [SQL] AND USER_STATE IS NOT NULL
    where.on("userState", "is not null");
    // [ORACLE/DB2] AND USER_NAME LIKE '%'||:$1$UserName||'%'
    // [MYSQL] AND USER_NAME LIKE CONCAT('%',:$1$UserName,'%')
    where.on("userName", "like", entity.getUserName());
    // [ORACLE/DB2] AND USER_NAME NOT LIKE '%'||:$1$UserName||'%'
    // [MYSQL] AND USER_NAME NOT LIKE CONCAT('%',:$1$UserName,'%')
    where.on("userName", "not like", entity.getUserName());
    // [ORACLE/DB2] AND PHONE LIKE :$1$Phone||'%'
    // [MYSQL] AND PHONE LIKE CONCAT(:$1$Phone,'%')
    where.on("phone", "starts", "139");
    // [ORACLE/DB2] AND PHONE LIKE '%'||:$1$Phone
    // [MYSQL] AND PHONE LIKE CONCAT('%',:$1$Phone)
    where.on("phone", "ends", "8888");
    // [SQL] AND EFTFLAG IN (:$1$Eftflag, :$2$Eftflag, ...)
    where.on("eftflag", "in", 'E', 'N', ...);
    // [SQL] AND EFTFLAG NOT IN (:$1$Eftflag, :$2$Eftflag, ...)
    where.on("eftflag", "not in", 'E', 'N', ...);
    // [SQL] AND CREATE_TIME BETWEEN :$1$CreateTime AND :$2$CreateTime
    where.on("createTime", "between", entity.getStartTime(), entity.getEndTime());
    // [SQL] AND ( USER_NAME LIKE '%'||:$1$UserName||'%' OR REAL_NAME LIKE '%'||:$2$realName||'%' OR ... )
    where.sub("or") // 子条件
        .on("userName", "like", entity.getKeyword())
        .on("realName", "like", entity.getKeyword())
        .on(...);
 * </pre>
 *
 * @author zhaohuihua
 * @version 181221
 */
public class DbWhere extends DbItems {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 没有查询条件的空Where **/
    public static final DbWhere NONE = new EmptyDbWhere();

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
        DbField condition = parseCondition(fieldName, operate, fieldValues);
        this.put(condition);
        return this;
    }

    /** 增加自定义条件 **/
    public DbWhere on(WhereCondition condition) {
        this.put(condition);
        return this;
    }

    private static DbField parseCondition(String fieldName, String operate, Object... fieldValues) {
        VerifyTools.nvl(fieldName, "fieldName");
        DbField condition = new DbField();
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
     */
    public void replace(String fieldName, String operate, Object... fieldValues) {
        DbField condition = parseCondition(fieldName, operate, fieldValues);
        this.replace(condition);
    }

    /**
     * 从map中获取参数构建对象
     * 
     * @param map Map参数
     * @return 对象实例
     */
    public static DbWhere from(Map<String, Object> map) {
        return from(map, DbWhere.class);
    }

    /**
     * 从map中获取参数构建对象
     * 
     * @param map Map参数
     * @param clazz 对象类型
     * @return 对象实例
     */
    protected static <T extends DbItems> T from(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            throw new NullPointerException("map is null");
        }
        if (clazz == null) {
            throw new NullPointerException("clazz is null");
        }

        T items;
        try {
            items = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to new instance for " + clazz.getName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to new instance for " + clazz.getName(), e);
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (VerifyTools.isAnyBlank(key, value)) {
                continue;
            }
            int index = key.lastIndexOf('$');
            if (index < 0) {
                items.put(key, value);
            } else {
                String field = key.substring(0, index);
                String operate = key.substring(index + 1);
                items.put(operate, field, value);
            }
        }
        return items;
    }

    /**
     * 空的查询条件
     *
     * @author zhaohuihua
     * @version 190310
     */
    public static class EmptyDbWhere extends DbWhere {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;

        private EmptyDbWhere() {
        }

        protected void put(String fieldName, Object fieldValue) {
            throw new UnsupportedOperationException("EmptyDbWhere");
        }

        protected void put(String operateType, String fieldName, Object fieldValue) {
            throw new UnsupportedOperationException("EmptyDbWhere");
        }

        protected void put(DbFields fields) {
            throw new UnsupportedOperationException("EmptyDbWhere");
        }

        protected void put(DbCondition condition) {
            throw new UnsupportedOperationException("EmptyDbWhere");
        }

    }
}
