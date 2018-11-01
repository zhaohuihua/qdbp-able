package com.gitee.qdbp.tools.excel.json;

import com.gitee.qdbp.tools.excel.XMetadata;

/**
 * 一对多合并, 将子数据列表以fieldName指定的字段名合并至主数据
 *
 * @author zhaohuihua
 * @version 181101
 */
public class MergeToList extends MergeMetadata {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 自身名称 **/
    private String selfName;

    /** 自身名称 **/
    public String getSelfName() {
        return selfName;
    }

    /** 自身名称 **/
    public void setSelfName(String selfName) {
        this.selfName = selfName;
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
        }
        return instance;
    }

}
