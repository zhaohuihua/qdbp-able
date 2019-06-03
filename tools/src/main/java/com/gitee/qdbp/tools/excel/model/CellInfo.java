package com.gitee.qdbp.tools.excel.model;

import java.util.List;
import com.gitee.qdbp.tools.excel.XMetadata;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 单元格信息
 *
 * @author zhaohuihua
 * @version 160302
 */
public class CellInfo extends ColumnInfo {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 行序号, 从1开始 **/
    private Integer row;

    /** 内容 **/
    private Object value;

    /** 所有单元格信息 **/
    private List<CellInfo> cells;

    /** 配置元数据 **/
    private XMetadata metadata;

    public CellInfo() {
    }

    public CellInfo(Integer column, String field, String title, boolean required) {
        super(column, field, title, required);
    }

    /** 获取行序号, 从1开始 **/
    public Integer getRow() {
        return row;
    }

    /** 设置行序号, 从1开始 **/
    public void setRow(Integer row) {
        this.row = row;
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
    public List<CellInfo> getCells() {
        return cells;
    }

    /** 设置所有单元格信息 **/
    public void setCells(List<CellInfo> cells) {
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

    /**
     * 将当前对象转换为子类对象
     *
     * @param clazz 目标类型
     * @return 目标对象
     */
    public <T extends FieldInfo> T to(Class<T> clazz) {
        T instance = super.to(clazz);

        if (instance instanceof CellInfo) {
            CellInfo real = (CellInfo) instance;
            real.setRow(this.getRow()); // 行列号
            real.setValue(this.getValue()); // 内容
            real.setMetadata(this.getMetadata()); // 配置元数据
            real.setCells(this.getCells()); // 所有单元格信息
        }
        return instance;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (VerifyTools.isNotBlank(this.getTitle())) {
            buffer.append(this.getTitle()).append(':');
        }
        if (VerifyTools.isBlank(this.getField())) {
            buffer.append("{UNKNOWN}");
        } else {
            buffer.append(this.getField());
        }

        buffer.append('[');
        if (this.getRow() == null) {
            buffer.append('?');
        } else {
            buffer.append(this.getRow());
        }
        buffer.append(',');
        if (this.getColumn() == null) {
            buffer.append('?');
        } else {
            buffer.append(this.getColumn());
        }
        buffer.append("]");

        if (this.isRequired()) {
            buffer.append("(*)");
        }
        if (VerifyTools.isBlank(this.getValue())) {
            buffer.append(':').append("{NULL}");
        } else {
            buffer.append(':').append(this.getValue());
        }
        return buffer.toString();
    }

}
