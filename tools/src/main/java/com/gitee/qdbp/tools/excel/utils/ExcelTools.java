package com.gitee.qdbp.tools.excel.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.utils.ConvertTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.CopyConcat;
import com.gitee.qdbp.tools.excel.rule.CellRule;
import com.gitee.qdbp.tools.utils.JsonTools;

/**
 * Excel工具类
 *
 * @author zhaohuihua
 * @version 170223
 */
public abstract class ExcelTools {

    private static final Logger log = LoggerFactory.getLogger(ExcelTools.class);

    /** 公式中的单元格引用 **/
    private static final Pattern CELL_REF = Pattern.compile("(\\$?[a-z]+)(\\$?[0-9]+)", Pattern.CASE_INSENSITIVE);

    /**
     * 列名转换为列序号<br>
     * A = 1, AA = 27, AK = 37, HH = 216
     * 
     * @param columnName 列名, 如A, AA, AK, HH
     * @return 列序号, 从1开始
     */
    public static int columnNameToIndex(String columnName) {
        char[] chars = columnName.toUpperCase().toCharArray();
        int index = 0;
        for (int i = 0, len = chars.length; i < len; i++) {
            char c = chars[len - 1 - i];
            if (c < 'A' || c > 'Z') {
                throw new NumberFormatException(columnName);
            }
            index += (c - 'A' + 1) * Math.pow(26, i);
        }
        return index; // 从1开始
    }

    /**
     * 列序号转换为列名<br>
     * 1 = A, 27 = AA, 37 = AK, 216 = HH
     * 
     * @param columnIndex 列序号, 从1开始
     * @return 列名, 如A, AA, AK, HH
     */
    public static String columnIndexToName(int columnIndex) {
        if (columnIndex <= 0) {
            throw new IllegalArgumentException("columnIndex must be greater than 0.");
        }

        StringBuilder buffer = new StringBuilder();
        int temp = columnIndex;
        int min = 'A';
        int max = 'Z';
        int radix = max - min + 1;
        while (temp > 0) {
            int n = temp % radix;
            if (n == 0) {
                n = radix;
            }
            buffer.append((char) (min + n - 1));
            temp = (temp - n) / radix;
        }
        return buffer.reverse().toString();

    }

    /** 将多个字段复制合并到一个字段 **/
    public static void copyConcat(Map<String, Object> data, List<CopyConcat> copyConcatFields) {
        if (VerifyTools.isBlank(copyConcatFields)) {
            return;
        }
        for (CopyConcat i : copyConcatFields) {
            String targetField = i.getTargetField();
            List<String> sourceFields = i.getSourceFields();
            if (VerifyTools.isAnyBlank(targetField, sourceFields)) {
                continue;
            }
            String separator = i.getSeparator() == null ? " " : i.getSeparator();
            StringBuilder buffer = new StringBuilder();
            for (String src : sourceFields) {
                Object value = data.get(src);
                if (VerifyTools.isNotBlank(value)) {
                    if (buffer.length() > 0) {
                        buffer.append(separator);
                    }
                    buffer.append(value);
                }
            }
            data.put(targetField, buffer.toString());
        }
    }

    public static void copyRow(Row src, Row target, boolean copyValue) {
        if (src == null || target == null) {
            return;
        }

        target.setHeight(src.getHeight());
        target.setRowStyle(src.getRowStyle());

        int sindex = src.getRowNum();
        int tindex = target.getRowNum();

        Iterator<Cell> iterator = src.cellIterator();
        while (iterator.hasNext()) {
            Cell scell = iterator.next();
            Cell tcell = target.getCell(scell.getColumnIndex(), Row.CREATE_NULL_AS_BLANK);

            // 复制单元格
            copyCell(scell, tcell, copyValue);

            // 处理公式: 按照相对位置处理公式
            // 例如, 从第3行复制公式, 公式=$A$1+C2+D3+E4
            // 复制到第10行, 公式就应该变成=$A$1+C9+D10+E11
            // $开头的行号$A$1是固定不变的
            int cellType = scell.getCellType();
            if (cellType == Cell.CELL_TYPE_FORMULA) {
                String formula = scell.getCellFormula();

                Matcher matcher = CELL_REF.matcher(formula);
                int index = 0;
                StringBuilder buffer = new StringBuilder();
                while (matcher.find()) {
                    buffer.append(formula.substring(index, matcher.start()));
                    buffer.append(matcher.group(1)); // 列标
                    String row = matcher.group(2); // 行标
                    if (row.startsWith("$")) { // 固定行标
                        buffer.append(row);
                    } else { // 计算行标
                        buffer.append(ConvertTools.toInteger(row) + (tindex - sindex));
                    }
                    index = matcher.end();
                }
                buffer.append(formula.substring(index));
                tcell.setCellFormula(buffer.toString());
            }
        }
    }

    public static void copyCell(Cell src, Cell target, boolean copyValue) {
        if (src == null || target == null) {
            return;
        }

        // 复制样式
        target.setCellStyle(src.getCellStyle());

        // 单元格类型
        int cellType = src.getCellType();
        target.setCellType(cellType);

        if (cellType == Cell.CELL_TYPE_FORMULA) { // 公式
            target.setCellFormula(src.getCellFormula());
        } else if (copyValue) { // 复制内容

            if (cellType == Cell.CELL_TYPE_ERROR) { // 错误
                target.setCellErrorValue(src.getErrorCellValue());
            } else {
                Object value = getCellValue(src);
                setCellValue(target, value);
            }

            // 评论
            Comment comment = src.getCellComment();
            if (comment == null) {
                target.removeCellComment();
            } else {
                target.setCellComment(comment);
            }
        }
    }

    public static Object getCellValue(Cell cell) {

        if (cell == null) {
            return null;
        }

        Object object = null;

        // 获取单元格类型
        int cellType = cell.getCellType();
        if (cellType == Cell.CELL_TYPE_FORMULA) {
            // 如果单元格类型是公式, 取公式结果的类型
            cellType = cell.getCachedFormulaResultType();
        }

        switch (cellType) {
        case Cell.CELL_TYPE_STRING:
            object = cell.getStringCellValue();
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            object = cell.getBooleanCellValue();
            break;
        case Cell.CELL_TYPE_NUMERIC:
            switch (cell.getCellStyle().getDataFormat()) {
            case 58: // yyyy-MM-dd
            case 57: // yyyy年m月
            case 17: // yyyy-MM
            case 20: // HH:mm
            case 21: // HH:mm:ss
            case 22: // yyyy-MM-dd HH:mm:ss
            case 32: // h时mm分
                object = cell.getDateCellValue();
                break;
            default:
                if (DateUtil.isCellDateFormatted(cell)) { // 日期时间
                    object = cell.getDateCellValue();
                } else { // 数字
                    // cell.getNumericCellValue()取到的都是double型
                    // 比如id=10001, 就会变成10001.0
                    // 那么, 如果数字没有小数部分, 就统一转换为long型
                    double number = cell.getNumericCellValue();
                    long integer = (long) number;
                    if (number == integer) {
                        object = integer;
                    } else {
                        object = number;
                    }
                }
                break;
            }
            break;
        default:
            break;
        }
        if (object instanceof String) {
            object = ((String) object).trim();
        }
        return object;
    }

    public static boolean setCellValue(Cell cell, Object value) {
        try {
            doSetCellValue(cell, value);
            return true;
        } catch (Exception e) {
            log.error("SetCellValueError, " + e.toString());
            cell.setCellValue(TypeUtils.castToString(value));
            return false;
        }
    }

    private static void doSetCellValue(Cell cell, Object value) {

        if (cell == null) {
            return;
        }
        if (value == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
            return;
        }

        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_BLANK: // 空单元格, 根据数据类型赋值
            if (value instanceof Date) {
                cell.setCellValue((Date) value);
            } else if (value instanceof Calendar) {
                cell.setCellValue((Calendar) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue(TypeUtils.castToBoolean(value));
            } else if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else {
                cell.setCellValue(TypeUtils.castToString(value));
            }
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            cell.setCellValue(TypeUtils.castToBoolean(value));
            break;
        case Cell.CELL_TYPE_NUMERIC:
            switch (cell.getCellStyle().getDataFormat()) {
            case 58: // yyyy-MM-dd
            case 57: // yyyy年m月
            case 17: // yyyy-MM
            case 20: // HH:mm
            case 21: // HH:mm:ss
            case 22: // yyyy-MM-dd HH:mm:ss
            case 32: // h时mm分
                cell.setCellValue(TypeUtils.castToDate(value));
                break;
            default:
                if (DateUtil.isCellDateFormatted(cell)) { // 日期时间
                    cell.setCellValue(TypeUtils.castToDate(value));
                } else { // 数字
                    cell.setCellValue(TypeUtils.castToDouble(value));
                }
                break;
            }
            break;
        default: // Cell.CELL_TYPE_STRING and other
            cell.setCellValue(TypeUtils.castToString(value));
            break;
        }
    }

    public static String newConvertErrorMessage(CellInfo cell, CellRule rule) {
        String fmt = "Cell[%s,%s], value:%s, failed to execute convert rule:%s";
        String columnName = ExcelTools.columnIndexToName(cell.getColumn());
        String valueString = JsonTools.toLogString(cell.getValue());
        return String.format(fmt, cell.getRow(), columnName, valueString, rule.toString());
    }
}
