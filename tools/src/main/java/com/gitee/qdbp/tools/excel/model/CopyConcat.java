package com.gitee.qdbp.tools.excel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.able.utils.ConvertTools;

/**
 * 字段复制合并参数<br>
 * 将多个字段复制合并到一个字段
 *
 * @author zhaohuihua
 * @version 181105
 */
public class CopyConcat implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 分隔符 **/
    private String separator;
    /** 目标字段 **/
    private String targetField;
    /** 源字段 **/
    private List<String> sourceFields;

    /** 分隔符 **/
    public String getSeparator() {
        return separator;
    }

    /** 分隔符 **/
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /** 目标字段 **/
    public String getTargetField() {
        return targetField;
    }

    /** 目标字段 **/
    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    /** 源字段 **/
    public List<String> getSourceFields() {
        return sourceFields;
    }

    /** 源字段 **/
    public void setSourceFields(List<String> sourceFields) {
        this.sourceFields = sourceFields;
    }

    /** 增加源字段 **/
    public void addSourceFields(String... sourceFields) {
        if (sourceFields == null) {
            return;
        }
        if (this.sourceFields == null) {
            this.sourceFields = new ArrayList<>();
        }
        for (String i : sourceFields) {
            this.sourceFields.add(i);
        }
    }

    public String toString() {
        if (targetField == null) {
            return "NULL";
        }
        String separator = this.separator == null ? " " : this.separator;
        StringBuilder buffer = new StringBuilder();
        buffer.append("{");
        buffer.append(targetField).append(": ");
        buffer.append(ConvertTools.joinToString(sourceFields, separator));
        buffer.append("}");
        return buffer.toString();
    }
}
