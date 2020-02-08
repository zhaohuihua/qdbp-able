package com.gitee.qdbp.able.jdbc.condition;

import java.util.Map;
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
    protected void put(DbConditions fields) {
        throw new UnsupportedOperationException("DbUpdate can't supported put(DbConditions)");
    }

    /**
     * 从map中获取参数构建对象
     * 
     * @param map Map参数
     * @return 对象实例
     */
    public static DbUpdate parse(Map<String, Object> map) {
        return parse(map, DbUpdate.class);
    }

    /**
     * 从map中获取参数构建对象
     * 
     * @param map Map参数
     * @param clazz 对象类型
     * @return 对象实例
     */
    public static <T extends DbUpdate> T parse(Map<String, Object> map, Class<T> clazz) {
        VerifyTools.requireNonNull(clazz, "class");

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
                int index = key.lastIndexOf('$');
                if (index < 0) {
                    if (value == null || "".equals(value)) {
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
