package com.gitee.zhaohuihua.tools.excel.model;

import java.io.Serializable;
import java.util.Map;
import com.gitee.zhaohuihua.tools.excel.XMetadata;

/**
 * 行信息
 *
 * @author zhaohuihua
 * @version 160302
 */
public class RowInfo implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** Sheet页签的名称 **/
    private final String sheetName;

    /** 第几行, 从1开始 **/
    private final Integer row;

    /** 所有单元格信息 **/
    private Map<String, CellInfo> cells;

    /** 配置元数据 **/
    private XMetadata metadata;

    public RowInfo(String sheetName, Integer row) {
        this.sheetName = sheetName;
        this.row = row;
    }

    /** 获取Sheet页签的名称 **/
    public String getSheetName() {
        return sheetName;
    }

    /** 获取第几行, 从1开始 **/
    public Integer getRow() {
        return row;
    }

    /** 获取所有单元格信息 **/
    public Map<String, CellInfo> getCells() {
        return cells;
    }

    /** 设置所有单元格信息 **/
    public void setCells(Map<String, CellInfo> cells) {
        this.cells = cells;
    }

    /** 获取配置元数据 **/
    public XMetadata getMetadata() {
        return metadata;
    }

    /** 设置配置元数据 **/
    public void setMetadata(XMetadata metadata) {
        this.metadata = metadata;
    }

}
