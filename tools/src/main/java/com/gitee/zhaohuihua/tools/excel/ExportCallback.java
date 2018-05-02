package com.gitee.zhaohuihua.tools.excel;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.zhaohuihua.core.exception.ServiceException;
import com.gitee.zhaohuihua.tools.excel.model.CellInfo;
import com.gitee.zhaohuihua.tools.excel.model.RowInfo;
import com.gitee.zhaohuihua.tools.excel.rule.PresetRule;

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

    public void onRowStart(Row row, RowInfo info, Map<String, Object> json) {
    }

    public void onRowFinished(Row row, RowInfo info, Map<String, Object> data) {
    }

    public void onSheetStart(Sheet sheet, XMetadata metadata, List<?> data) {
    }

    public void onSheetFinished(Sheet sheet, XMetadata metadata, List<?> data) {
    }

    public Map<String, Object> convert(Object value) {
        return (JSONObject) JSON.toJSON(value);
    }

    /** 单元格字段转换 **/
    public void convert(Map<String, Object> map, CellInfo cell) throws ServiceException {

        String key = cell.getField();
        PresetRule rule = cell.getMetadata().getRule(key);
        if (rule == null) {
            // 没有预置的转换规则
            map.put(key, convert(map, key, cell.getValue()));
        } else {
            // 调用转换规则
            rule.exports(map, cell);
        }

    }

    /** 单元格字段转换 **/
    protected Object convert(Map<String, Object> map, String key, Object value) throws ServiceException {
        return value;
    }
}