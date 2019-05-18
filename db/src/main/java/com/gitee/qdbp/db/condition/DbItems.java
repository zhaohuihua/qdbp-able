package com.gitee.qdbp.db.condition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.gitee.qdbp.able.utils.VerifyTools;

/**
 * 数据库操作容器
 *
 * @author zhaohuihua
 * @version 181221
 */
abstract class DbItems implements DbFields, Serializable {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    private List<DbCondition> items = new ArrayList<>();

    protected void put(String fieldName, Object fieldValue) {
        this.items.add(new DbField(fieldName, fieldValue));
    }

    protected void put(String operateType, String fieldName, Object fieldValue) {
        this.items.add(new DbField(operateType, fieldName, fieldValue));
    }

    protected void put(DbFields fields) {
        this.items.add(fields);
    }

    /**
     * 根据字段名称删除
     * 
     * @param fieldName 字段名称
     */
    public void remove(String fieldName) {
        if (VerifyTools.isBlank(fieldName)) {
            return;
        }
        Iterator<DbCondition> itr = this.items.iterator();
        while (itr.hasNext()) {
            DbCondition item = itr.next();
            if (item instanceof DbField) {
                if (fieldName.equals(((DbField) item).getFieldName())) {
                    itr.remove();
                }
            } else if (item instanceof DbFields) {
                ((DbFields) item).remove(fieldName);
            }
        }
    }

    /** 是否为空 **/
    public boolean isEmpty() {
        if (this.items.isEmpty()) {
            return true;
        }
        Iterator<DbCondition> itr = this.items.iterator();
        while (itr.hasNext()) {
            DbCondition item = itr.next();
            if (item instanceof DbField) {
                return false;
            } else if (item instanceof DbFields) {
                if (!((DbFields) item).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /** 清空内容 **/
    public void clear() {
        this.clear();
    }

    /** 获取内容 **/
    public List<DbCondition> items() {
        return this.items;
    }

    /** 是否存在指定的字段 **/
    public boolean contains(String fieldName) {
        if (VerifyTools.isBlank(fieldName)) {
            throw new IllegalArgumentException("fileName is blank");
        }
        Iterator<DbCondition> itr = this.items.iterator();
        while (itr.hasNext()) {
            DbCondition item = itr.next();
            if (item instanceof DbField) {
                if (fieldName.equals(((DbField) item).getFieldName())) {
                    return true;
                }
            } else if (item instanceof DbFields) {
                if (((DbFields) item).contains(fieldName)) {
                    return true;
                }
            }
        }
        return false;
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
}
