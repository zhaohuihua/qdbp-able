package com.gitee.qdbp.tools.excel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.XMetadata;
import com.gitee.qdbp.tools.excel.condition.CellValueCondition;
import com.gitee.qdbp.tools.excel.condition.CellValueCondition.Item;
import com.gitee.qdbp.tools.excel.condition.CellValueContainsTextCondition;
import com.gitee.qdbp.tools.excel.condition.CellValueEqualsTextCondition;
import com.gitee.qdbp.tools.excel.condition.IndexListCondition;
import com.gitee.qdbp.tools.excel.condition.IndexRangeCondition;
import com.gitee.qdbp.tools.excel.condition.MatchesRowCondition;
import com.gitee.qdbp.tools.excel.condition.NameListCondition;
import com.gitee.qdbp.tools.excel.condition.Required;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.FieldInfo;
import com.gitee.qdbp.tools.excel.rule.CellRule;
import com.gitee.qdbp.tools.excel.rule.ClearRule;
import com.gitee.qdbp.tools.excel.rule.DateRule;
import com.gitee.qdbp.tools.excel.rule.MapRule;
import com.gitee.qdbp.tools.excel.rule.NumberRule;
import com.gitee.qdbp.tools.excel.rule.RateRule;
import com.gitee.qdbp.tools.excel.rule.SplitRule;
import com.gitee.qdbp.tools.utils.PropertyTools;

/**
 * 元数据解析工具类
 *
 * @author zhaohuihua
 * @version 180920
 */
public class MetadataTools {

    private static Logger log = LoggerFactory.getLogger(MetadataTools.class);

    /**
     * 解析XMetadata<br>
     * MetadataTools.parseProperties(PropertyTools.load(filePath)); <pre>
     * ## SheetIndex从1开始, 行号从1开始, 列号从A开始
     * 
     * ## 字段对应关系, field.names/field.rows必填其一
     * ## 字段列表
     * # field.names = *id|*name|positive|height||birthday|gender|subsidy
     * ## 字段行
     * field.rows = 5
     * 
     * ## 跳过的行数(不配置时默认取字段行或标题行的下一行)
     * # skip.rows = 2
     * ## 标题行号,从1开始
     * header.rows = 2-4
     * ## 页脚行号,从1开始,只对导出生效
     * ## 导入时每个excel的页脚位置有可能不一样, 所以导入不能指定页脚行, 只能在导入之前把页脚删掉
     * ## 如果页脚有公式, 那么数据行必须至少2行, 公式的范围必须包含这两行数据, 如SUM($E$5:E6), 而不能是SUM($E$5:E5), 否则公式不会计算
     * footer.rows = 7-9
     * ## 包含指定关键字时跳过此行
     * ## A列为空, 或B列包含小计且H列包含元, 或B列包含总计且H列包含元
     * # skip.row.when = { A:"NULL" }, { B:"小计", H:"元" }, { B:"总计", H:"元" }
     * 
     * ## 加载哪些Sheet, sheet.index/sheet.name必填其一
     * ## 配置规则: * 表示全部
     * ## 配置规则: 1|2|5-8|12
     * ## 配置规则: !1 表示排除第1个
     * ## 配置规则: !1|3|5 表示排除第1/3/5个
     * # sheet.index = !1
     * ## 配置规则: * 表示全部
     * ## 配置规则: 开发|测试
     * ## 配置规则: !说明|描述 表示排除
     * sheet.name = !说明
     * ## 页签名称填充至哪个字段
     * sheet.name.fill.to = dept
     * 
     * ## 字段转换规则(支持多个转换规则)
     * rules.rate = { clear:"[^\\.\\d]" }, { number:"int" }, { rate:100 }
     * rules.tags = { split:"|" }
     * rules.positive = { map:{ true:"已转正|是|Y", false:"未转正|否|N" } }
     * rules.gender = { map:{ UNKNOWN:"未知|0", MALE:"男|1", FEMALE:"女|2" } }
     * rules.birthday = { date:"yyyy/MM/dd" }
     * </pre>
     * 
     * @param properties 配置内容
     * @return XMetadata
     */
    public static XMetadata parseProperties(Properties properties) {

        // 解析fieldNames和fieldRows
        String sFieldNames = PropertyTools.getStringUseDefKeys(properties, "field.names", "columns"); // 兼容旧版本
        List<FieldInfo> fieldNames = null;
        IndexRangeCondition fieldRows = null;
        if (VerifyTools.isNotBlank(sFieldNames)) {
            fieldNames = MetadataTools.parseFieldInfos(sFieldNames);
        } else {
            String sFieldRows = PropertyTools.getString(properties, "field.rows", false);
            if (VerifyTools.isNotBlank(sFieldRows)) {
                fieldRows = new IndexRangeCondition(sFieldRows, 1); // 配置项从1开始, 程序从0开始
            } else {
                throw new IllegalStateException("excel setting columns or columnRows is required.");
            }
        }

        // 解析headerRows
        IndexRangeCondition headerRows = null;
        String sHeaderRows = PropertyTools.getStringUseDefKeys(properties, "header.rows", "header.row"); // 兼容旧版本
        if (VerifyTools.isNotBlank(sHeaderRows)) {
            headerRows = new IndexRangeCondition(sHeaderRows, 1); // 配置项从1开始, 程序从0开始
        }
        // 解析skipRows
        Integer skipRows = PropertyTools.getInteger(properties, "skip.rows", false);
        if (skipRows == null) {
            if (fieldRows != null && headerRows != null) {
                skipRows = Math.max(fieldRows.getMax(), headerRows.getMax()) + 1;
            } else if (fieldRows != null) {
                skipRows = fieldRows.getMax() + 1;
            } else if (headerRows != null) {
                skipRows = headerRows.getMax() + 1;
            }
        }

        // 解析footerRows
        // 导入时每个excel的页脚位置有可能不一样, 所以导入不能指定页脚行, 只能在导入之前把页脚删掉
        // 如果页脚有公式, 那么数据行必须至少2行, 公式的范围必须包含这两行数据,
        // 如SUM($E$5:E6), 而不能是SUM($E$5:E5), 否则导出数据行之后公式不正确
        IndexRangeCondition footerRows = null;
        String sFooterRow = PropertyTools.getString(properties, "footer.rows", false);
        if (VerifyTools.isBlank(sFooterRow == null)) {
            sFooterRow = PropertyTools.getString(properties, "footer.row", false);
        }
        if (VerifyTools.isNotBlank(sFooterRow)) {
            footerRows = new IndexRangeCondition(sFooterRow, 1); // 配置项从1开始, 程序从0开始
        }

        // 解析sheetIndexs和sheetNames
        IndexListCondition sheetIndexs = null;
        NameListCondition sheetNames = null;
        String sSheetIndex = PropertyTools.getString(properties, "sheet.index", false);
        String sSheetName = PropertyTools.getString(properties, "sheet.name", false);
        if (VerifyTools.isNotBlank(sSheetIndex)) {
            sheetIndexs = new IndexListCondition(sSheetIndex, 1); // 配置项从1开始
        }
        if (VerifyTools.isNotBlank(sSheetName)) {
            sheetNames = new NameListCondition(sSheetName);
            // 有SheetName规则而没有SheetIndex规则时, SheetIndex全部通过
            if (VerifyTools.isBlank(sSheetIndex)) {
                sheetIndexs = new IndexListCondition();
            }
        }

        // 解析跳过行规则
        // ## 包含指定关键字时跳过此行
        // ## A列为空, 或B列包含小计且H列包含元, 或B列包含总计且H列包含元
        // # skip.row.when.contains = { A:"NULL" }, { B:"小计", H:"元" }, { B:"总计", H:"元" }
        List<MatchesRowCondition> skipRowWhen = new ArrayList<>();
        String sSkipRowWhenContains = PropertyTools.getString(properties, "skip.row.when.contains", false);
        if (VerifyTools.isNotBlank(sSkipRowWhenContains)) {
            List<List<Item>> conditions = CellValueCondition.parse(sSkipRowWhenContains);
            if (VerifyTools.isNotBlank(conditions)) {
                for (List<Item> items : conditions) {
                    skipRowWhen.add(new CellValueContainsTextCondition(items));
                }
            }
        }
        // # skip.row.when.equals = { A:"NULL" }, { B:"小计", H:"元" }, { B:"总计", H:"元" }
        String sSkipRowWhenEquals = PropertyTools.getString(properties, "skip.row.when.equals", false);
        if (VerifyTools.isNotBlank(sSkipRowWhenEquals)) {
            List<List<Item>> conditions = CellValueCondition.parse(sSkipRowWhenEquals);
            if (VerifyTools.isNotBlank(conditions)) {
                for (List<Item> items : conditions) {
                    skipRowWhen.add(new CellValueEqualsTextCondition(items));
                }
            }
        }

        // 解析转换规则
        Map<String, CellRule> rules = new HashMap<>();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (VerifyTools.isAnyBlank(key, value)) {
                    continue;
                }
                if (key.startsWith("rule.")) { // 兼容旧版本
                    // rule.map.positive = { true:"已转正|是|Y", false:"未转正|否|N" }
                    // rule.map.gender = { UNKNOWN:"未知|0", MALE:"男|1", FEMALE:"女|2" }
                    // rule.date.birthday = yyyy/MM/dd
                    String[] array = StringTools.split(key, '.');
                    if (array.length < 3) {
                        continue;
                    }
                    String field = array[2];
                    String type = array[1];
                    if (rules.containsKey(field)) {
                        continue; // 已有新版本, 忽略旧版本
                    }
                    CellRule rule = parseCellRules("{" + type + ":" + value + "}");
                    if (rule != null) {
                        rules.put(field, rule);
                    }
                } else if (key.startsWith("rules.")) { // 新版本
                    // rules.positive = { map:{ true:"已转正|是|Y", false:"未转正|否|N" } }
                    // rules.gender = { map:{ UNKNOWN:"未知|0", MALE:"男|1", FEMALE:"女|2" } }
                    // rules.birthday = { date:"yyyy/MM/dd" }
                    String field = key.substring("rules.".length());
                    CellRule rule = parseCellRules(value);
                    if (rule != null) {
                        rules.put(field, rule);
                    }
                }
            }
        }

        // Sheet名称填充至哪个字段
        String sheetNameFillTo = PropertyTools.getString(properties, "sheet.name.fill.to", false);

        XMetadata metadata = new XMetadata();
        metadata.setFieldInfos(fieldNames); // 字段信息列表
        metadata.setFieldRows(fieldRows); // 字段名所在的行
        metadata.setSkipRows(skipRows == null ? 0 : skipRows); // 跳过几行
        metadata.setHeaderRows(headerRows); // 表头所在的行号
        metadata.setFooterRows(footerRows); // 页脚所在的行号
        metadata.setSheetNameFillTo(sheetNameFillTo); // Sheet名称填充至哪个字段
        metadata.setSheetIndexs(sheetIndexs); // Sheet序号配置
        metadata.setSheetNames(sheetNames); // Sheet名称配置
        if (!skipRowWhen.isEmpty()) {
            metadata.setSkipRowWhen(skipRowWhen); // 跳过行规则
        }
        if (!rules.isEmpty()) {
            metadata.setRules(rules); // 字段与转换规则的映射表
        }
        return metadata;
    }

    /**
     * 解析单元格规则
     * 
     * @param jsonString JSON字符串
     * @return 单元格规则
     */
    // rules.height = { clear:"[^\\.\\d]" }, { number:"int" }
    // rules.positive = { map:{ true:"已转正|是|Y", false:"未转正|否|N" } }
    // rules.gender = { map:{ UNKNOWN:"未知|0", MALE:"男|1", FEMALE:"女|2" } }
    // rules.birthday = { date:"yyyy/MM/dd" }
    public static CellRule parseCellRules(String jsonString) {
        if (VerifyTools.isBlank(jsonString)) {
            return null;
        }
        if (!jsonString.startsWith("[")) {
            jsonString = "[" + jsonString + "]";
        }
        // 转换为JSON数组
        JSONArray array;
        try {
            array = JSON.parseArray(jsonString);
        } catch (Exception e) {
            log.warn("CellRuleError, json string format error: " + jsonString, e);
            return null;
        }
        // 逐一解析
        CellRule rule = null;
        for (Object i : array) {
            if (!(i instanceof JSONObject)) {
                continue;
            }
            JSONObject json = (JSONObject) i;
            for (Entry<String, Object> entry : json.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (VerifyTools.isAnyBlank(key, value)) {
                    continue;
                }
                String type = null;
                try {
                    if (key.equals(type = "clear")) {
                        rule = new ClearRule(rule, value.toString());
                    } else if (key.equals(type = "split")) {
                        rule = new SplitRule(rule, value.toString().toCharArray());
                    } else if (key.equals(type = "rate")) {
                        rule = new RateRule(rule, TypeUtils.castToDouble(value));
                    } else if (key.equals(type = "number")) {
                        rule = new NumberRule(rule, value.toString());
                    } else if (key.equals(type = "date")) {
                        rule = new DateRule(rule, value.toString());
                    } else if (key.equals(type = "map")) {
                        if (value instanceof JSONObject) {
                            rule = new MapRule(rule, (JSONObject) value);
                        } else {
                            log.warn("CellRuleError, type = {}, json string format error: {}", type, value);
                        }
                    }
                } catch (Exception e) {
                    log.warn("CellRuleError, type = {}, json string format error: {}", type, value, e);
                }
            }
        }
        return rule;
    }

    /**
     * 解析字段列表配置<br>
     * 星号开头或(*)结尾的字段为必填字段: [*name] or [name(*)]
     * 
     * @param text 配置内容
     * @return
     */
    public static List<FieldInfo> parseFieldInfos(String text) {
        if (VerifyTools.isBlank(text)) {
            return null;
        }
        List<FieldInfo> columns = new ArrayList<>();
        String[] array = StringTools.split(text);
        for (int i = 0; i < array.length; i++) {
            String item = array[i];
            if (VerifyTools.isBlank(item)) {
                columns.add(null);
            } else {
                Required required = Required.of(item);
                columns.add(new FieldInfo(i + 1, required.getName(), required.isRequired()));
            }
        }
        return columns == null ? null : columns;
    }

    /**
     * 解析字段列表配置<br>
     * 星号开头或(*)结尾的字段为必填字段: [*name] or [name(*)]
     * 
     * @param sheet Sheet
     * @param fieldRows 字段数据所在的行
     * @return
     */
    public static List<FieldInfo> parseFields(Sheet sheet, IndexRangeCondition fieldRows) {
        if (sheet == null || fieldRows == null) {
            return new ArrayList<>();
        }

        // 如果有多行表头, 后一行覆盖前一行
        Map<Integer, FieldInfo> map = new HashMap<>();
        int maxColumnIndex = 0;
        for (int r = fieldRows.getMin(); r <= fieldRows.getMax(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                if (maxColumnIndex < i) {
                    maxColumnIndex = i;
                }
                if (cell == null) {
                    continue;
                }
                Object value = ExcelTools.getCellValue(cell);
                String string = value == null ? null : value.toString();
                if (VerifyTools.isBlank(string)) {
                    continue;
                }
                Required required = Required.of(string);
                map.put(i, new FieldInfo(i + 1, required.getName(), required.isRequired()));
            }
        }
        boolean isEmpty = true;
        List<FieldInfo> fieldInfos = new ArrayList<>();
        for (int i = 0; i <= maxColumnIndex; i++) {
            FieldInfo fieldInfo = map.get(i);
            fieldInfos.add(fieldInfo);
            if (fieldInfo != null) {
                isEmpty = false;
            }
        }
        return isEmpty ? new ArrayList<>() : fieldInfos;
    }

    /**
     * 解析表头数据<br>
     * 星号开头或(*)结尾的字段为必填字段: [* 姓名] or [姓名 (*)]
     * 
     * @param sheet Sheet
     * @param headerRows 表头数据所在的行
     * @param fieldInfos 字段数据
     * @return
     */
    public static Map<String, CellInfo> parseHeaders(Sheet sheet, IndexRangeCondition headerRows,
            List<FieldInfo> fieldInfos) {
        // 生成单元格信息
        Map<String, CellInfo> cells = new HashMap<>();
        // 读取标题文本
        for (FieldInfo fieldInfo : fieldInfos) {
            if (fieldInfo == null) {
                continue;
            }
            CellInfo cellInfo = parseHeader(sheet, fieldInfo, headerRows);
            cellInfo.setCells(cells);
            cells.put(fieldInfo.getField(), cellInfo);
        }
        return cells;
    }

    private static CellInfo parseHeader(Sheet sheet, FieldInfo fieldInfo, IndexRangeCondition headerRows) {
        int column = fieldInfo.getColumn();
        String fieldName = fieldInfo.getField();
        String fieldHead = ExcelTools.columnIndexToName(column);
        boolean required = fieldInfo.isRequired();
        if (headerRows != null) {
            // 如果有多行表头, 后一行覆盖前一行
            for (int r = headerRows.getMin(); r <= headerRows.getMax(); r++) {
                Row header = sheet.getRow(r);
                if (header == null) {
                    continue;
                }
                Cell cell = header.getCell(column - 1);
                if (cell == null) {
                    continue;
                }
                Object value = ExcelTools.getCellValue(cell);
                String string = value == null ? null : value.toString();
                if (VerifyTools.isBlank(string)) {
                    continue;
                }
                Required result = Required.of(string);
                fieldHead = result.getName();
                required = fieldInfo.isRequired() || result.isRequired();
            }
        }
        return new CellInfo(column, fieldName, fieldHead, required);
    }

}
