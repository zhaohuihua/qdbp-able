package com.gitee.qdbp.tools.excel.json;

import com.gitee.qdbp.tools.excel.XMetadata;

/**
 * 合并参数
 *
 * @author zhaohuihua
 * @version 181101
 */
public class MergeParams extends XMetadata {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 合并类型枚举 **/
    public static enum Type {
        json, list, field
    }

    /** 合并类型 **/
    private Type type;

    /** Excel文件路径 **/
    private String fileName;
    /** 字段名称 **/
    private String fieldName;
    /** KEY所在的列号 **/
    private Integer keyColumn;
    /** ID所在的列号 **/
    private Integer idColumn;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /** Excel文件路径 **/
    public String getFileName() {
        return fileName;
    }

    /** Excel文件路径 **/
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

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

    /** ID所在的列号 **/
    public Integer getIdColumn() {
        return idColumn;
    }

    /** ID所在的列号 **/
    public void setIdColumn(Integer idColumn) {
        this.idColumn = idColumn;
    }

    /** 转换为MergeMetadata对象 **/
    public MergeMetadata toMergeMetadata() {
        if (this.type == null) {
            return null;
        }
        switch (this.type) {
        case json: {
            MergeToJson metadata = this.to(MergeToJson.class);
            metadata.setFileName(this.getFileName()); // Excel文件路径
            metadata.setIdColumn(this.getIdColumn()); // ID所在的列号
            metadata.setFieldName(this.getFieldName()); // 字段名称
            metadata.setKeyColumn(this.getKeyColumn()); // KEY所在的列号
            return metadata;
        }
        case list: {
            MergeToList metadata = this.to(MergeToList.class);
            metadata.setFileName(this.getFileName()); // Excel文件路径
            metadata.setIdColumn(this.getIdColumn()); // ID所在的列号
            metadata.setFieldName(this.getFieldName()); // 字段名称
            return metadata;
        }
        case field: {
            MergeToField metadata = this.to(MergeToField.class);
            metadata.setFileName(this.getFileName()); // Excel文件路径
            metadata.setIdColumn(this.getIdColumn()); // ID所在的列号
            return metadata;
        }
        default:
            return null;
        }
    }
}
