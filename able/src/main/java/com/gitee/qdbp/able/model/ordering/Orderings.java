package com.gitee.qdbp.able.model.ordering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 排序工具类<br>
 * Orderings orderings = new Orderings("name asc, createTime desc");<br>
 * List&lt;Ordering&gt; orderings = Orderings.of("name asc, createTime desc");<br>
 * List&lt;Ordering&gt; orderings = new Orderings().asc("name").desc("createTime").list();<br>
 *
 * @author zhaohuihua
 * @version 160411
 */
public class Orderings implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 多个条件的分隔符正则表达式 **/
    private static final Pattern GROUP = Pattern.compile(",");

    /** 不排序 **/
    public static final List<Ordering> NONE = Collections.emptyList();

    private List<Ordering> orderings = new ArrayList<>();

    /** 默认构造函数 **/
    public Orderings() {
    }

    /** 构造函数, orderings=排序字段 **/
    public Orderings(String orderings) {
        this.orderings = of(orderings);
    }

    /** 增加升序排序字段 **/
    public Orderings asc(String orderBy) {
        Ordering ordering = new Ordering();
        ordering.setOrderBy(orderBy);
        ordering.setOrderType(OrderType.ASC);
        orderings.add(ordering);
        return this;
    }

    /** 增加降序排序字段 **/
    public Orderings desc(String orderBy) {
        Ordering ordering = new Ordering();
        ordering.setOrderBy(orderBy);
        ordering.setOrderType(OrderType.DESC);
        orderings.add(ordering);
        return this;
    }

    /** 获取排序字段列表 **/
    public List<Ordering> list() {
        return orderings.isEmpty() ? null : orderings;
    }

    /** 获取排序字段列表 **/
    public List<Ordering> getOrderings() {
        return orderings;
    }

    /** 设置排序字段列表 **/
    public void setOrderings(List<Ordering> orderings) {
        this.orderings = orderings;
    }

    /** 以文本形式设置排序字段 **/
    public void setOrdering(String text) {
        this.orderings = Orderings.of(text);
    }

    /** 解析排序字段 **/
    public static List<Ordering> of(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        Orderings container = new Orderings();
        String[] array = GROUP.split(text);
        for (String item : array) {
            if (item.trim().length() == 0) {
                continue;
            }
            item = item.trim();
            int spaceIndex = item.lastIndexOf(' ');
            if (spaceIndex < 0) {
                container.asc(item);
            } else {
                String by = item.substring(0, spaceIndex).trim();
                String type = item.substring(spaceIndex + 1);
                if ("asc".equalsIgnoreCase(type)) {
                    container.asc(by);
                } else if ("desc".equalsIgnoreCase(type)) {
                    container.desc(by);
                } else {
                    throw new IllegalArgumentException("OrderTypeError: " + type);
                }
            }
        }
        return container.list();
    }

}
