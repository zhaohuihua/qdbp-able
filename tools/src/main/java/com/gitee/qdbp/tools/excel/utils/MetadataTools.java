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
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.XMetadata;
import com.gitee.qdbp.tools.excel.condition.ContainsTextCondition;
import com.gitee.qdbp.tools.excel.condition.IndexListCondition;
import com.gitee.qdbp.tools.excel.condition.IndexRangeCondition;
import com.gitee.qdbp.tools.excel.condition.MatchesRowCondition;
import com.gitee.qdbp.tools.excel.condition.NameListCondition;
import com.gitee.qdbp.tools.excel.condition.Required;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.FieldInfo;
import com.gitee.qdbp.tools.excel.rule.DateRule;
import com.gitee.qdbp.tools.excel.rule.MapRule;
import com.gitee.qdbp.tools.excel.rule.PresetRule;
import com.gitee.qdbp.tools.utils.PropertyTools;

/**
 * 元数据解析工具类
 *
 * @author zhaohuihua
 * @version 180920
 */
public class MetadataTools {

    /**
     * 解析XMetadata<br>
     * MetadataTools.parseMetadata(PropertyTools.load(filePath));
     * 
     * @param properties 配置内容
     * @return XMetadata
     */
    public static XMetadata parseMetadata(Properties properties) {

        // 解析fieldNames和fieldRows
        String sFieldNames = PropertyTools.getStringUseDefKeys(properties, "field.names", "columns");
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
        String sHeaderRows = PropertyTools.getString(properties, "header.rows", false);
        if (VerifyTools.isBlank(sHeaderRows)) {
            sHeaderRows = PropertyTools.getString(properties, "header.row", false);
        }
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

        // 解析转换规则和跳过行规则
        Map<String, PresetRule> rules = new HashMap<>();
        List<MatchesRowCondition> skipRowWhen = new ArrayList<>();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
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
        List<FieldInfo> fields = new ArrayList<>();
        for (int i = 0; i <= maxColumnIndex; i++) {
            FieldInfo column = map.get(i);
            fields.add(column);
            if (column != null) {
                isEmpty = false;
            }
        }
        return isEmpty ? new ArrayList<>() : fields;
    }

    /**
     * 解析表头数据<br>
     * 星号开头或(*)结尾的字段为必填字段: [* 姓名] or [姓名 (*)]
     * 
     * @param sheet Sheet
     * @param headerRows 表头数据所在的行
     * @param columns 字段数据
     * @return
     */
    public static Map<String, CellInfo> parseHeaders(Sheet sheet, IndexRangeCondition headerRows,
            List<FieldInfo> columns) {
        // 生成单元格信息
        Map<String, CellInfo> cells = new HashMap<>();
        // 读取标题文本
        for (FieldInfo column : columns) {
            if (column == null) {
                continue;
            }
            CellInfo cell = parseHeader(sheet, column, headerRows);
            cell.setCells(cells);
            cells.put(column.getField(), cell);
        }
        return cells;
    }

    private static CellInfo parseHeader(Sheet sheet, FieldInfo column, IndexRangeCondition headerRows) {
        int c = column.getColumn();
        String field = column.getField();
        String text = "第" + c + "列";
        boolean required = column.isRequired();
        if (headerRows != null) {
            // 如果有多行表头, 后一行覆盖前一行
            for (int r = headerRows.getMin(); r <= headerRows.getMax(); r++) {
                Row header = sheet.getRow(r);
                if (header == null) {
                    continue;
                }
                Cell cell = header.getCell(c - 1);
                if (cell == null) {
                    continue;
                }
                Object value = ExcelTools.getCellValue(cell);
                String string = value == null ? null : value.toString();
                if (VerifyTools.isBlank(string)) {
                    continue;
                }
                Required result = Required.of(string);
                text = result.getName();
                required = column.isRequired() || result.isRequired();
            }
        }
        return new CellInfo(c, field, text, required);
    }

}
