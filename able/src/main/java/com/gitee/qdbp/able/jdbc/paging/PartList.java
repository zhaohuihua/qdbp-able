package com.gitee.qdbp.able.jdbc.paging;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 部分记录结果集
 *
 * @author zhaohuihua
 * @version 151013
 */
public class PartList<E> extends ArrayList<E> {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 总记录数 **/
    private Integer total;

    public PartList() {
    }

    public PartList(Collection<? extends E> list, Integer total) {
        super();
        this.total = total;
        if (list != null) {
            this.addAll(list);
        }
    }

    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    public Integer getTotal() {
        return total != null ? total : size();
    }

    /**
     * 设置总记录数
     *
     * @param total 总记录数
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

}
