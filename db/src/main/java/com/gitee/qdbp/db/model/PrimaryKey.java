package com.gitee.qdbp.db.model;

import com.joyintech.constant.CommConstant.UD_KEY_GEN_TYPE;

/**
 * 主键信息
 *
 * @author zhaohuihua
 * @version 181220
 */
public class PrimaryKey extends ColumnInfo {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** 主键生成方式 **/
    private UD_KEY_GEN_TYPE type;

    /** 主键生成方式 **/
    public UD_KEY_GEN_TYPE getType() {
        return type;
    }

    /** 主键生成方式 **/
    public void setType(UD_KEY_GEN_TYPE type) {
        this.type = type;
    }

    /** {@inheritDoc} **/
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('{');
        buffer.append("field:").append(this.getFieldName()).append(',');
        buffer.append("column:").append(this.getColumnName()).append(',');
        buffer.append("type:").append(this.type.name());
        buffer.append('}');
        return buffer.toString();
    }
}
