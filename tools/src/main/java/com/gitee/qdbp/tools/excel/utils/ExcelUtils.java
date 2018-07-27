package com.gitee.qdbp.tools.excel.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.tools.utils.ConvertTools;

/**
 * Excel工具类
 *
 * @author zhaohuihua
 * @version 170223
 */
public abstract class ExcelUtils {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    /** 公式中的单元格引用 **/
    private static final Pattern CELL_REF = Pattern.compile("(\\$?[a-z]+)(\\$?[0-9]+)", Pattern.CASE_INSENSITIVE);

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
                    long integer = new Double(number).longValue();
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

}
