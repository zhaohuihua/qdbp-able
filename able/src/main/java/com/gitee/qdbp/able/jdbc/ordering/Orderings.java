package com.gitee.qdbp.able.jdbc.ordering;

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
public class Orderings extends ArrayList<Ordering> implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 多个条件的分隔符正则表达式 **/
    private static final Pattern GROUP = Pattern.compile(",");

    /** 不排序 **/
    public static final List<Ordering> NONE = Collections.emptyList();

    /** 默认构造函数 **/
    public Orderings() {
    }

    /** 构造函数, orderings=排序字段 **/
    public Orderings(String orderings) {
        this.addAll(of(orderings));
    }

    /** 增加升序排序字段 **/
    public Orderings asc(String orderBy) {
        Ordering ordering = new Ordering();
        ordering.setOrderBy(orderBy);
        ordering.setOrderType(OrderType.ASC);
        this.add(ordering);
        return this;
    }

    /** 增加降序排序字段 **/
    public Orderings desc(String orderBy) {
        Ordering ordering = new Ordering();
        ordering.setOrderBy(orderBy);
        ordering.setOrderType(OrderType.DESC);
        this.add(ordering);
        return this;
    }

    /** 解析排序字段 **/
    public static Orderings of(String text) {
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
        return container;
    }

}
