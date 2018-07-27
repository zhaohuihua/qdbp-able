package com.gitee.qdbp.tools.excel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.IBatchResult;
import com.gitee.qdbp.able.result.IResultMessage;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.excel.model.FailedInfo;
import com.gitee.qdbp.tools.excel.model.RowInfo;
import com.gitee.qdbp.tools.excel.rule.ConvertRule;
import com.gitee.qdbp.tools.excel.rule.PresetRule;

/**
 * 导入回调函数
 *
 * @author zhaohuihua
 * @version 160302
 */
public abstract class ImportCallback implements IBatchResult, ConvertRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 记录总数 **/
    private Integer total = 0;

    /** 失败列表 **/
    private List<Failed> failed = new ArrayList<>();

    /** 整行失败 **/
    public void addFailed(String sheetName, int row, IResultMessage result) {
        failed.add(new FailedInfo(sheetName, row, result));
    }

    /** 具体某一列失败 **/
    public void addFailed(String sheetName, int row, String field, IResultMessage result) {
        failed.add(new FailedInfo(sheetName, row, field, result));
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

    /** 初始化处理逻辑 **/
    public void init(Workbook workbook, XMetadata metadata) throws ServiceException {
    }

    /** 收尾处理逻辑 **/
    public void finish(Workbook workbook, XMetadata metadata) throws ServiceException {
    }

    /** 开始解析Sheet之前的处理逻辑, 返回false跳过该Sheet **/
    public boolean onSheetStart(Sheet sheet, XMetadata metadata) {
        return true;
    }

    /** 解析Sheet完成之后的处理逻辑, 返回false跳过后面的所有Sheet **/
    public boolean onSheetFinished(Sheet sheet, XMetadata metadata) {
        return true;
    }

    /** 具体的业务处理逻辑, 一般是插入数据库之类的持久化操作 **/
    public abstract void callback(Map<String, Object> map, RowInfo row) throws ServiceException;

    /** 单元格字段转换 **/
    @Override
    public void convert(Map<String, Object> map, CellInfo cell) throws ServiceException {

        String key = cell.getField();
        PresetRule rule = cell.getMetadata().getRule(key);
        if (rule == null) {
            // 没有预置的转换规则
            map.put(key, convert(map, key, cell.getValue()));
        } else {
            // 调用转换规则
            rule.imports(map, cell);
        }

    }

    /** 单元格字段转换, 如果转换失败则直接抛异常 **/
    protected Object convert(Map<String, Object> map, String key, Object value) throws ServiceException {
        return value;
    }
}
