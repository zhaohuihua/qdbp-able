package com.gitee.zhaohuihua.tools.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gitee.zhaohuihua.tools.excel.model.ColumnInfo;
import com.gitee.zhaohuihua.tools.excel.parse.IndexListConfig;
import com.gitee.zhaohuihua.tools.excel.parse.IndexRangeConfig;
import com.gitee.zhaohuihua.tools.excel.parse.NameListConfig;
import com.gitee.zhaohuihua.tools.excel.parse.Required;
import com.gitee.zhaohuihua.tools.excel.rule.DateRule;
import com.gitee.zhaohuihua.tools.excel.rule.MapRule;
import com.gitee.zhaohuihua.tools.excel.rule.PresetRule;
import com.gitee.zhaohuihua.tools.utils.Config;
import com.gitee.zhaohuihua.tools.utils.StringTools;
import com.gitee.zhaohuihua.tools.utils.VerifyTools;

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

    private Config config;

    /** 列名 **/
    private List<ColumnInfo> columns;
    /** 列名与转换规则的映射表(配置规则) **/
    private Map<String, PresetRule> rules;
    /** 跳过几行 **/
    private Integer skipRows;
    /** 表头 **/
    private IndexRangeConfig headerRows;
    /** 页脚 **/
    private IndexRangeConfig footerRows;
    /** Sheet名称填充至哪个字段 **/
    private String sheetNameFillTo;

    /** Sheet序号配置, 默认读取第1个Sheet **/
    private IndexListConfig sheetIndexs = new IndexListConfig(0);
    /** Sheet名称配置, 默认全部匹配 **/
    private NameListConfig sheetNames = new NameListConfig();

    public XMetadata(Config config) {
        this.config = config;
        this.columns = parseColumn(config.getString("columns"));
        // 解析skipRows and headerRows
        this.skipRows = config.getInteger("skip.rows", false);
        String headerRow = config.getString("header.rows", false);
        if (VerifyTools.isBlank(headerRow)) {
            headerRow = config.getString("header.row", false);
        }
        if (VerifyTools.isNotBlank(headerRow)) {
            this.headerRows = new IndexRangeConfig(headerRow, 1); // 配置项从1开始, 程序从0开始
            if (this.skipRows == null) {
                this.skipRows = this.headerRows.getMax() + 1;
            }
        } else {
            if (this.skipRows == null) {
                this.skipRows = 0;
            }
        }
        // 解析footerRows
        // 导入时每个excel的页脚位置有可能不一样, 所以导入不能指定页脚行, 只能在导入之前把页脚删掉
        // 如果页脚有公式, 那么数据行必须至少2行, 公式的范围必须包含这两行数据,
        // 如SUM($E$5:E6), 而不能是SUM($E$5:E5), 否则导出数据行之后公式不正确
        String footerRow = config.getString("footer.rows", false);
        if (VerifyTools.isBlank(footerRow == null)) {
            footerRow = config.getString("footer.row", false);
        }
        if (VerifyTools.isNotBlank(footerRow)) {
            this.footerRows = new IndexRangeConfig(footerRow, 1); // 配置项从1开始, 程序从0开始
        }

        this.sheetNameFillTo = config.getString("sheet.name.fill.to", false);
        String sheetIndexText = config.getString("sheet.index", false);
        String sheetNameText = config.getString("sheet.name", false);
        if (VerifyTools.isNotBlank(sheetIndexText)) {
            sheetIndexs = new IndexListConfig(sheetIndexText, 1); // 配置项从1开始
        }
        if (VerifyTools.isNotBlank(sheetNameText)) {
            sheetNames = new NameListConfig(sheetNameText);
            // 有SheetName规则而没有SheetIndex规则时, SheetIndex全部通过
            if (VerifyTools.isBlank(sheetIndexText)) {
                sheetIndexs = new IndexListConfig();
            }
        }

        this.rules = new HashMap<>();
        for (ColumnInfo column : columns) {
            if (column == null) {
                continue;
            }
            String field = column.getField();
            String key;
            if (config.getString(key = "rule.date." + field, false) != null) {
                this.rules.put(field, new DateRule(config.getString(key)));
            } else if (config.getString(key = "rule.map." + field, false) != null) {
                this.rules.put(field, new MapRule(config.getString(key)));
            }
        }
    }

    public boolean isEnableSheet(int sheetIndex, String sheetName) {
        return sheetIndexs.isEnable(sheetIndex) && sheetNames.isEnable(sheetName);
    }

    public boolean isEnableHeader(int rowIndex) {
        return headerRows != null && headerRows.isEnable(rowIndex);
    }

    public boolean isEnableFooter(int rowIndex) {
        return footerRows != null && footerRows.isEnable(rowIndex);
    }

    private List<ColumnInfo> parseColumn(String text) {
        List<ColumnInfo> columns = new ArrayList<>();
        String[] array = StringTools.split(text);
        for (int i = 0; i < array.length; i++) {
            Required required = Required.of(array[i]);
            if (required == null) {
                columns.add(null);
            } else {
                columns.add(new ColumnInfo(i, required.getName(), required.isRequired()));
            }
        }
        return columns;
    }

    public Config getConfig() {
        return config;
    }

    public List<ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }

    public void addColumn(ColumnInfo column) {
        this.columns.add(column);
    }

    public Integer getSkipRows() {
        return skipRows;
    }

    public void setSkipRows(Integer skipRows) {
        this.skipRows = skipRows;
    }

    public IndexRangeConfig getHeaderRows() {
        return headerRows;
    }

    public void setHeaderRows(IndexRangeConfig headerRows) {
        this.headerRows = headerRows;
    }

    public IndexRangeConfig getFooterRows() {
        return footerRows;
    }

    public void setFooterRows(IndexRangeConfig footerRows) {
        this.footerRows = footerRows;
    }

    public IndexListConfig getSheetIndexs() {
        return sheetIndexs;
    }

    public void setSheetIndexs(IndexListConfig sheetIndexs) {
        this.sheetIndexs = sheetIndexs;
    }

    public NameListConfig getSheetNames() {
        return sheetNames;
    }

    public void setSheetNames(NameListConfig sheetNames) {
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
        this.rules.put(column, rule);
    }

    public PresetRule getRule(String column) {
        return this.rules.get(column);
    }
}
