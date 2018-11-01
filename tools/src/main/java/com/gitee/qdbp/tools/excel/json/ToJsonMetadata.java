package com.gitee.qdbp.tools.excel.json;

import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.tools.excel.XMetadata;

/**
 * 数据转换参数
 *
 * @author zhaohuihua
 * @version 181101
 */
public class ToJsonMetadata extends XMetadata {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** Excel文件路径 **/
    private String fileName;
    /** 自身名称 **/
    private String selfName;
    /** ID字段名 **/
    private String idField;
    /** 合并参数 **/
    private List<MergeMetadata> mergers;

    /** Excel文件路径 **/
    public String getFileName() {
        return fileName;
    }

    /** Excel文件路径 **/
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** 自身名称 **/
    public String getSelfName() {
        return selfName;
    }

    /** 自身名称 **/
    public void setSelfName(String selfName) {
        this.selfName = selfName;
    }

    /** ID字段名 **/
    public String getIdField() {
        return idField;
    }

    /** ID字段名 **/
    public void setIdField(String idField) {
        this.idField = idField;
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
