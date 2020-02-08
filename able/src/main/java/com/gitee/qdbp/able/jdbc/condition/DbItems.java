package com.gitee.qdbp.able.jdbc.condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.gitee.qdbp.able.jdbc.base.DbCondition;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 数据库操作容器, DbWhere/DbUpdate的父类
 *
 * @author zhaohuihua
 * @version 181221
 */
abstract class DbItems implements DbConditions, Serializable {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    private List<DbCondition> items = new ArrayList<>();

    /** 默认构造函数 **/
    protected DbItems() {
    }

    /** 带条件的构造函数 **/
    protected <T extends DbCondition> DbItems(List<T> conditions) {
        VerifyTools.requireNonNull(conditions, "conditions");
        this.items.clear();
        this.items.addAll(conditions);
    }

    /**
     * 增加条件
     * 
     * @param fieldName 字段名
     * @param fieldValue 字段值
     */
    protected void put(String fieldName, Object fieldValue) {
        this.items.add(new DbField(fieldName, fieldValue));
    }

    /**
     * 增加条件
     * 
     * @param operateType 操作符
     * @param fieldName 字段名
     * @param fieldValue 字段值
     */
    protected void put(String operateType, String fieldName, Object fieldValue) {
        this.items.add(new DbField(operateType, fieldName, fieldValue));
    }

    /**
     * 增加条件
     * 
     * @param condition 条件
     */
    protected void put(DbField condition) {
        VerifyTools.requireNonNull(condition, "condition");
        this.items.add(condition);
    }

    /**
     * 增加条件
     * 
     * @param condition 容器类型的条件, 如SubWhere
     */
    protected void put(DbConditions condition) {
        VerifyTools.requireNonNull(condition, "condition");
        this.items.add(condition);
    }

    /**
     * 增加条件
     * 
     * @param condition 自定义条件
     */
    protected void put(DbCondition condition) {
        VerifyTools.requireNonNull(condition, "condition");
        this.items.add(condition);
    }

    /** 遍历条件 **/
    @Override
    public Iterator<DbCondition> iterator() {
        return this.items.iterator();
    }

    /** 是否为空 **/
    public boolean isEmpty() {
        if (this.items.isEmpty()) {
            return true;
        }
        Iterator<DbCondition> iterator = this.items.iterator();
        while (iterator.hasNext()) {
            DbCondition item = iterator.next();
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** 清空内容 **/
    public void clear() {
        this.items.clear();
    }

    /**
     * 根据字段名称替换条件
     * 
     * @param field 字段
     */
    protected void replace(DbField field) {
        VerifyTools.requireNotBlank(field, "field");
        VerifyTools.requireNotBlank(field.getFieldName(), "fieldName");

        String fieldName = field.getFieldName();
        Iterator<DbCondition> iterator = this.iterator();
        while (iterator.hasNext()) {
            DbCondition item = iterator.next();
            if (item instanceof DbField) {
                if (((DbField) item).matchesWithField(fieldName)) {
                    DbField target = (DbField) item;
                    target.setOperateType(field.getOperateType());
                    target.setFieldValue(field.getFieldValue());
                }
            } else if (item instanceof DbItems) {
                ((DbItems) item).replace(field);
            } else { // DbCondition/DbConditions, 暂不支持替换
            }
        }
    }

    /**
     * 根据字段名称删除
     * 
     * @param fieldName 字段名称
     */
    public List<DbCondition> remove(String fieldName) {
        VerifyTools.requireNotBlank(fieldName, "fieldName");
        Iterator<DbCondition> iterator = this.items.iterator();
        List<DbCondition> removed = new ArrayList<>();
        while (iterator.hasNext()) {
            DbCondition item = iterator.next();
            if (item instanceof DbField) {
                if (((DbField) item).matchesWithField(fieldName)) {
                    iterator.remove();
                    removed.add(item);
                }
            } else if (item instanceof DbConditions) {
                List<DbCondition> subRemoved = ((DbConditions) item).remove(fieldName);
                removed.addAll(subRemoved);
            } else {
                if (fieldName.contains(".")) {
                    if (fieldName.equals(item.getClass().getName())) {
                        iterator.remove();
                        removed.add(item);
                    }
                } else {
                    if (fieldName.equals(item.getClass().getSimpleName())) {
                        iterator.remove();
                        removed.add(item);
                    }
                }
            }
        }
        return removed;
    }

    /** 是否存在指定的字段 **/
    public boolean contains(String fieldName) {
        VerifyTools.requireNotBlank(fieldName, "fieldName");
        Iterator<DbCondition> iterator = this.items.iterator();
        while (iterator.hasNext()) {
            DbCondition item = iterator.next();
            if (item instanceof DbField) {
                if (((DbField) item).matchesWithField(fieldName)) {
                    return true;
                }
            } else if (item instanceof DbConditions) {
                if (((DbConditions) item).contains(fieldName)) {
                    return true;
                }
            } else {
                if (fieldName.contains(".")) {
                    if (fieldName.equals(item.getClass().getName())) {
                        return true;
                    }
                } else {
                    if (fieldName.equals(item.getClass().getSimpleName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
