package com.gitee.qdbp.able.jdbc.condition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.gitee.qdbp.able.jdbc.base.DbCondition;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 数据库更新操作容器<br>
 * 支持Set,Add,ToNull操作<br>
 * <pre>
    DbUpdate ud = new DbUpdate();
    // SQL> SET USER_NAME = :$1
    ud.set("userName", "zhaohuihua"); // 用户名修改为指定值
    // SQL> SET MEMBER_SCORE = MEMBER_SCORE + :$1
    ud.add("memberScore", +100); // 会员积分增加100
    // SQL> SET MEMBER_SCORE = MEMBER_SCORE - :$1
    ud.add("memberScore", -100); // 会员积分减少100
    // SQL> SET USER_STATE = NULL
    ud.toNull("userState"); // 用户状态修改为空
 * </pre>
 *
 * @author zhaohuihua
 * @version 181221
 */
public class DbUpdate extends DbItems {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** SET :fieldName = :fieldValue **/
    public DbUpdate set(String fieldName, Object fieldValue) {
        this.put(fieldName, fieldValue);
        return this;
    }

    /** SET :fieldName = :fieldName + :fieldValue **/
    public DbUpdate add(String fieldName, Object fieldValue) {
        this.put("Add", fieldName, fieldValue);
        return this;
    }

    /** SET :fieldName = NULL **/
    public DbUpdate toNull(String fieldName) {
        this.put("ToNull", fieldName, null);
        return this;
    }

    @Override
    protected void put(DbFields fields) {
        throw new UnsupportedOperationException("DbUpdate can't supported put(DbFields)");
    }

    /**
     * 查询指定字段所有的条件
     * 
     * @param fieldName 指定字段
     * @return 条件列表
     */
    public List<DbField> fields(String fieldName) {
        List<DbCondition> items = this.items();
        List<DbField> result = new ArrayList<>();
        if (items.isEmpty()) {
            return result;
        }

        Iterator<DbCondition> itr = items.iterator();
        while (itr.hasNext()) {
            DbCondition item = itr.next();
            if (item instanceof DbField) {
                if (((DbField) item).getFieldName().equals(fieldName)) {
                    result.add((DbField) item);
                }
            }
        }
        return result;
    }

    /**
     * 从map中获取参数构建对象
     * 
     * @param map Map参数
     * @param emptiable 是否允许条件为空
     * @return 对象实例
     */
    public static DbUpdate from(Map<String, Object> map, boolean emptiable) {
        if (map == null || map.isEmpty()) {
            if (emptiable) {
                return new DbUpdate();
            } else {
                throw new IllegalArgumentException("map must not be " + (map == null ? "null" : "empty"));
            }
        }
        DbUpdate ud = from(map, DbUpdate.class);
        if (!emptiable && ud.isEmpty()) {
            throw new IllegalArgumentException("update object must not be empty.");
        }
        return ud;
    }

    /**
     * 从map中获取参数构建对象
     * 
     * @param map Map参数
     * @param clazz 对象类型
     * @return 对象实例
     */
    protected static <T extends DbItems> T from(Map<String, Object> map, Class<T> clazz) {
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

        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (VerifyTools.isBlank(key)) {
                    continue;
                }
                if (value == null) {
                    continue;
                }
                int index = key.lastIndexOf('$');
                if (index < 0) {
                    if (value.equals("")) {
                        items.put("ToNull", key, value);
                    } else {
                        items.put(key, value);
                    }
                } else {
                    String field = key.substring(0, index);
                    String operate = key.substring(index + 1);
                    items.put(operate, field, value);
                }
            }
        }
        return items;
    }
}
