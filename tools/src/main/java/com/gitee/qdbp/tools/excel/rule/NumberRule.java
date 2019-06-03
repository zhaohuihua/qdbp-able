package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.Map;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 数字规则
 *
 * @author zhaohuihua
 * @version 181104
 */
public class NumberRule implements CellRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private String type;

    public NumberRule() {
    }

    public NumberRule(String type) {
        this.type = type;
    }

    @Override
    public Map<String, Object> imports(CellInfo cellInfo) throws ServiceException {
        if (VerifyTools.isNotBlank(cellInfo.getValue())) {
            cellInfo.setValue(toNumber(cellInfo.getValue(), type));
        }
        return null;
    }

    @Override
    public Map<String, Object> exports(CellInfo cellInfo) throws ServiceException {
        if (VerifyTools.isNotBlank(cellInfo.getValue())) {
            cellInfo.setValue(toNumber(cellInfo.getValue(), type));
        }
        return null;
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
        buffer.append("{number:").append(type).append("}");
        return buffer.toString();
    }
}
