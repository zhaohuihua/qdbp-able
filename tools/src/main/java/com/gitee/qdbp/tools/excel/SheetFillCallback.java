package com.gitee.qdbp.tools.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.RowInfo;
import com.gitee.qdbp.tools.excel.rule.CellRule;
import com.gitee.qdbp.tools.excel.rule.IgnoreIllegalValue;
import com.gitee.qdbp.tools.excel.utils.ExcelTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * Sheet填充回调函数
 *
 * @author zhaohuihua
 * @version 170223
 */
public class SheetFillCallback implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;
    /** 日志对象 **/
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /** 开始填充Row之前的处理逻辑, 返回false跳过该Row **/
    public boolean onRowStart(Row row, RowInfo rowInfo, Map<String, Object> json) {
        return true;
    }

    /** 填充Row完成之后的处理逻辑, 返回false跳过后面的所有Row **/
    public boolean onRowFinished(Row row, RowInfo rowInfo, Map<String, Object> data) {
        return true;
    }

    /** 写入单元格内容 **/
    public void setCellValue(Cell cell, Object value, Map<String, Object> data) {
        ExcelTools.setCellValue(cell, value);
    }

    /** JavaBean转换为Map **/
    public Map<String, Object> toMap(Object value) {
        return (JSONObject) JSON.toJSON(value);
    }

    /** 单元格字段转换, data.put(cellInfo.getField(), cellInfo.getValue()); **/
    public void convert(CellInfo cellInfo, Map<String, Object> data) throws ServiceException {

        List<CellRule> rules = cellInfo.getRules();
        if (rules == null || rules.isEmpty()) {
            // 没有预置的转换规则
            data.put(cellInfo.getField(), convert(cellInfo.getField(), cellInfo.getValue(), data));
        } else {
            // 特殊处理: 判断有没有IgnoreIllegalValue规则
            boolean ignoreIllegalValue = false;
            List<CellRule> actions = new ArrayList<>();
            for (CellRule rule : rules) {
                if (rule instanceof IgnoreIllegalValue) {
                    ignoreIllegalValue = true;
                } else {
                    actions.add(rule);
                }
            }
            // 调用转换规则
            for (CellRule rule : actions) {
                try {
                    Map<String, Object> values = rule.exports(cellInfo);
                    data.put(cellInfo.getField(), cellInfo.getValue());
                    if (VerifyTools.isNotBlank(values)) {
                        data.putAll(values);
                    }
                } catch (Exception e) {
                    if (!ignoreIllegalValue) {
                        String msg = ExcelTools.newConvertErrorMessage(cellInfo, rule);
                        Exception se = new IllegalArgumentException(msg, e);
                        throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, se);
                    } else {
                        cellInfo.setValue(null);
                        if (log.isWarnEnabled()) {
                            String msg = ExcelTools.newConvertErrorMessage(cellInfo, rule);
                            log.warn(msg + ", " + e.toString());
                        }
                    }
                }
            }
        }
    }

    /** 单元格字段转换 **/
    protected Object convert(String key, Object value, Map<String, Object> map) throws ServiceException {
        return value;
    }
}
