package com.gitee.zhaohuihua.core.model.paging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 分页结果集
 *
 * @author zhaohuihua
 * @version 170113
 */
public class PageList<T> implements Iterable<T>, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 列表 **/
    private List<T> list;

    /** 总记录数 **/
    private Integer total;

    /** 构造函数 **/
    public PageList() {
        this.list = new ArrayList<>();
    }

    /** 构造函数 **/
    public PageList(Collection<T> list, Integer total) {
        setList(list);
        setTotal(total);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    public boolean add(T item) {
        return this.list.add(item);
    }

    public boolean addAll(Collection<? extends T> items) {
        return this.list.addAll(items);
    }

    public boolean remove(T item) {
        return this.list.remove(item);
    }

    public boolean removeAll(Collection<?> items) {
        return this.list.removeAll(items);
    }

    public boolean contains(Object item) {
        return this.list.contains(item);
    }

    public boolean retainAll(Collection<?> items) {
        return this.list.retainAll(items);
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public int size() {
        return this.list.size();
    }

    public void clear() {
        this.list.clear();
    }

    public T get(int index) {
        if (this.list instanceof List) {
            return ((List<T>) this.list).get(index);
        } else {
            int i = 0;
            for (T item : this.list) {
                if (i++ == index) {
                    return item;
                }
            }
            return null;
        }
    }

    public T set(int index, T element) {
        return this.list.set(index, element);
    }

    /** 获取列表 **/
    public Collection<T> getList() {
        return list;
    }

    /** 设置列表 **/
    public <C extends T> void setList(Collection<C> list) {
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = new ArrayList<>(list);
        }
    }

    /** 获取总记录数 **/
    public Integer getTotal() {
        return total != null ? total : list == null ? 0 : list.size();
    }

    /** 设置总记录数 **/
    public void setTotal(Integer total) {
        this.total = total == null ? null : Math.max(0, total);
    }

    /** PageList转List **/
    public List<T> toList() {
        PartList<T> pl = new PartList<T>();
        if (!this.isEmpty()) {
            pl.addAll(this.getList());
        }
        pl.setTotal(this.getTotal());
        return pl;
    }

    /** Collection转PageList **/
    public static <T, C extends T> PageList<T> of(Collection<C> list) {
        if (list == null) {
            return null;
        } else {
            PageList<T> sub = new PageList<T>();
            sub.setList(list);
            sub.setTotal(list.size());
            if (list instanceof PartList) {
                PartList<?> pl = (PartList<?>) list;
                sub.setTotal(pl.getTotal());
            }
            return sub;
        }
    }
}
