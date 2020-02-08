package com.gitee.qdbp.able.jdbc.condition;

import java.util.Iterator;
import com.gitee.qdbp.able.jdbc.base.DbCondition;

/**
 * 条件容器接口
 *
 * @author zhaohuihua
 * @version 190620
 */
interface DbConditions extends DbCondition, Iterable<DbCondition> {

    /** 遍历内容 **/
    Iterator<DbCondition> iterator();

    /** 是否存在指定的字段 **/
    boolean contains(String fieldName);

    /** 移除内容 **/
    void remove(String fieldName);

    /** 是否为空 **/
    boolean isEmpty();

    /** 清空内容 **/
    void clear();
}
