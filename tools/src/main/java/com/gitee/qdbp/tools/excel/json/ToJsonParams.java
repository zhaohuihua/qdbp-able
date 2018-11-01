package com.gitee.qdbp.tools.excel.json;

import java.io.Serializable;
import java.util.List;

/**
 * 数据转换参数
 *
 * @author zhaohuihua
 * @version 181101
 */
public class ToJsonParams implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 主字段的配置信息 **/
    private String mainField;
    /** 合并字段的配置信息 **/
    private List<String> mergeFields;

    /** 主字段的配置信息 **/
    public String getMainField() {
        return mainField;
    }

    /** 主字段的配置信息 **/
    public void setMainField(String mainField) {
        this.mainField = mainField;
    }

    /** 合并字段的配置信息 **/
    public List<String> getMergeFields() {
        return mergeFields;
    }

    /** 合并字段的配置信息 **/
    public void setMergeFields(List<String> mergeFields) {
        this.mergeFields = mergeFields;
    }

}
