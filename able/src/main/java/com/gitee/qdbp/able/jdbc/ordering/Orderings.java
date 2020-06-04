package com.gitee.qdbp.able.jdbc.ordering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
    public static final Orderings NONE = new EmptyOrderings();

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

    private static class EmptyOrderings extends Orderings {

        private static final long serialVersionUID = 8842843931221139166L;

        @Override
        public Iterator<Ordering> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public ListIterator<Ordering> listIterator() {
            return Collections.emptyListIterator();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.isEmpty();
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }
        @Override
        public <T> T[] toArray(T[] a) {
            if (a.length > 0) a[0] = null;
            return a;
        }
        @Override
        public Ordering get(int index) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof List) && ((List<?>) o).isEmpty();
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<Ordering> subList(int fromIndex, int toIndex) {
            return (List<Ordering>) Collections.EMPTY_LIST;
        }

        @Override
        public void trimToSize() {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public void ensureCapacity(int minCapacity) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public Ordering set(int index, Ordering e) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public boolean add(Ordering e) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public void add(int index, Ordering e) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public Ordering remove(int index) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public boolean addAll(Collection<? extends Ordering> c) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public boolean addAll(int index, Collection<? extends Ordering> c) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("read only");
        }

        // Preserves singleton property
        private Object readResolve() {
            return Collections.EMPTY_LIST;
        }
    }
}
