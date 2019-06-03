package com.gitee.qdbp.tools.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.poi.ss.usermodel.Row;
import com.gitee.qdbp.tools.excel.condition.IndexListCondition;
import com.gitee.qdbp.tools.excel.condition.IndexRangeCondition;
import com.gitee.qdbp.tools.excel.condition.MatchesRowCondition;
import com.gitee.qdbp.tools.excel.condition.NameListCondition;
import com.gitee.qdbp.tools.excel.model.CopyConcat;
import com.gitee.qdbp.tools.excel.model.FieldInfo;
import com.gitee.qdbp.tools.excel.rule.CellRule;
import com.gitee.qdbp.tools.excel.utils.MetadataTools;
import com.gitee.qdbp.tools.utils.Config;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * excel配置数据<br>
 * 有哪些配置项详见{@linkplain MetadataTools#parseProperties(Properties)}<br>
 *
 * @author zhaohuihua
 * @version 160302
 */
public class XMetadata implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    // fieldInfos和fieldRows同时存在时以fieldInfos优先
    /** 字段信息列表 **/
    private List<FieldInfo> fieldInfos;
    /** 字段名所在的行 **/
    private IndexRangeCondition fieldRows;
    /** 字段与转换规则的映射表(配置规则) **/
    private Map<String, List<CellRule>> rules;
    /** 跳过几行 **/
    private Integer skipRows;
    /** 包含指定关键字时跳过此行 **/
    // skip.row.when.1 = 1:NULL
    // skip.row.when.2 = 2:小计, 10:元
    // skip.row.when.3 = 2:总计, 10:元
    // 第1列为空, 或第2列包含小计且第10列包含元, 或第2列包含总计且第10列包含元
    private List<MatchesRowCondition> skipRowWhen;
    /** 表头所在的行号 **/
    private IndexRangeCondition headerRows;
    /** 页脚所在的行号 **/
    private IndexRangeCondition footerRows;
    /** Sheet名称填充至哪个字段 **/
    private String sheetNameFillTo;
    /** Sheet序号配置, 默认读取第1个Sheet **/
    private IndexListCondition sheetIndexs = new IndexListCondition(0);
    /** Sheet名称配置, 默认全部匹配 **/
    private NameListCondition sheetNames = new NameListCondition();

    /** 字段复制合并参数 **/
    private List<CopyConcat> copyConcatFields;

    public XMetadata() {
        this.skipRows = 0;
        this.sheetIndexs = new IndexListCondition();
    }

    /**
     * 构造函数
     * 
     * @param config 配置项
     * @deprecated 改为{@linkplain MetadataTools#parseProperties(Properties)}
     */
    @Deprecated
    public XMetadata(Config config) {
        XMetadata metadata = MetadataTools.parseProperties(config.properties());
        this.setFieldInfos(metadata.getFieldInfos()); // 字段信息列表
        this.setFieldRows(metadata.getFieldRows()); // 字段名所在的行
        this.setRules(metadata.getRules()); // 字段与转换规则的映射表
        this.setSkipRows(metadata.getSkipRows()); // 跳过几行
        this.setSkipRowWhen(metadata.getSkipRowWhen()); // 包含指定关键字时跳过此行
        this.setHeaderRows(metadata.getHeaderRows()); // 表头所在的行号
        this.setFooterRows(metadata.getFooterRows()); // 页脚所在的行号
        this.setSheetNameFillTo(metadata.getSheetNameFillTo()); // Sheet名称填充至哪个字段
        this.setSheetIndexs(metadata.getSheetIndexs()); // Sheet序号配置
        this.setSheetNames(metadata.getSheetNames()); // Sheet名称配置
    }

    /** 判断指定页签是否有效 **/
    public boolean isEnableSheet(int sheetIndex, String sheetName) {
        if (sheetIndexs == null && sheetNames == null) {
            return false;
        } else if (sheetIndexs == null) {
            return sheetNames.isEnable(sheetName);
        } else if (sheetNames == null) {
            return sheetIndexs.isEnable(sheetIndex);
        } else {
            return sheetIndexs.isEnable(sheetIndex) && sheetNames.isEnable(sheetName);
        }
    }

    /** 判断指定行是否为字段行 **/
    public boolean isFieldRow(int rowIndex) {
        return fieldRows != null && fieldRows.isEnable(rowIndex);
    }

    /** 判断指定行是否为页头 **/
    public boolean isHeaderRow(int rowIndex) {
        return headerRows != null && headerRows.isEnable(rowIndex);
    }

    /** 判断指定行是否为页脚 **/
    public boolean isFooterRow(int rowIndex) {
        return footerRows != null && footerRows.isEnable(rowIndex);
    }

    /** 判断指定行是否跳过 **/
    public boolean isSkipRow(Row row) {
        if (VerifyTools.isBlank(skipRowWhen)) {
            return false;
        }
        for (MatchesRowCondition condition : skipRowWhen) {
            if (condition.isMatches(row)) {
                return true;
            }
        }
        return false;
    }

    /** 字段信息列表 **/
    public List<FieldInfo> getFieldInfos() {
        return fieldInfos;
    }

    /** 字段信息列表 **/
    public void setFieldInfos(List<FieldInfo> fieldInfos) {
        this.fieldInfos = fieldInfos;
    }

    /** 增加字段名称信息 **/
    public void addColumn(FieldInfo fieldInfo) {
        if (this.fieldInfos == null) {
            this.fieldInfos = new ArrayList<>();
        }
        this.fieldInfos.add(fieldInfo);
    }

    /** 跳过几行 **/
    public Integer getSkipRows() {
        return skipRows == null ? 0 : skipRows;
    }

    /** 跳过几行 **/
    public void setSkipRows(Integer skipRows) {
        this.skipRows = skipRows;
    }

    /** 包含指定关键字时跳过此行 **/
    public List<MatchesRowCondition> getSkipRowWhen() {
        return skipRowWhen;
    }

    /** 包含指定关键字时跳过此行 **/
    public void setSkipRowWhen(List<MatchesRowCondition> skipRowWhen) {
        this.skipRowWhen = skipRowWhen;
    }

    /** 增加跳过某行的关键字 **/
    public void addSkipRowWhen(MatchesRowCondition condition) {
        if (this.skipRowWhen == null) {
            this.skipRowWhen = new ArrayList<>();
        }
        this.skipRowWhen.add(condition);
    }

    /** 字段名所在的行 **/
    public IndexRangeCondition getFieldRows() {
        return fieldRows;
    }

    /** 字段名所在的行 **/
    public void setFieldRows(IndexRangeCondition fieldRows) {
        this.fieldRows = fieldRows;
    }

    /** 表头所在的行号 **/
    public IndexRangeCondition getHeaderRows() {
        return headerRows;
    }

    /** 表头所在的行号 **/
    public void setHeaderRows(IndexRangeCondition headerRows) {
        this.headerRows = headerRows;
    }

    /** 页脚所在的行号 **/
    public IndexRangeCondition getFooterRows() {
        return footerRows;
    }

    /** 页脚所在的行号 **/
    public void setFooterRows(IndexRangeCondition footerRows) {
        this.footerRows = footerRows;
    }

    /** Sheet序号配置, 默认读取第1个Sheet **/
    public IndexListCondition getSheetIndexs() {
        return sheetIndexs;
    }

    /** Sheet序号配置, 默认读取第1个Sheet **/
    public void setSheetIndexs(IndexListCondition sheetIndexs) {
        this.sheetIndexs = sheetIndexs;
    }

    /** Sheet名称配置, 默认全部匹配 **/
    public NameListCondition getSheetNames() {
        return sheetNames;
    }

    /** Sheet名称配置, 默认全部匹配 **/
    public void setSheetNames(NameListCondition sheetNames) {
        this.sheetNames = sheetNames;
    }

    /** Sheet名称填充至哪个字段 **/
    public String getSheetNameFillTo() {
        return sheetNameFillTo;
    }

    /** Sheet名称填充至哪个字段 **/
    public void setSheetNameFillTo(String sheetNameFillTo) {
        this.sheetNameFillTo = sheetNameFillTo;
    }

    /** 字段与转换规则的映射表 **/
    public Map<String, List<CellRule>> getRules() {
        return rules;
    }

    /** 字段与转换规则的映射表 **/
    public void setRules(Map<String, List<CellRule>> rules) {
        this.rules = rules;
    }

    /** 增加转换规则 **/
    public void addRule(String column, List<CellRule> rule) {
        if (this.rules == null) {
            this.rules = new HashMap<>();
        }
        this.rules.put(column, rule);
    }

    /** 获取指定列的转换规则 **/
    public List<CellRule> getRule(String column) {
        return this.rules == null ? null : this.rules.get(column);
    }

    /** 字段复制合并参数 **/
    public List<CopyConcat> getCopyConcatFields() {
        return copyConcatFields;
    }

    /** 字段复制合并参数 **/
    public void setCopyConcatFields(List<CopyConcat> copyConcatFields) {
        this.copyConcatFields = copyConcatFields;
    }

    /** 增加字段复制合并参数 **/
    public void addCopyConcatFields(CopyConcat... copyConcatFields) {
        if (copyConcatFields == null) {
            return;
        }
        if (this.copyConcatFields == null) {
            this.copyConcatFields = new ArrayList<>();
        }
        for (CopyConcat item : copyConcatFields) {
            this.copyConcatFields.add(item);
        }
    }

    /**
     * 将当前对象转换为子类对象
     *
     * @param clazz 目标类型
     * @return 目标对象
     */
    public <T extends XMetadata> T to(Class<T> clazz) {
        T instance;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create " + clazz.getSimpleName() + " instance.", e);
        }

        instance.setFieldInfos(this.getFieldInfos()); // 字段信息列表
        instance.setFieldRows(this.getFieldRows()); // 字段名所在的行
        instance.setRules(this.getRules()); // 字段与转换规则的映射表
        instance.setSkipRows(this.getSkipRows()); // 跳过几行
        instance.setSkipRowWhen(this.getSkipRowWhen()); // 包含指定关键字时跳过此行
        instance.setHeaderRows(this.getHeaderRows()); // 表头所在的行号
        instance.setFooterRows(this.getFooterRows()); // 页脚所在的行号
        instance.setSheetNameFillTo(this.getSheetNameFillTo()); // Sheet名称填充至哪个字段
        instance.setSheetIndexs(this.getSheetIndexs()); // Sheet序号配置
        instance.setSheetNames(this.getSheetNames()); // Sheet名称配置
        instance.setCopyConcatFields(this.getCopyConcatFields()); // 字段复制合并参数 
        return instance;
    }

}
