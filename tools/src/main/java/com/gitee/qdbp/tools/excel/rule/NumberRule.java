package com.gitee.qdbp.tools.excel.rule;

import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 数字规则
 *
 * @author zhaohuihua
 * @version 181104
 */
public class NumberRule extends BaseRule {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private String type;

    public NumberRule(String type) {
        this(null, type);
    }

    public NumberRule(CellRule parent, String type) {
        super(parent);
        this.type = type;
    }

    @Override
    public void doImports(CellInfo cellInfo)
            throws ServiceException {
        if (VerifyTools.isBlank(cellInfo.getValue())) {
            cellInfo.setValue(null);
        } else {
            cellInfo.setValue(toNumber(cellInfo.getValue(), type));
        }
    }

    @Override
    public void doExports(CellInfo cellInfo)
            throws ServiceException {
        if (VerifyTools.isBlank(cellInfo.getValue())) {
            cellInfo.setValue(null);
        } else {
            cellInfo.setValue(toNumber(cellInfo.getValue(), type));
        }
    }

    private static Number toNumber(Object value, String type) {
        if (type == null) {
            type = "double";
        }
        if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("integer")) {
            return TypeUtils.castToInt(value);
        } else if (type.equalsIgnoreCase("float")) {
            return TypeUtils.castToFloat(value);
        } else if (type.equalsIgnoreCase("long")) {
            return TypeUtils.castToLong(value);
        } else if (type.equalsIgnoreCase("short")) {
            return TypeUtils.castToShort(value);
        } else if (type.equalsIgnoreCase("byte")) {
            return TypeUtils.castToByte(value);
        } else {
            return TypeUtils.castToDouble(value);
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.getParent() != null) {
            buffer.append(this.getParent().toString()).append(", ");
        }
        buffer.append("{number:").append(type).append("}");
        return buffer.toString();
    }
}
