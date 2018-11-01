package com.gitee.qdbp.tools.excel.json;

import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.tools.excel.XMetadata;

/**
 * 主数据参数
 *
 * @author zhaohuihua
 * @version 181101
 */
public class ToJsonMetadata extends XMetadata {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 字段名称 **/
    private String fieldName;
    /** Excel文件路径 **/
    private String fileName;
    /** ID所在的列号 **/
    private Integer idColumn;
    /** 合并参数 **/
    private List<MergeMetadata> mergers;

    /** 字段名称 **/
    public String getFieldName() {
        return fieldName;
    }

    /** 字段名称 **/
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /** Excel文件路径 **/
    public String getFileName() {
        return fileName;
    }

    /** Excel文件路径 **/
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** ID所在的列号 **/
    public Integer getIdColumn() {
        return idColumn;
    }

    /** ID所在的列号 **/
    public void setIdColumn(Integer idColumn) {
        this.idColumn = idColumn;
    }

    /** 合并参数 **/
    public List<MergeMetadata> getMergers() {
        return mergers;
    }

    /** 合并参数 **/
    public void setMergers(List<MergeMetadata> mergers) {
        this.mergers = mergers;
    }

    /** 增加合并参数 **/
    public void addMergers(MergeMetadata... mergers) {
        if (mergers == null) {
            return;
        }
        if (this.mergers == null) {
            this.mergers = new ArrayList<>();
        }
        for (MergeMetadata item : mergers) {
            this.mergers.add(item);
        }
    }

}
