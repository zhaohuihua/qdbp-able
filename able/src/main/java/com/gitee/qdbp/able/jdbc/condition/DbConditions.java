package com.gitee.qdbp.able.jdbc.condition;

import java.util.Iterator;
import java.util.List;
import com.gitee.qdbp.able.jdbc.base.DbCondition;

/**
 * 条件容器接口
 *
 * @author zhaohuihua
 * @version 190620
 */
interface DbConditions extends DbCondition, Iterable<DbCondition> {

    /** 遍历条件 **/
    Iterator<DbCondition> iterator();

    /** 是否存在指定的条件 **/
    boolean contains(String fieldName);

    /** 查找条件 **/
    <T extends DbCondition> List<T> find(String fieldName);

    /** 移除条件 **/
    <T extends DbCondition> List<T> remove(String fieldName);

    /** 是否为空 **/
    boolean isEmpty();

    /** 清空条件 **/
    void clear();
}
