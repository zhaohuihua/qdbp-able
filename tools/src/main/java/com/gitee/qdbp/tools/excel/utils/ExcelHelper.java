package com.gitee.qdbp.tools.excel.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.SheetFillCallback;
import com.gitee.qdbp.tools.excel.SheetParseCallback;
import com.gitee.qdbp.tools.excel.XMetadata;
import com.gitee.qdbp.tools.excel.condition.IndexRangeCondition;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.ColumnInfo;
import com.gitee.qdbp.tools.excel.model.FieldInfo;
import com.gitee.qdbp.tools.excel.model.RowInfo;

/**
 * Excel处理类
 *
 * @author zhaohuihua
 * @version 170929
 */
public class ExcelHelper {

    private static final Logger log = LoggerFactory.getLogger(ExcelHelper.class);

    private static final Pattern IGNORE_SHEET_NAME = Pattern.compile("sheet\\d*", Pattern.CASE_INSENSITIVE);

    public static void parse(Sheet sheet, XMetadata metadata, SheetParseCallback cb) {

        String sheetName = sheet.getSheetName();
        List<FieldInfo> fieldInfos = metadata.getFieldInfos();
        if (fieldInfos == null && metadata.getFieldRows() != null) {
            fieldInfos = MetadataTools.parseFieldInfoByRows(sheet, metadata.getFieldRows());
            if (fieldInfos.isEmpty()) {
                log.warn("Sheet[{}], Field list is empty, fieldRows={}", sheetName, metadata.getFieldRows());
            }
        }

        // 读取标题信息, 生成列数据
        List<ColumnInfo> columnInfos = MetadataTools.parseHeaders(sheet, metadata.getHeaderRows(), fieldInfos);

        int skipRows = metadata.getSkipRows();
        int totalSize = ExcelTools.getTotalRowsOfSheet(sheet);
        for (int i = skipRows; i <= totalSize; i++) {
            if (metadata.isHeaderRow(i)) {
                // 是表头则跳过
                // 导入时每个excel的页脚位置有可能不一样, 所以导入不能指定页脚行, 只能设置skip.row.when
                continue;
            }
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if (metadata.isSkipRow(row)) {
                log.trace("Sheet[{}], skip row, row={}", sheetName, i);
                continue;
            }
            try {
                parse(sheetName, row, i + 1, columnInfos, metadata, cb);
            } catch (ServiceException e) {
                cb.addFailed(sheetName, i + 1, e);
                if (e.getCause() != null) {
                    String error = e.getMessage();
                    String cause = e.getCause().getMessage();
                    log.error("Sheet[{}], excel parse error, {}, {}", sheetName, error, cause, e);
                }
            } catch (NumberFormatException e) {
                cb.addFailed(sheetName, i + 1, ResultCode.PARAMETER_FORMAT_ERROR);
            } catch (IllegalArgumentException e) {
                cb.addFailed(sheetName, i + 1, ResultCode.PARAMETER_FORMAT_ERROR);
            } catch (Throwable e) {
                cb.addFailed(sheetName, i + 1, ResultCode.SERVER_INNER_ERROR);
                log.error("Sheet[{}], excel parse error.", sheetName, e);
            }
        }
    }

    // 1.先读取整行数据, 2.将数据值填入cellInfo.value, 3.调用规则将值转换后填入data
    private static void parse(String sheetName, Row row, int rowIndex, List<ColumnInfo> columnInfos, XMetadata metadata,
            SheetParseCallback cb) throws ServiceException {
        // 读取Excel整行数据
        Map<String, Object> map = new HashMap<>();
        int size = Math.min(columnInfos.size(), ExcelTools.getTotalColumnsOfRow(row));
        for (int i = 0; i < size; i++) {
            FieldInfo fieldInfo = columnInfos.get(i);
            if (fieldInfo == null || fieldInfo.getColumn() == null) {
                continue;
            }
            Cell cell = row.getCell(i);

            Object value = cb.getCellValue(cell, fieldInfo);

            if (VerifyTools.isNotBlank(value)) {
                map.put(fieldInfo.getField(), value);
            }
        }

        // 如果整行为空, 直接返回
        if (map.isEmpty()) {
            return;
        }

        cb.addTotal(1); // 总行数加1

        // Sheet名称填充至指定字段
        if (VerifyTools.isNotBlank(metadata.getSheetNameFillTo())) {
            if (!IGNORE_SHEET_NAME.matcher(sheetName).matches()) {
                map.put(metadata.getSheetNameFillTo(), sheetName);
            }
        }

        // 生成单元格信息
        Map<String, Object> data = new HashMap<>();
        List<CellInfo> cellInfos = newCellInfos(columnInfos, metadata);
        for (CellInfo info : cellInfos) {
            if (info == null) {
                continue;
            }
            Object original = map.get(info.getField());
            // 填充行号和value
            fillCellInfo(cellInfos, rowIndex, map);
            try {
                // 调用转换规则
                cb.convert(info, data);
            } catch (ServiceException e) {
                // 拼接[列序号]字段名(单元格的值), 如[D:年龄]100Kg
                String title = '[' + info.getTitle() + ']';
                if (info.getColumn() != null) {
                    title = '[' + ExcelTools.columnIndexToName(info.getColumn()) + ':' + info.getTitle() + ']';
                }
                if (VerifyTools.isNotBlank(original)) {
                    // 不知道怎么取Excel单元格的原始文本, 因此日期,时间就不好提示了
                    if (original instanceof String || original instanceof Boolean || original instanceof Number) {
                        title += original.toString();
                    }
                }
                cb.addFailed(sheetName, rowIndex, title, original, e);
                return;
            }
            { // 检查必填字段
                Object value = info.getValue();
                if (info.isRequired() && VerifyTools.isBlank(value)) {
                    cb.addFailed(sheetName, rowIndex, info.getTitle(), value, ResultCode.PARAMETER_IS_REQUIRED);
                    return;
                }
            }
        }

        // 字段复制合并
        if (VerifyTools.isNotBlank(metadata.getCopyConcatFields())) {
            ExcelTools.copyConcat(data, metadata.getCopyConcatFields());
        }

        // 回调具体的业务处理方法
        RowInfo rowInfo = new RowInfo(sheetName, rowIndex);
        rowInfo.setCells(cellInfos);
        rowInfo.setMetadata(metadata);
        cb.callback(data, rowInfo);
    }

    public static void fill(List<?> list, Sheet sheet, XMetadata metadata, SheetFillCallback cb) {

        String sheetName = sheet.getSheetName();
        List<FieldInfo> fieldInfos = metadata.getFieldInfos();
        if (fieldInfos == null && metadata.getFieldRows() != null) {
            fieldInfos = MetadataTools.parseFieldInfoByRows(sheet, metadata.getFieldRows());
            if (fieldInfos.isEmpty()) {
                log.warn("Sheet[{}], Field list is empty, fieldRows={}", sheetName, metadata.getFieldRows());
            }
        }

        // 读取标题信息, 生成单元格数据
        List<ColumnInfo> columnInfos = MetadataTools.parseHeaders(sheet, metadata.getHeaderRows(), fieldInfos);

        int begin = metadata.getSkipRows();
        // 从第一行复制样式
        Row first = sheet.getRow(begin);

        // 如果配置了页脚, 需要插入行将页脚移到数据行之后
        IndexRangeCondition footerRows = metadata.getFooterRows();
        if (footerRows != null) {
            if (footerRows.getMin() < begin + 1) {
                // 页脚不能小于开始行+1, 也就是说表头与页脚之间最少要有一行
                String m = "Sheet[{}], Footer must be greater than begin row + 1. FooterMinRow={}, BeginRow={}.";
                log.warn(m, sheetName, footerRows.getMin() + 1, begin + 1);
                footerRows = null;
            } else {
                int footerMin = Math.max(footerRows.getMin(), begin + 1);
                // 计算需要插入多少行, = 需要多少行 - (footer至begin之间已有多少行)
                int insert = list.size() - (footerMin - begin);
                if (insert > 0) {
                    // shiftRows会自动处理公式中引用的单元格
                    // 将页脚往下移
                    sheet.shiftRows(begin + 1, footerRows.getMax(), insert, true, false);
                }
            }
        }
        // 生成单元格信息, 重复利用, 每次循环更新rowIndex和value
        List<CellInfo> cellInfos = newCellInfos(columnInfos, metadata);
        for (int i = 0; i < list.size(); i++) {
            int rowIndex = i + begin + 1;
            Row row = getOrCreateRow(sheet, i + begin);

            Map<String, Object> map = cb.toMap(list.get(i));
            // 填充rowIndex和value
            fillCellInfo(cellInfos, rowIndex, map);

            RowInfo rowInfo;
            { // RowInfo
                rowInfo = new RowInfo(sheet.getSheetName(), rowIndex);
                rowInfo.setCells(cellInfos);
                rowInfo.setMetadata(metadata);
            }

            if (!cb.onRowStart(row, rowInfo, map)) {
                continue;
            }

            // 从第一行复制样式, i > 0 是因为第一行不用复制
            if (i > 0 && first != null) {
                ExcelTools.copyRow(first, row, true);
            }

            Map<String, Object> data = new HashMap<>();
            for (CellInfo info : cellInfos) {
                if (info == null || info.getColumn() == null) {
                    continue;
                }
                Cell cell = row.getCell(info.getColumn() - 1, Row.CREATE_NULL_AS_BLANK);
                // 设置值
                setValue(cell, info, data, cb);
            }

            if (!cb.onRowFinished(row, rowInfo, data)) {
                break;
            }
        }

    }

    private static Row getOrCreateRow(Sheet sheet, int index) {
        Row row = sheet.getRow(index);
        return row != null ? row : sheet.createRow(index);
    }

    private static void setValue(Cell cell, CellInfo cellInfo, Map<String, Object> data, SheetFillCallback cb) {

        try {
            // 调用转换规则
            cb.convert(cellInfo, data);
        } catch (ServiceException ignore) {
            // 转换失败则输出原内容
        }

        String field = cellInfo.getField();
        Object value = data.get(field);
        cb.setCellValue(cell, value, data);
    }

    private static List<CellInfo> newCellInfos(List<ColumnInfo> columnInfos, XMetadata metadata) {
        List<CellInfo> cellInfos = new ArrayList<>();
        for (ColumnInfo columnInfo : columnInfos) {
            if (columnInfo == null) {
                cellInfos.add(null);
            } else {
                CellInfo info = columnInfo.to(CellInfo.class);
                info.setCells(cellInfos);
                info.setMetadata(metadata);
                info.setRules(metadata.getRule(columnInfo.getField()));
                cellInfos.add(info);
            }
        }
        return cellInfos;
    }

    private static void fillCellInfo(List<CellInfo> cellInfos, int rowIndex, Map<String, Object> map) {
        for (CellInfo cellInfo : cellInfos) {
            if (cellInfo != null) {
                cellInfo.setRow(rowIndex);
                cellInfo.setValue(map.get(cellInfo.getField()));
            }
        }
    }
}
