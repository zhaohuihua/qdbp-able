package com.gitee.zhaohuihua.tools.excel.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitee.zhaohuihua.core.exception.ServiceException;
import com.gitee.zhaohuihua.core.result.ResultCode;
import com.gitee.zhaohuihua.tools.excel.ExportCallback;
import com.gitee.zhaohuihua.tools.excel.ImportCallback;
import com.gitee.zhaohuihua.tools.excel.XMetadata;
import com.gitee.zhaohuihua.tools.excel.model.CellInfo;
import com.gitee.zhaohuihua.tools.excel.model.ColumnInfo;
import com.gitee.zhaohuihua.tools.excel.model.RowInfo;
import com.gitee.zhaohuihua.tools.excel.parse.HeaderParser;
import com.gitee.zhaohuihua.tools.excel.parse.IndexRangeConfig;
import com.gitee.zhaohuihua.tools.utils.VerifyTools;

/**
 * Excel处理类
 *
 * @author zhaohuihua
 * @version 170929
 */
public class ExcelHelper {

    private static final Logger log = LoggerFactory.getLogger(ExcelHelper.class);

    private static final Pattern IGNORE_SHEET_NAME = Pattern.compile("sheet\\d*", Pattern.CASE_INSENSITIVE);

    public static void parse(Sheet sheet, XMetadata metadata, ImportCallback cb) {

        int totalSize = sheet.getPhysicalNumberOfRows();

        // 读取标题信息, 生成单元格数据
        Map<String, CellInfo> cells = new HeaderParser(metadata).parseHeaders(sheet);

        String sheetName = sheet.getSheetName();
        int skipRows = metadata.getSkipRows();
        for (int i = skipRows; i <= totalSize; i++) {
            if (metadata.isEnableHeader(i)) {
                // 是表头则跳过
                // 导入时每个excel的页脚位置有可能不一样, 所以导入不能指定页脚行, 只能在导入之前把页脚删掉
                continue;
            }
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            try {
                parse(sheetName, row, i, cells, metadata, cb);
            } catch (ServiceException e) {
                cb.addFailed(sheetName, i + 1, e);
                if (e.getCause() != null) {
                    log.error("excel parse error, " + e.getMessage() + ", " + e.getCause().getMessage());
                }
            } catch (NumberFormatException e) {
                cb.addFailed(sheetName, i + 1, ResultCode.PARAMETER_FORMAT_ERROR);
            } catch (IllegalArgumentException e) {
                cb.addFailed(sheetName, i + 1, ResultCode.PARAMETER_FORMAT_ERROR);
            } catch (Throwable e) {
                cb.addFailed(sheetName, i + 1, ResultCode.SERVER_INNER_ERROR);
                log.error("excel parse error.", e);
            }
        }
    }

    private static void parse(String sheetName, Row row, int index, Map<String, CellInfo> cells, XMetadata metadata,
            ImportCallback cb) throws ServiceException {
        Map<String, Object> map = new HashMap<>();
        List<ColumnInfo> columns = metadata.getColumns();
        for (int i = 0; i < columns.size() && i < row.getLastCellNum(); i++) {
            ColumnInfo column = columns.get(i);
            if (column == null) {
                continue;
            }
            Cell cell = row.getCell(i);

            Object object = ExcelUtils.getCellValue(cell);

            if (object instanceof String && VerifyTools.isNotBlank(object)) {
                object = object.toString().trim();
            }

            if (VerifyTools.isNotBlank(object)) {
                map.put(column.getField(), object);
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

        for (ColumnInfo column : columns) {
            if (column == null) {
                continue;
            }

            String field = column.getField();

            Object object = map.get(field);
            CellInfo info = cells.get(field);
            info.setRow(index + 1);
            info.setValue(object);

            // 检查必填字段
            if (info.isRequired() && VerifyTools.isBlank(object)) {
                cb.addFailed(sheetName, index + 1, info.getHeader(), ResultCode.PARAMETER_IS_REQUIRED);
                return;
            }

            try {
                // 调用转换规则
                cb.convert(map, info);
            } catch (ServiceException e) {
                // 拼接字段名和单元格的值, 如年龄(100)
                String header = info.getHeader();
                if (VerifyTools.isNotBlank(object)) {
                    // 不知道怎么取Excel单元格的原始文本, 因此日期,时间就不好提示了
                    if (object instanceof String || object instanceof Boolean || object instanceof Number) {
                        header = info.getHeader() + "(" + object.toString() + ")";
                    }
                }
                cb.addFailed(sheetName, index + 1, header, e);
                return;
            }
        }

        // 回调具体的业务处理方法
        RowInfo info = new RowInfo(sheetName, index + 1);
        info.setCells(cells);
        info.setMetadata(metadata);
        cb.callback(map, info);
    }

    public static void export(List<?> data, Sheet sheet, XMetadata metadata, ExportCallback cb) {

        // 读取标题信息, 生成单元格数据
        Map<String, CellInfo> cells = new HeaderParser(metadata).parseHeaders(sheet);

        cb.onSheetStart(sheet, metadata, data);

        int begin = metadata.getSkipRows();
        // 从第一行复制样式
        Row first = sheet.getRow(begin);

        // 如果配置了页脚, 需要插入行将页脚移到数据行之后
        IndexRangeConfig footerRows = metadata.getFooterRows();
        if (footerRows != null) {
            if (footerRows.getMin() < begin + 1) {
                // 页脚不能小于开始行+1, 也就是说表头与页脚之间最少要有一行
                String m = "Footer must be greater than begin row. FooterMinRow={}, BeginRow={}.";
                log.warn(m, footerRows.getMin() + 1, begin + 1);
                footerRows = null;
            }

            int footerMin = Math.max(footerRows.getMin(), begin + 1);
            // 计算需要插入多少行, = 需要多少行 - (footer至begin之间已有多少行)
            int insert = data.size() - (footerMin - begin);
            if (insert > 0) {
                // shiftRows会自动处理公式中引用的单元格
                // 将页脚往下移
                sheet.shiftRows(begin + 1, footerRows.getMax(), insert, true, false);
            }
        }
        for (int i = 0; i < data.size(); i++) {
            int index = i + begin;
            Row row = getOrCreateRow(sheet, index);
            // 从第一行复制行高, i > 0 是因为第一行不用复制
            if (i > 0 && first != null) {
                ExcelUtils.copyRow(first, row, true);
            }

            RowInfo info;
            { // RowInfo
                info = new RowInfo(sheet.getSheetName(), index + 1);
                info.setCells(cells);
                info.setMetadata(metadata);
            }

            Map<String, Object> json = cb.convert(data.get(i));
            cb.onRowStart(row, info, json);

            List<ColumnInfo> columns = metadata.getColumns();
            int cellCount = Math.max(columns.size(), row.getPhysicalNumberOfCells());
            for (int c = 0; c < cellCount; c++) {
                Cell cell = row.getCell(c, Row.CREATE_NULL_AS_BLANK);
                // 设置值
                if (c < columns.size()) {
                    setValue(cell, columns.get(c), index, cells, json, cb);
                }
            }

            cb.onRowFinished(row, info, json);
        }

        cb.onSheetFinished(sheet, metadata, data);
    }

    private static Row getOrCreateRow(Sheet sheet, int index) {
        if (index < sheet.getPhysicalNumberOfRows()) {
            Row row = sheet.getRow(index);
            return row != null ? row : sheet.createRow(index);
        } else {
            return sheet.createRow(index);
        }
    }

    private static void setValue(Cell cell, ColumnInfo column, int index, Map<String, CellInfo> cells,
            Map<String, Object> json, ExportCallback cb) {
        if (column == null) {
            return;
        }

        String field = column.getField();
        Object object = json.get(field);
        CellInfo info = cells.get(field);
        info.setRow(index + 1);
        info.setValue(object);
        try {
            // 调用转换规则
            cb.convert(json, cells.get(field));
        } catch (ServiceException ignore) {
            // 转换失败则输出原内容
        }

        Object value = json.get(field);
        if (value == null) {
            cell.setCellType(Cell.CELL_TYPE_BLANK);
        } else {
            ExcelUtils.setCellValue(cell, value);
        }
    }
}
