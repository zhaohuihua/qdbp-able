package com.gitee.qdbp.able.jdbc.paging;

import java.io.Serializable;

/**
 * 分页查询参数<br>
 * 第1页: start=0, end=10;<br>
 * 第2页: start=10, end=20.<br>
 * mysql: limit {start}, {rows}<br>
 * oracle: SELECT * FROM ( SELECT ROWNUM R_N, T_T.* FROM ( {sql} ) T_T WHERE ROWNUM <= {end}) WHERE R_N > {start}
 *
 * @author zhaohuihua
 * @version 140516
 */
public class Paging implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;
    /** 跳过行数默认值 **/
    private static final int OFFSET_DEFAULT = 0;
    /** 当前页数默认值 **/
    private static final int PAGE_DEFAULT = 1;
    /** 每页行数默认值 **/
    private static final int ROWS_DEFAULT = 10;
    /** 每页行数最小值(行数为0时只统计总数不查询列表) **/
    private static final int ROWS_MIN = 0;
    /** 不分页 **/
    public static final Paging NONE = new ReadOnlyPaging(PAGE_DEFAULT, ROWS_MIN, OFFSET_DEFAULT, false, false);
    /** 只统计(不查询列表) **/
    public static final Paging COUNT = new ReadOnlyPaging(PAGE_DEFAULT, ROWS_MIN, OFFSET_DEFAULT, true, true);

    /** 跳过多少行 **/
    private Integer offset;
    /** 每页显示行数(行数为0时只统计总数不查询列表) **/
    private Integer rows;
    /** 当前页数 **/
    private Integer page;
    /** 是否需要统计总数 **/
    private boolean needCount = true;
    /** 是否开启分页(false时不分页,查询全部) **/
    private boolean paging = true;

    /** 默认构造函数 **/
    public Paging() {
    }

    /**
     * 构造函数
     * 
     * @param page 当前页数(从1开始)
     * @param rows 每页显示行数
     */
    public Paging(int page, int rows) {
        this(page, rows, OFFSET_DEFAULT, true, true);
    }

    /**
     * 构造函数
     * 
     * @param page 当前页数(从1开始)
     * @param rows 每页显示行数
     * @param needCount 是否统计总数
     */
    public Paging(int page, int rows, boolean needCount) {
        this(page, rows, OFFSET_DEFAULT, true, needCount);
    }

    /**
     * 构造函数
     * 
     * @param page 当前页数(从1开始)
     * @param rows 每页显示行数
     * @param offset 跳过多少行
     * @param paging 是否分页
     * @param needCount 是否统计总数
     */
    protected Paging(Integer page, Integer rows, Integer offset, boolean paging, boolean needCount) {
        this.page = page;
        this.rows = rows;
        this.offset = offset;
        this.paging = paging;
        this.needCount = needCount;
    }

    /**
     * 获取跳过多少行
     *
     * @return 返回跳过多少行
     */
    public Integer getOffset() {
        return format(offset, OFFSET_DEFAULT);
    }

    /**
     * 设置跳过多少行
     *
     * @param offset 要设置的跳过多少行
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * 获取每页显示行数(行数为0时只统计总数不查询列表)
     *
     * @return 返回每页显示行数
     */
    public Integer getRows() {
        return format(rows, ROWS_DEFAULT, ROWS_MIN);
    }

    /**
     * 设置每页显示行数(行数为0时只统计总数不查询列表)
     *
     * @param rows 要设置的每页显示行数
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * 获取当前页数
     *
     * @return 返回当前页数
     */
    public Integer getPage() {
        return format(page, PAGE_DEFAULT);
    }

    /**
     * 设置当前页数
     *
     * @param page 要设置的当前页数
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * 获取开始行数
     *
     * @return 返回开始行数
     */
    public Integer getStart() {
        return getOffset() + (getPage() - 1) * getRows();
    }

    /**
     * 获取结束行数
     *
     * @return 返回结束行数
     */
    public Integer getEnd() {
        return getStart() + getRows();
    }

    /**
     * 判断是否需要统计总数
     *
     * @return needCount 是否统计总数
     */
    public boolean isNeedCount() {
        return needCount;
    }

    /**
     * 设置是否需要统计总数
     *
     * @param needCount 是否统计总数
     */
    public void setNeedCount(boolean needCount) {
        this.needCount = needCount;
    }

    /**
     * 判断是否开启分页(false时不分页,查询全部)
     *
     * @return paging 是否分页
     */
    public boolean isPaging() {
        return paging;
    }

    /**
     * 设置是否开启分页(false时不分页,查询全部)
     *
     * @param needCount 是否分页
     */
    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    /**
     * 获取Int类型的值; 如果目标值为空, 则返回默认值; 如果目标值小于最小值, 则返回最小值
     *
     * @param number 目标值
     * @param def 默认值
     * @param min 最小值
     * @return Int值
     */
    private Integer format(Integer number, int def, int min) {
        return number == null ? def : number < min ? min : number;
    }

    /**
     * 获取Int类型的值, 如果目标值为空或小于最小值, 则返回最小值
     *
     * @param number 目标值
     * @param min 最小值
     * @return Int值
     */
    private Integer format(Integer number, int min) {
        return number == null || number < min ? min : number;
    }

    /**
     * 通过offset+rows参数构造分页对象
     * 
     * @param offset 开始行数(从0开始)
     * @param rows 每页显示行数
     * @return 分页对象
     */
    public static Paging offset(int offset, int rows) {
        return new Paging(1, rows, offset, true, true);
    }

    /**
     * 通过offset+rows参数构造分页对象
     * 
     * @param offset 开始行数(从0开始)
     * @param rows 每页显示行数
     * @param needCount 是否分页
     * @return 分页对象
     */
    public static Paging offset(int offset, int rows, boolean needCount) {
        return new Paging(1, rows, offset, true, needCount);
    }

    protected static class ReadOnlyPaging extends Paging {

        /** 版本序列号 **/
        private static final long serialVersionUID = 1L;

        protected ReadOnlyPaging(Integer page, Integer rows, Integer offset, boolean paging, boolean needCount) {
            super(page, rows, offset, paging, needCount);
        }

        @Override
        public void setOffset(Integer offset) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public void setRows(Integer rows) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public void setPage(Integer page) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public void setNeedCount(boolean needCount) {
            throw new UnsupportedOperationException("read only");
        }

        @Override
        public void setPaging(boolean paging) {
            throw new UnsupportedOperationException("read only");
        }
    }
}
