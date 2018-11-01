package com.gitee.qdbp.tools.excel.json;

import com.gitee.qdbp.tools.excel.XMetadata;

/**
 * 如果指定了selfWith, 一对多合并, 将子数据以selfWith指定列的字段内容作为字段名合并至主数据<br>
 * 如果未指定selfWith而是指定了selfName, 一对一合并, 则将子数据列表以selfName指定的字段名合并至主数据<br>
 *
 * @author zhaohuihua
 * @version 181101
 */
public class MergeToJson extends MergeMetadata {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 自身名称 **/
    private String selfName;
    /** 自身名称所在的字段名 **/
    private String selfWith;

    /** 自身名称 **/
    public String getSelfName() {
        return selfName;
    }

    /** 自身名称 **/
    public void setSelfName(String selfName) {
        this.selfName = selfName;
    }

    /** 自身名称所在的字段名 **/
    public String getSelfWith() {
        return selfWith;
    }

    /** 自身名称所在的字段名 **/
    public void setSelfWith(String selfWith) {
        this.selfWith = selfWith;
    }

    /**
     * 将当前对象转换为子类对象
     *
     * @param clazz 目标类型
     * @return 目标对象
     */
    public <T extends XMetadata> T to(Class<T> clazz) {
        T instance = super.to(clazz);

        if (instance instanceof MergeToJson) {
            MergeToJson real = (MergeToJson) instance;
            real.setSelfName(this.getSelfName()); // 自身名称
            real.setSelfWith(this.getSelfWith()); // 自身名称所在的字段名
        }
        return instance;
    }
}
