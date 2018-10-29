package com.gitee.qdbp.tools.excel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.ColumnInfo;
import com.gitee.qdbp.tools.excel.parse.IndexRangeCondition;
import com.gitee.qdbp.tools.excel.parse.Required;

/**
 * 元数据解析工具类
 *
 * @author zhaohuihua
 * @version 180920
 */
public class MetadataTools {

    /**
     * 解析字段列表配置<br>
     * 星号开头或(*)结尾的字段为必填字段: [*name] or [name(*)]
     * 
     * @param text 配置内容
     * @return
     */
    public static List<ColumnInfo> parseFields(String text) {
        if (VerifyTools.isBlank(text)) {
            return null;
        }
        List<ColumnInfo> columns = new ArrayList<>();
        String[] array = StringTools.split(text);
        for (int i = 0; i < array.length; i++) {
            String item = array[i];
            if (VerifyTools.isBlank(item)) {
                columns.add(null);
            } else {
                Required required = Required.of(item);
                columns.add(new ColumnInfo(i + 1, required.getName(), required.isRequired()));
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
    public static List<ColumnInfo> parseFields(Sheet sheet, IndexRangeCondition fieldRows) {
        if (sheet == null || fieldRows == null) {
            return null;
        }

        // 如果有多行表头, 后一行覆盖前一行
        Map<Integer, ColumnInfo> map = new HashMap<>();
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
                map.put(i, new ColumnInfo(i + 1, required.getName(), required.isRequired()));
            }
        }
        List<ColumnInfo> fields = new ArrayList<>();
        for (int i = 0; i < maxColumnIndex; i++) {
            fields.add(map.get(i));
        }
        return fields;
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
            List<ColumnInfo> columns) {
        // 生成单元格信息
        Map<String, CellInfo> cells = new HashMap<>();
        // 读取标题文本
        for (ColumnInfo column : columns) {
            if (column == null) {
                continue;
            }
            CellInfo cell = parseHeader(sheet, column, headerRows);
            cell.setCells(cells);
            cells.put(column.getField(), cell);
        }
        return cells;
    }

    private static CellInfo parseHeader(Sheet sheet, ColumnInfo column, IndexRangeCondition headerRows) {
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
