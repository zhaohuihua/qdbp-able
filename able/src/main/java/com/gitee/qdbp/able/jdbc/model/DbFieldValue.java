package com.gitee.qdbp.able.jdbc.model;

import java.io.Serializable;

/**
 * 字段值<br>
 * 自定义对象保存到数据库时需要转换(转换目标一般是Boolean/Character/Date/Number/String之一)<br>
 * 先定义一个转换类: XxxToDbValueConverter implements Converter或GenericConverter<br>
 * 再注入spring的ConversionService之中, SpringVarToDbValueConverter会判断XxxEntity能否转换为DbFieldValue
 *
 * @author zhaohuihua
 * @version 200123
 */
public class DbFieldValue implements Serializable {

    /** SerialVersionUID **/
    private static final long serialVersionUID = 1L;
    private Object fieldValue;

    public DbFieldValue() {
    }

    public DbFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}
