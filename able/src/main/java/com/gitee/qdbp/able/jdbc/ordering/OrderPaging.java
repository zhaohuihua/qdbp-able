package com.gitee.qdbp.able.jdbc.ordering;

import java.util.List;
import com.gitee.qdbp.able.jdbc.paging.Paging;

/**
 * 排序和分页查询条件<br>
 * <br>
 * // 只排序不分页<br>
 * OrderPaging odpg = OrderPaging.of("deptName asc, createTime desc");<br>
 * // 只分页不排序<br>
 * OrderPaging odpg = OrderPaging.of(1, 20, Orderings.NONE);<br>
 * // 分页+排序<br>
 * OrderPaging odpg = OrderPaging.of(1, 20, "deptName asc, createTime desc");<br>
 *
 * @author zhaohuihua
 * @version 160728
 */
public class OrderPaging extends Paging {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 不分页也不排序的查询条件 **/
    public static final OrderPaging NONE = new ReadOnlyOrderPaging(Paging.NONE);

    /** 只统计结果的查询条件 **/
    public static final OrderPaging COUNT = new ReadOnlyOrderPaging(Paging.COUNT);

    /** 排序字段和排序类型 **/
    private Orderings orderings;

    /** 获取排序字段列表 **/
    public Orderings getOrderings() {
        return orderings;
    }

    /** 设置排序字段列表 **/
    public void setOrderings(Orderings orderings) {
        this.orderings = orderings;
    }

    /** 以文本形式设置排序字段 **/
    public void setOrdering(String text) {
        this.orderings = Orderings.of(text);
    }

    /**
     * 构造一个只分页不排序的查询条件
     * 
     * @param paging 分页条件
     * @return 查询条件
     */
    public static OrderPaging of(Paging paging) {
        return of(paging, (Orderings) null);
    }

    /**
     * 构造一个只排序不分页的查询条件
     * 
     * @param orderings 排序规则
     * @return 查询条件
     */
    public static OrderPaging of(String orderings) {
        return of(Paging.NONE, Orderings.of(orderings));
    }

    /**
     * 构造一个分页+排序的查询条件
     * 
     * @param paging 分页条件
     * @param orderings 排序规则
     * @return 查询条件
     */
    public static OrderPaging of(Paging paging, String orderings) {
        return of(paging, Orderings.of(orderings));
    }

    /**
     * 构造一个只分页不排序的查询条件
     * 
     * @param pageIndex 当前页数
     * @param pageSize 每页行数
     * @return 查询条件
     */
    public static OrderPaging of(int pageIndex, int pageSize) {
        return of(new Paging(pageIndex, pageSize), Orderings.NONE);
    }

    /**
     * 构造一个分页+排序的查询条件
     * 
     * @param pageIndex 当前页数
     * @param pageSize 每页行数
     * @param orderings 排序规则
     * @return 查询条件
     */
    public static OrderPaging of(int pageIndex, int pageSize, String orderings) {
        return of(new Paging(pageIndex, pageSize), Orderings.of(orderings));
    }

    /**
     * 构造一个只排序不分页的查询条件
     * 
     * @param orderings 排序规则
     * @return 查询条件
     */
    public static OrderPaging of(Orderings orderings) {
        return of(Paging.NONE, orderings);
    }

    /**
     * 构造一个分页+排序的查询条件
     * 
     * @param paging 分页条件
     * @param orderings 排序规则
     * @return 查询条件
     */
    public static OrderPaging of(Paging paging, Orderings orderings) {
        OrderPaging condition = new OrderPaging();
        condition.setPage(paging.getPage());
        condition.setRows(paging.getRows());
        condition.setOffset(paging.getOffset());
        condition.setPaging(paging.isPaging());
        condition.setNeedCount(paging.isNeedCount());
        condition.setOrderings(orderings);
        return condition;
    }

    protected static class ReadOnlyOrderPaging extends OrderPaging {

        /** 版本序列号 **/
        private static final long serialVersionUID = 1L;

        protected ReadOnlyOrderPaging(Paging paging) {
            this(paging, null);
        }

        protected ReadOnlyOrderPaging(Paging paging, Orderings orderings) {
            super.setPage(paging.getPage());
            super.setRows(paging.getRows());
            super.setOffset(paging.getOffset());
            super.setPaging(paging.isPaging());
            super.setNeedCount(paging.isNeedCount());
            super.orderings = orderings;
        }

        public void setSkip(Integer skip) {
            throw new UnsupportedOperationException("read only");
        }

        public void setRows(Integer rows) {
            throw new UnsupportedOperationException("read only");
        }

        public void setPage(Integer page) {
            throw new UnsupportedOperationException("read only");
        }

        public void setNeedCount(boolean needCount) {
            throw new UnsupportedOperationException("read only");
        }

        public void setPaging(boolean paging) {
            throw new UnsupportedOperationException("read only");
        }

        public void setOrderings(List<Ordering> orderings) {
            throw new UnsupportedOperationException("read only");
        }

        public void setOrdering(String text) {
            throw new UnsupportedOperationException("read only");
        }
    }
}
