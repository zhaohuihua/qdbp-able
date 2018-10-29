package com.gitee.qdbp.tools.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import com.gitee.qdbp.able.beans.KeyString;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.model.ColumnInfo;
import com.gitee.qdbp.tools.excel.parse.ContainsTextCondition;
import com.gitee.qdbp.tools.excel.parse.IndexListCondition;
import com.gitee.qdbp.tools.excel.parse.IndexRangeCondition;
import com.gitee.qdbp.tools.excel.parse.MatchesRowCondition;
import com.gitee.qdbp.tools.excel.parse.NameListCondition;
import com.gitee.qdbp.tools.excel.rule.DateRule;
import com.gitee.qdbp.tools.excel.rule.MapRule;
import com.gitee.qdbp.tools.excel.rule.PresetRule;
import com.gitee.qdbp.tools.excel.utils.MetadataTools;
import com.gitee.qdbp.tools.utils.Config;

/**
 * excel配置数据<br>
 * rule.map.areaType = { "PROVINCE":"1|省", "CITY":"2|市", "DISTRICT":"3|区|县|区/县" }<br>
 * rule.date.createTime = yyyy/MM/dd<br>
 *
 * @author zhaohuihua
 * @version 160302
 */
public class XMetadata implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    // columns和fieldRows同时存在时以columns优先
    /** 列名 **/
    private List<ColumnInfo> columns;
    /** 字段名所在的行 **/
    private IndexRangeCondition fieldRows;
    /** 列名与转换规则的映射表(配置规则) **/
    private Map<String, PresetRule> rules;
    /** 跳过几行 **/
    private Integer skipRows;
    /** 包含指定关键字时跳过此行 **/
    // skip.row.when.1 = 1:NULL
    // skip.row.when.2 = 2:小计, 10:元
    // skip.row.when.3 = 2:总计, 10:元
    // 第1列为空, 或第2列包含小计且第10列包含元, 或第2列包含总计且第10列包含元
    private List<MatchesRowCondition> skipRowWhen;
    /** 表头 **/
    private IndexRangeCondition headerRows;
    /** 页脚 **/
    private IndexRangeCondition footerRows;
    /** Sheet名称填充至哪个字段 **/
    private String sheetNameFillTo;

    /** Sheet序号配置, 默认读取第1个Sheet **/
    private IndexListCondition sheetIndexs = new IndexListCondition(0);
    /** Sheet名称配置, 默认全部匹配 **/
    private NameListCondition sheetNames = new NameListCondition();

    public XMetadata() {
        this.skipRows = 0;
        this.sheetIndexs = new IndexListCondition();
    }

    public XMetadata(Config config) {
        String columns = config.getString("columns", false);
        if (VerifyTools.isNotBlank(columns)) {
            this.columns = MetadataTools.parseFields(columns);
        } else {
            String columnRows = config.getString("columnRows", false);
            if (VerifyTools.isNotBlank(columnRows)) {
                this.fieldRows = new IndexRangeCondition(columnRows, 1); // 配置项从1开始, 程序从0开始
            } else {
                throw new IllegalStateException("excel setting columns or columnRows is required.");
            }
        }
        // 解析skipRows and headerRows
        Integer skipRows = config.getInteger("skip.rows", false);
        String headerRow = config.getString("header.rows", false);
        if (VerifyTools.isBlank(headerRow)) {
            headerRow = config.getString("header.row", false);
        }
        if (VerifyTools.isNotBlank(headerRow)) {
            this.headerRows = new IndexRangeCondition(headerRow, 1); // 配置项从1开始, 程序从0开始
        }
        if (skipRows == null) {
            if (this.fieldRows != null && this.headerRows != null) {
                skipRows = Math.max(this.fieldRows.getMax(), this.headerRows.getMax()) + 1;
            } else if (this.fieldRows != null) {
                skipRows = this.fieldRows.getMax() + 1;
            } else if (this.headerRows != null) {
                skipRows = this.headerRows.getMax() + 1;
            }
        }
        this.skipRows = skipRows == null ? 0 : skipRows;

        // 解析footerRows
        // 导入时每个excel的页脚位置有可能不一样, 所以导入不能指定页脚行, 只能在导入之前把页脚删掉
        // 如果页脚有公式, 那么数据行必须至少2行, 公式的范围必须包含这两行数据,
        // 如SUM($E$5:E6), 而不能是SUM($E$5:E5), 否则导出数据行之后公式不正确
        String footerRow = config.getString("footer.rows", false);
        if (VerifyTools.isBlank(footerRow == null)) {
            footerRow = config.getString("footer.row", false);
        }
        if (VerifyTools.isNotBlank(footerRow)) {
            this.footerRows = new IndexRangeCondition(footerRow, 1); // 配置项从1开始, 程序从0开始
        }

        this.sheetNameFillTo = config.getString("sheet.name.fill.to", false);
        String sheetIndexText = config.getString("sheet.index", false);
        String sheetNameText = config.getString("sheet.name", false);
        if (VerifyTools.isNotBlank(sheetIndexText)) {
            sheetIndexs = new IndexListCondition(sheetIndexText, 1); // 配置项从1开始
        }
        if (VerifyTools.isNotBlank(sheetNameText)) {
            sheetNames = new NameListCondition(sheetNameText);
            // 有SheetName规则而没有SheetIndex规则时, SheetIndex全部通过
            if (VerifyTools.isBlank(sheetIndexText)) {
                sheetIndexs = new IndexListCondition();
            }
        }

        Map<String, PresetRule> rules = new HashMap<>();
        List<MatchesRowCondition> skipRowWhen = new ArrayList<>();
        for (KeyString entry : config.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("skip.row.when") || key.startsWith("skip.row.when.")) {
                skipRowWhen.add(new ContainsTextCondition(value));
            } else if (key.startsWith("rule.date.")) {
                String field = key.substring("rule.data.".length());
                rules.put(field, new DateRule(value));
            } else if (key.startsWith("rule.map.")) {
                String field = key.substring("rule.map.".length());
                rules.put(field, new MapRule(value));
            }
        }
        if (!skipRowWhen.isEmpty()) {
            this.skipRowWhen = skipRowWhen;
        }
        if (!rules.isEmpty()) {
            this.rules = rules;
        }
    }

    public boolean isEnableSheet(int sheetIndex, String sheetName) {
        return sheetIndexs.isEnable(sheetIndex) && sheetNames.isEnable(sheetName);
    }

    public boolean isFieldRow(int rowIndex) {
        return fieldRows != null && fieldRows.isEnable(rowIndex);
    }

    public boolean isHeaderRow(int rowIndex) {
        return headerRows != null && headerRows.isEnable(rowIndex);
    }

    public boolean isFooterRow(int rowIndex) {
        return footerRows != null && footerRows.isEnable(rowIndex);
    }

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

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }

    public void addColumn(ColumnInfo column) {
        if (this.columns == null) {
            this.columns = new ArrayList<>();
        }
        this.columns.add(column);
    }

    public Integer getSkipRows() {
        return skipRows == null ? 0 : skipRows;
    }

    public void setSkipRows(Integer skipRows) {
        this.skipRows = skipRows;
    }

    public List<MatchesRowCondition> getSkipRowWhen() {
        return skipRowWhen;
    }

    public void setSkipRowWhen(List<MatchesRowCondition> skipRowWhen) {
        this.skipRowWhen = skipRowWhen;
    }

    public void addSkipRowWhen(MatchesRowCondition condition) {
        if (this.skipRowWhen == null) {
            this.skipRowWhen = new ArrayList<>();
        }
        this.skipRowWhen.add(condition);
    }

    public IndexRangeCondition getFieldRows() {
        return fieldRows;
    }

    public void setFieldRows(IndexRangeCondition fieldRows) {
        this.fieldRows = fieldRows;
    }

    public IndexRangeCondition getHeaderRows() {
        return headerRows;
    }

    public void setHeaderRows(IndexRangeCondition headerRows) {
        this.headerRows = headerRows;
    }

    public IndexRangeCondition getFooterRows() {
        return footerRows;
    }

    public void setFooterRows(IndexRangeCondition footerRows) {
        this.footerRows = footerRows;
    }

    public IndexListCondition getSheetIndexs() {
        return sheetIndexs;
    }

    public void setSheetIndexs(IndexListCondition sheetIndexs) {
        this.sheetIndexs = sheetIndexs;
    }

    public NameListCondition getSheetNames() {
        return sheetNames;
    }

    public void setSheetNames(NameListCondition sheetNames) {
        this.sheetNames = sheetNames;
    }

    public String getSheetNameFillTo() {
        return sheetNameFillTo;
    }

    public void setSheetNameFillTo(String sheetNameFillTo) {
        this.sheetNameFillTo = sheetNameFillTo;
    }

    public Map<String, PresetRule> getRules() {
        return rules;
    }

    public void setRules(Map<String, PresetRule> rules) {
        this.rules = rules;
    }

    public void addRule(String column, PresetRule rule) {
        if (this.rules == null) {
            this.rules = new HashMap<>();
        }
        this.rules.put(column, rule);
    }

    public PresetRule getRule(String column) {
        return this.rules.get(column);
    }
}
