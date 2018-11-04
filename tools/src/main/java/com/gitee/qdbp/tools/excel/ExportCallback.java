package com.gitee.qdbp.tools.excel;

import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.FieldInfo;
import com.gitee.qdbp.tools.excel.model.RowInfo;
import com.gitee.qdbp.tools.excel.rule.CellRule;
import com.gitee.qdbp.tools.excel.utils.ExcelTools;

/**
 * 导出回调函数
 *
 * @author zhaohuihua
 * @version 170223
 */
public class ExportCallback {

    /** 初始化处理逻辑 **/
    public void init(Workbook workbook, XMetadata metadata) throws ServiceException {
    }

    /** 收尾处理逻辑 **/
    public void finish(Workbook workbook, XMetadata metadata) throws ServiceException {
    }

    public void onRowStart(Row row, RowInfo rowInfo, Map<String, Object> json) {
    }

    public void onRowFinished(Row row, RowInfo rowInfo, Map<String, Object> data) {
    }

    public void onSheetStart(Sheet sheet, XMetadata metadata, List<?> data) {
    }

    public void onSheetFinished(Sheet sheet, XMetadata metadata, List<?> data) {
    }

    /** 写入单元格内容 **/
    public void setCellValue(Cell cell, Object value, FieldInfo fieldInfo) {
        ExcelTools.setCellValue(cell, value);
    }

    public Map<String, Object> convert(Object value) {
        return (JSONObject) JSON.toJSON(value);
    }

    /** 单元格字段转换 **/
    public void convert(Map<String, Object> map, CellInfo cellInfo) throws ServiceException {

        String key = cellInfo.getField();
        CellRule rule = cellInfo.getMetadata().getRule(key);
        if (rule == null) {
            // 没有预置的转换规则
            map.put(key, convert(map, key, cellInfo.getValue()));
        } else {
            // 调用转换规则
            rule.exports(map, cellInfo);
        }

    }

    /** 单元格字段转换 **/
    protected Object convert(Map<String, Object> map, String key, Object value) throws ServiceException {
        return value;
    }
}
