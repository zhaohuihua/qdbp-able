package com.gitee.qdbp.tools.excel.model;

import java.util.List;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.rule.CellRule;

/**
 * 列信息
 *
 * @author zhaohuihua
 * @version 190317
 */
public class ColumnInfo extends FieldInfo {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 标题文本 **/
    private String title;
    /** 转换规则 **/
    private List<CellRule> rules;

    public ColumnInfo() {
    }

    public ColumnInfo(Integer column, String field, String title, boolean required) {
        super(column, field, required);
        this.title = title;
    }

    /** 获取标题文本 **/
    public String getTitle() {
        return title;
    }

    /** 设置标题文本 **/
    public void setTitle(String title) {
        this.title = title;
    }

    /** 获取转换规则 **/
    public List<CellRule> getRules() {
        return rules;
    }

    /** 设置转换规则 **/
    public void setRules(List<CellRule> rules) {
        this.rules = rules;
    }

    /**
     * 将当前对象转换为子类对象
     *
     * @param clazz 目标类型
     * @return 目标对象
     */
    public <T extends FieldInfo> T to(Class<T> clazz) {
        T instance = super.to(clazz);

        if (instance instanceof ColumnInfo) {
            ColumnInfo real = (ColumnInfo) instance;
            real.setTitle(this.getTitle()); // 标题文本
            real.setRules(this.getRules()); // 转换规则
        }
        return instance;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (VerifyTools.isNotBlank(this.getTitle())) {
            buffer.append(this.getTitle()).append(':');
        }
        if (VerifyTools.isBlank(this.getField())) {
            buffer.append("{UNKNOWN}");
        } else {
            buffer.append(this.getField());
        }
        if (this.getColumn() != null) {
            buffer.append('[').append(this.getColumn()).append("]");
        }
        if (this.isRequired()) {
            buffer.append("(*)");
        }
        return buffer.toString();
    }

}
