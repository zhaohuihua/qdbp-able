package com.gitee.qdbp.able.jdbc.ordering;

import java.io.Serializable;


/**
 * 排序信息
 *
 * @author zhaohuihua
 * @version 151223
 */
public class Ordering implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 2099951019209473307L;

    /** 排序字段 **/
    private String orderBy;

    /** 排序类型 **/
    private OrderType orderType;

    /** 获取排序字段 **/
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /** 设置排序字段 **/
    public String getOrderBy() {
        return orderBy;
    }

    /** 获取排序类型 **/
    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    /** 设置排序字段 **/
    public OrderType getOrderType() {
        return orderType != null ? orderType : OrderType.ASC;
    }

    @Override
    public String toString() {
        return orderBy + " " + getOrderType();
    }
}
