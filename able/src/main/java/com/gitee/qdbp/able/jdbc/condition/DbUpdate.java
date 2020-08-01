package com.gitee.qdbp.able.jdbc.condition;

import java.util.Iterator;
import java.util.List;
import com.gitee.qdbp.able.beans.Copyable;
import com.gitee.qdbp.able.jdbc.base.DbCondition;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 数据库更新条件容器<br>
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
public class DbUpdate extends DbItems implements Copyable {

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
     * 克隆为新对象(如果子类有新增字段或没有默认构造函数就应该覆盖该方法)
     * 
     * @return 新对象
     * @since 5.0
     */
    @Override
    public DbUpdate copy() {
        DbUpdate copies = newCopies();
        this.copyTo(copies);
        return copies;
    }

    protected void copyTo(DbUpdate copies) {
        Iterator<DbCondition> iterator = this.iterator();
        while (iterator.hasNext()) {
            DbCondition item = iterator.next();
            if (item instanceof DbField) {
                copies.put(((DbField) item).copy());
            } else if (item instanceof DbUpdate) {
                copies.put(((DbUpdate) item).copy());
            } else if (item instanceof Copyable) {
                DbCondition newer = (DbCondition) ((Copyable) item).copy();
                copies.put(newer);
            } else { // DbCondition/DbConditions
                copies.put(item); // 无法克隆为副本
            }
        }
    }

    /**
     * 创建副本对象(如果子类没有默认构造函数就应该覆盖该方法)
     * 
     * @return 副本对象
     * @since 5.0
     */
    protected DbUpdate newCopies() {
        if (this.getClass() == DbUpdate.class) { // 当前类
            return new DbUpdate();
        } else { // 子类
            try {
                return this.getClass().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("CloneNotSupported, FailedToInvokeDefaultConstructor.");
            }
        }
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
