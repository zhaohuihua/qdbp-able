package com.gitee.zhaohuihua.tools.excel.model;

import java.io.Serializable;
import java.util.Map;
import com.gitee.zhaohuihua.tools.excel.XMetadata;

/**
 * 单元格信息
 *
 * @author zhaohuihua
 * @version 160302
 */
public class CellInfo implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 字段名 **/
    private final String field;

    /** 是不是必填字段 **/
    private final boolean required;

    /** 标题文本 **/
    private final String header;

    /** 第几列, 从1开始 **/
    private final Integer column;

    /** 第几行, 从1开始 **/
    private Integer row;

    /** 内容 **/
    private Object value;

    /** 所有单元格信息 **/
    private Map<String, CellInfo> cells;

    /** 配置元数据 **/
    private XMetadata metadata;

    public CellInfo(Integer column, String field, String header, boolean required) {
        this.column = column;
        this.field = field;
        this.header = header;
        this.required = required;
    }

    /** 获取字段名 **/
    public String getField() {
        return field;
    }

    /** 是不是必填字段 **/
    public boolean isRequired() {
        return required;
    }

    /** 获取标题文本 **/
    public String getHeader() {
        return header;
    }

    /** 获取第几行, 从1开始 **/
    public Integer getRow() {
        return row;
    }

    /** 设置第几行, 从1开始 **/
    public void setRow(Integer row) {
        this.row = row;
    }

    /** 获取第几列, 从1开始 **/
    public Integer getColumn() {
        return column;
    }

    /** 获取内容 **/
    public Object getValue() {
        return value;
    }

    /** 设置内容 **/
    public void setValue(Object value) {
        this.value = value;
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
