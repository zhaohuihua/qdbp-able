package com.gitee.qdbp.tools.excel.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PaneInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.CopyConcat;
import com.gitee.qdbp.tools.excel.rule.CellRule;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.JsonTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

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

    /** 获取Sheet的总行数 **/
    public static int getTotalRowsOfSheet(Sheet sheet) {
        // sheet.getPhysicalNumberOfRows()这个方法有坑
        // 之前填好30行数据, 之后又从别的表格复制了5行插入在最前面, 总行数应该是35才对, 但这个方法返回的依然是30
        return Math.max(sheet.getLastRowNum(), sheet.getPhysicalNumberOfRows());
    }

    /** 获取Row的总列数 **/
    public static int getTotalColumnsOfRow(Row row) {
        return Math.max(row.getLastCellNum(), row.getPhysicalNumberOfCells());
    }

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

    /**
     * 复制Sheet<br>
     * TODO 目前只复制了部分属性, 另外复制前景色和背景色有问题
     * 
     * @param src 复制源
     * @param target 目标行
     * @param copyValue 是否复制值
     */
    public static void copySheet(Sheet src, Sheet target, boolean copyValue) {
        if (src == null || target == null) {
            return;
        }

        boolean columnWidthSetted = false;
        Iterator<Row> iterator = src.rowIterator();
        while (iterator.hasNext()) {
            Row srow = iterator.next();
            Row trow = target.getRow(srow.getRowNum());
            if (trow == null) {
                trow = target.createRow(srow.getRowNum());
            }
            copyRow(srow, trow, copyValue);
            // 复制列宽,隐藏列
            if (!columnWidthSetted) {
                columnWidthSetted = true;
                Iterator<Cell> cellIterator = srow.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell scell = cellIterator.next();
                    int columnIndex = scell.getColumnIndex();
                    target.setColumnWidth(columnIndex, src.getColumnWidth(columnIndex));
                    target.setColumnHidden(columnIndex, src.isColumnHidden(columnIndex));
                }
            }
        }
        // 复制合并区域
        int sheetMergerCount = src.getNumMergedRegions();
        for (int i = 0; i < sheetMergerCount; i++) {
            CellRangeAddress range = src.getMergedRegion(i);
            target.addMergedRegion(range);
        }

        // 复制Sheet全局参数
        target.setAutobreaks(src.getAutobreaks());
        target.setDefaultColumnWidth(src.getDefaultColumnWidth());
        target.setDefaultRowHeight(src.getDefaultRowHeight());
        // 冻结窗格
        PaneInformation panelInfo = src.getPaneInformation();
        if (panelInfo != null && panelInfo.isFreezePane()) {
            target.createFreezePane(panelInfo.getVerticalSplitLeftColumn(), panelInfo.getHorizontalSplitTopRow());
        }
    }

    /**
     * 复制行
     * 
     * @param src 复制源
     * @param target 目标行
     * @param copyValue 是否复制值
     */
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
            Cell tcell = target.getCell(scell.getColumnIndex(), MissingCellPolicy.CREATE_NULL_AS_BLANK);

            // 复制单元格
            copyCell(scell, tcell, copyValue);

            // 处理公式: 按照相对位置处理公式
            // 例如, 从第3行复制公式, 公式=$A$1+C2+D3+E4
            // 复制到第10行, 公式就应该变成=$A$1+C9+D10+E11
            // $开头的行号$A$1是固定不变的
            CellType cellType = scell.getCellTypeEnum();
            if (cellType == CellType.FORMULA) {
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

    /**
     * 复制单元格
     * 
     * @param src 复制源
     * @param target 目标行
     * @param copyValue 是否复制值
     */
    public static void copyCell(Cell src, Cell target, boolean copyValue) {
        if (src == null || target == null) {
            return;
        }

        // 复制样式
        copyCellStyle(src, target);

        // 单元格类型
        CellType cellType = src.getCellTypeEnum();
        target.setCellType(cellType);

        if (cellType == CellType.FORMULA) { // 公式
            target.setCellFormula(src.getCellFormula());
        } else if (copyValue) { // 复制内容

            if (cellType == CellType.ERROR) { // 错误
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

    private static void copyCellStyle(Cell src, Cell target) {
        if (src.getSheet().getWorkbook() == target.getSheet().getWorkbook()) {
            target.setCellStyle(src.getCellStyle());
        } else {
            CellStyle srcStyle = src.getCellStyle();
            CellStyle targetStyle = target.getSheet().getWorkbook().createCellStyle();
            copyCellStyle(srcStyle, targetStyle);
            target.setCellStyle(targetStyle);
            copyCellFont(src, target);
        }
    }

    private static void copyCellStyle(CellStyle src, CellStyle target) {
        target.setAlignment(src.getAlignmentEnum());
        // 边框和边框颜色
        target.setBorderBottom(src.getBorderBottomEnum());
        target.setBorderLeft(src.getBorderLeftEnum());
        target.setBorderRight(src.getBorderRightEnum());
        target.setBorderTop(src.getBorderTopEnum());
        target.setTopBorderColor(src.getTopBorderColor());
        target.setBottomBorderColor(src.getBottomBorderColor());
        target.setRightBorderColor(src.getRightBorderColor());
        target.setLeftBorderColor(src.getLeftBorderColor());

        // 为什么复制背景色不管用,复制前景色背景就变全黑了?
        // target.setFillBackgroundColor(src.getFillBackgroundColor()); // 背景色
        // target.setFillForegroundColor(src.getFillForegroundColor()); // 前景色

        target.setDataFormat(src.getDataFormat());
        target.setFillPattern(src.getFillPatternEnum());

        target.setHidden(src.getHidden());
        target.setIndention(src.getIndention()); // 首行缩进
        target.setLocked(src.getLocked());
        target.setRotation(src.getRotation()); // 旋转
        target.setVerticalAlignment(src.getVerticalAlignmentEnum());
        target.setWrapText(src.getWrapText());
    }

    private static void copyCellFont(Cell src, Cell target) {
        short srcFontIndex = src.getCellStyle().getFontIndex();
        Font srcFont = src.getSheet().getWorkbook().getFontAt(srcFontIndex);
        if (srcFont == null) {
            return;
        }
        if (src.getSheet().getWorkbook() == target.getSheet().getWorkbook()) {
            target.getCellStyle().setFont(srcFont);
        } else {
            Workbook wb = target.getSheet().getWorkbook();
            boolean bold = srcFont.getBold();
            short color = srcFont.getColor();
            short fontHeight = srcFont.getFontHeight();
            String fontName = srcFont.getFontName();
            boolean italic = srcFont.getItalic();
            boolean strikeout = srcFont.getStrikeout();
            short typeOffset = srcFont.getTypeOffset();
            byte underline = srcFont.getUnderline();
            Font oldFont = wb.findFont(bold, color, fontHeight, fontName, italic, strikeout, typeOffset, underline);
            if (oldFont != null) {
                target.getCellStyle().setFont(oldFont);
            } else {
                Font newFont = wb.createFont();
                newFont.setBold(bold);
                newFont.setColor(color);
                newFont.setFontHeight(fontHeight);
                newFont.setFontName(fontName);
                newFont.setItalic(italic);
                newFont.setStrikeout(strikeout);
                newFont.setTypeOffset(typeOffset);
                newFont.setUnderline(underline);
                target.getCellStyle().setFont(newFont);
            }
        }
    }

    /**
     * 获取单元格的值
     * 
     * @param cell 单元格
     * @return 单元格的值
     */
    public static Object getCellValue(Cell cell) {

        if (cell == null) {
            return null;
        }

        Object object = null;

        // 获取单元格类型
        CellType cellType = cell.getCellTypeEnum();
        if (cellType == CellType.FORMULA) {
            // 如果单元格类型是公式, 取公式结果的类型
            cellType = cell.getCachedFormulaResultTypeEnum();
        }

        switch (cellType) {
        case STRING:
            object = cell.getStringCellValue();
            break;
        case BOOLEAN:
            object = cell.getBooleanCellValue();
            break;
        case NUMERIC:
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

    /**
     * 设置单元格的值, 如果格式转换失败将不会设置单元格
     * 
     * @param cell 单元格
     * @param value 值
     * @return 是否成功
     */
    public static boolean setCellValue(Cell cell, Object value) {
        return setCellValue(cell, value, false);
    }

    /**
     * 设置单元格的值
     * 
     * @param cell 单元格
     * @param value 值
     * @param setToStringOnConvertError 格式转换失败时是否设置为toString的值
     * @return 是否成功
     */
    public static boolean setCellValue(Cell cell, Object value, boolean setToStringOnConvertError) {
        try {
            doSetCellValue(cell, value);
            return true;
        } catch (Exception e) {
            log.error("SetCellValueError, " + e.toString());
            if (setToStringOnConvertError) {
                cell.setCellValue(value == null ? "" : value.toString()); // 执行到这里value不可能为空了
            }
            return false;
        }
    }

    private static void doSetCellValue(Cell cell, Object value) {

        if (cell == null) {
            return;
        }
        if (value == null) {
            cell.setCellType(CellType.BLANK);
            return;
        }

        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
        case BLANK: // 空单元格, 根据数据类型赋值
            if (value instanceof Date) {
                cell.setCellValue((Date) value);
            } else if (value instanceof Calendar) {
                cell.setCellValue((Calendar) value);
            } else if (value instanceof Boolean) {
                cell.setCellValue(TypeUtils.castToBoolean(value));
            } else if (value instanceof Number) {
                // Cell.setCellValue()对数字的处理只有double类型
                cell.setCellValue(((Number) value).doubleValue());
            } else {
                cell.setCellValue(TypeUtils.castToString(value));
            }
            break;
        case BOOLEAN:
            cell.setCellValue(TypeUtils.castToBoolean(value));
            break;
        case NUMERIC:
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
        default: // STRING and other
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
