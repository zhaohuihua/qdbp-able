package com.gitee.qdbp.tools.excel.rule;

import java.util.Map;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.model.CellInfo;

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
    public void doImports(Map<String, Object> map, CellInfo cellInfo, String field, Object value)
            throws ServiceException {
        if (VerifyTools.isBlank(value)) {
            map.put(field, null);
        } else {
            try {
                map.put(field, toNumber(value, type));
            } catch (Exception e) {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        }
    }

    @Override
    public void doExports(Map<String, Object> map, CellInfo cellInfo, String field, Object value)
            throws ServiceException {
        if (VerifyTools.isBlank(value)) {
            map.put(field, null);
        } else {
            try {
                map.put(field, toNumber(value, type));
            } catch (Exception e) {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
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
}
