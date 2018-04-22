package com.gitee.zhaohuihua.core.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量操作结果
 *
 * @author zhaohuihua
 * @version 170527
 */
public class BatchResult implements IBatchResult, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 记录总数 **/
    private Integer total = 0;

    /** 失败列表 **/
    private List<Failed> failed = new ArrayList<>();

    /** 整条记录失败 **/
    public void addFailed(int row, IResultMessage result) {
        failed.add(new FailedItem(row, result));
    }

    /** 具体某一列失败 **/
    public void addFailed(int row, String field, IResultMessage result) {
        failed.add(new FailedItem(row, field, result));
    }

    /** 失败列表 **/
    @Override
    public List<Failed> getFailed() {
        return failed;
    }

    /** 增加记录总数 **/
    public void addTotal() {
        addTotal(1);
    }

    /** 增加记录总数 **/
    public void addTotal(int number) {
        total += number;
    }

    /** 设置记录总数 **/
    public void setTotal(int total) {
        this.total = total;
    }

    /** 获取记录总数 **/
    @Override
    public Integer getTotal() {
        return total;
    }

    /**
     * 失败信息
     *
     * @author zhaohuihua
     * @version 160302
     */
    public static class FailedItem implements Failed, Serializable {

        /** 版本序列号 **/
        private static final long serialVersionUID = 1L;

        /** 序号(从1开始) **/
        private Integer index;

        /** 字段名称 **/
        private String field;

        /** 错误代码 **/
        private String code;

        /** 错误描述 **/
        private String message;

        public FailedItem() {
        }

        /**
         * 构造函数
         * 
         * @param index 序号(从1开始)
         * @param cause 失败原因
         */
        public FailedItem(Integer index, IResultMessage cause) {
            this(index, null, cause);
        }

        /**
         * 构造函数
         * 
         * @param index 序号(从1开始)
         * @param field 字段名
         * @param cause 失败原因
         */
        public FailedItem(Integer index, String field, IResultMessage cause) {
            this.index = index;
            this.field = field;
            this.code = cause.getCode();
            this.message = cause.getMessage();
        }

        /** 获取序号(从1开始) **/
        @Override
        public Integer getIndex() {
            return index;
        }

        /** 获取序号(从1开始) **/
        public void setIndex(Integer index) {
            this.index = index;
        }

        /** 获取字段名称 **/
        public String getField() {
            return field;
        }

        /** 设置字段名称 **/
        public void setField(String field) {
            this.field = field;
        }

        /** 错误代码 **/
        @Override
        public String getCode() {
            return code;
        }

        /** 错误代码 **/
        public void setCode(String code) {
            this.code = code;
        }

        /** 错误描述 **/
        @Override
        public String getMessage() {
            return message;
        }

        /** 错误描述 **/
        public void setMessage(String message) {
            this.message = message;
        }

    }
}
