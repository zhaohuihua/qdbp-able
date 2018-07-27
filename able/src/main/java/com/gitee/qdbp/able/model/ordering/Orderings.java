package com.gitee.qdbp.able.model.ordering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 排序工具类<br>
 * List&lt;Ordering&gt; orderings = Orderings.of("name asc, createTime desc");<br>
 * List&lt;Ordering&gt; orderings = new Orderings().asc("name").desc("createTime").list();<br>
 *
 * @author zhaohuihua
 * @version 160411
 */
public class Orderings implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 6926222322201187819L;

    /** 多个条件的分隔符正则表达式 **/
    private static final Pattern GROUP = Pattern.compile(",");
    /** 多个单词的分隔符正则表达式 **/
    private static final Pattern WORDS = Pattern.compile("\\s+");

    private List<Ordering> orderings = new ArrayList<>();

    public Orderings asc(String orderBy) {
        Ordering ordering = new Ordering();
        ordering.setOrderBy(orderBy);
        ordering.setOrderType(OrderType.ASC);
        orderings.add(ordering);
        return this;
    }

    public Orderings desc(String orderBy) {
        Ordering ordering = new Ordering();
        ordering.setOrderBy(orderBy);
        ordering.setOrderType(OrderType.DESC);
        orderings.add(ordering);
        return this;
    }

    public List<Ordering> list() {
        return orderings.isEmpty() ? null : orderings;
    }

    public static List<Ordering> of(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }
        Orderings orderings = new Orderings();
        String[] array = GROUP.split(text);
        for (String item : array) {
            if (item.trim().length() == 0) {
                continue;
            }
            String[] words = WORDS.split(item.trim());
            if (words.length == 1) {
                orderings.asc(words[0]);
            } else if (words.length > 1) {
                String by = words[0];
                String type = words[1];
                switch (type) {
                case "asc":
                case "ASC":
                    orderings.asc(by);
                    break;
                case "desc":
                case "DESC":
                    orderings.desc(by);
                    break;
                default:
                    throw new IllegalArgumentException("OrderTypeError: " + type);
                }
            }
        }
        return orderings.list();
    }

}
