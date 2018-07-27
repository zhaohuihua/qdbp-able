package com.gitee.zhaohuihua.tools.excel.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import com.gitee.zhaohuihua.tools.excel.XMetadata;
import com.gitee.zhaohuihua.tools.excel.model.CellInfo;
import com.gitee.zhaohuihua.tools.excel.model.ColumnInfo;
import com.gitee.zhaohuihua.tools.excel.utils.ExcelUtils;

/**
 * 表头解析器
 *
 * @author zhaohuihua
 * @version 160907
 */
public class HeaderParser {
    
    private XMetadata metadata;
    
    public HeaderParser(XMetadata metadata) {
        this.metadata = metadata;
    }

    public Map<String, CellInfo> parseHeaders(Sheet sheet) {
        // 生成单元格信息
        Map<String, CellInfo> cells = new HashMap<>();
        // 读取标题文本
        List<ColumnInfo> columns = metadata.getColumns();
        for (ColumnInfo column : columns) {
            if (column == null) {
                continue;
            }
            CellInfo cell = parseHeader(sheet, column);
            cell.setCells(cells);
            cell.setMetadata(metadata);
            cells.put(column.getField(), cell);
        }
        return cells;
    }

    private CellInfo parseHeader(Sheet sheet, ColumnInfo column) {
        IndexRangeConfig headerRows = metadata.getHeaderRows();

        int c = column.getColumn();
        String field = column.getField();
        String text = "第" + (c + 1) + "列";
        boolean required = column.isRequired();
        if (headerRows != null) {
            for (int r = headerRows.getMin(); r <= headerRows.getMax(); r++) {
                Row header = sheet.getRow(r);
                if (header == null) {
                    continue;
                }
                Object value = ExcelUtils.getCellValue(header.getCell(c));
                String string = value == null ? null : value.toString();

                Required result = Required.of(string);
                if (result != null) {
                    text = result.getName();
                    required = column.isRequired() || result.isRequired();
                }
            }
        }
        return new CellInfo(c + 1, field, text, required);
    }

}
