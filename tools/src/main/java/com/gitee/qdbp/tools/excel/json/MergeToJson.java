package com.gitee.qdbp.tools.excel.json;

import com.gitee.qdbp.tools.excel.XMetadata;

/**
 * 如果指定了keyColumn, 一对多合并, 将子数据以keyColumn指定列的字段内容作为字段名合并至主数据<br>
 * 如果未指定keyColumn而是指定了fieldName, 一对一合并, 则将子数据列表以fieldName指定的字段名合并至主数据<br>
 *
 * @author zhaohuihua
 * @version 181101
 */
public class MergeToJson extends MergeBase {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 字段名称 **/
    private String fieldName;
    /** KEY所在的列号 **/
    private Integer keyColumn;

    /** 字段名称 **/
    public String getFieldName() {
        return fieldName;
    }

    /** 字段名称 **/
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /** KEY所在的列号 **/
    public Integer getKeyColumn() {
        return keyColumn;
    }

    /** KEY所在的列号 **/
    public void setKeyColumn(Integer keyColumn) {
        this.keyColumn = keyColumn;
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
            real.setFieldName(this.getFieldName()); // 字段名称
            real.setKeyColumn(this.getKeyColumn()); // KEY所在的列号
        }
        return instance;
    }
}
