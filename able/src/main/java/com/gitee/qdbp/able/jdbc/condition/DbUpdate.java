package com.gitee.qdbp.able.jdbc.condition;

import java.util.List;
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
     * 通过字段对象列表构造DbUpdate对象
     * 
     * @param <T> DbUpdate泛型
     * @param fields 字段对象
     * @param instanceType 实例类型
     * @return DbUpdate对象
     */
    public static <T extends DbUpdate> T ofFields(List<DbField> fields, Class<T> instanceType) {
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

}
