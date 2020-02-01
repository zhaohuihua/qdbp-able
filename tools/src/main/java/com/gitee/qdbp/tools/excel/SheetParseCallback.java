package com.gitee.qdbp.tools.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.IBatchResult;
import com.gitee.qdbp.able.result.IResultMessage;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.FailedInfo;
import com.gitee.qdbp.tools.excel.model.FieldInfo;
import com.gitee.qdbp.tools.excel.model.RowInfo;
import com.gitee.qdbp.tools.excel.rule.CellRule;
import com.gitee.qdbp.tools.excel.rule.IgnoreIllegalValue;
import com.gitee.qdbp.tools.excel.utils.ExcelTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 解析回调函数
 *
 * @author zhaohuihua
 * @version 160302
 */
public abstract class SheetParseCallback implements IBatchResult, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 日志对象 **/
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /** 记录总数 **/
    private Integer total = 0;
    /** 失败列表 **/
    private List<Failed> failed = new ArrayList<>();

    /** 整行失败 **/
    public void addFailed(String sheetName, int row, IResultMessage result) {
        failed.add(new FailedInfo(sheetName, row, result));
    }

    /** 具体某一列失败(注意:字段名和字段值都填在field这里, value的值不用于前端显示) **/
    public void addFailed(String sheetName, int row, String field, Object value, IResultMessage result) {
        failed.add(new FailedInfo(sheetName, row, field, value, result));
    }

    /** 失败列表 **/
    @Override
    public List<Failed> getFailed() {
        return failed;
    }

    /** 增加记录总数 **/
    public void addTotal(int number) {
        total += number;
    }

    /** 设置记录总数 **/
    public void setTotal(int total) {
        this.total = total;
    }

    /** 获取记录总数 **/
    @Override
    public Integer getTotal() {
        return total;
    }

    /** 具体的业务处理逻辑, 一般是插入数据库之类的持久化操作 **/
    public abstract void callback(Map<String, Object> map, RowInfo row) throws ServiceException;

    /** 读取单元格内容 **/
    public Object getCellValue(Cell cell, FieldInfo fieldInfo) {
        return ExcelTools.getCellValue(cell);
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
                    Map<String, Object> values = rule.imports(cellInfo);
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
                        if (log.isWarnEnabled()) {
                            String msg = ExcelTools.newConvertErrorMessage(cellInfo, rule);
                            log.warn(msg + ", " + e.toString());
                        }
                        cellInfo.setValue(null);
                    }
                }
            }
        }

    }

    /** 单元格字段转换, 如果转换失败则直接抛异常 **/
    protected Object convert(String key, Object value, Map<String, Object> map) throws ServiceException {
        return value;
    }
}
