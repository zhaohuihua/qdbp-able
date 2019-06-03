package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.Map;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.utils.VerifyTools;

public class RateRule implements CellRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private double rate;

    public RateRule() {
        this.rate = 1;
    }

    public RateRule(double rate) {
        this.rate = rate;
    }

    @Override
    public Map<String, Object> imports(CellInfo cellInfo) throws ServiceException {
        Object value = cellInfo.getValue();
        if (VerifyTools.isBlank(value)) {
            return null;
        }
        if (value instanceof String) {
            Double number = TypeUtils.castToDouble(value);
            cellInfo.setValue(number == null ? null : number.doubleValue() * rate);
        } else if (value instanceof Number) {
            Number number = (Number) value;
            cellInfo.setValue(number.doubleValue() * rate);
        } else {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
        }
        return null;
    }

    @Override
    public Map<String, Object> exports(CellInfo cellInfo) throws ServiceException {
        Object value = cellInfo.getValue();
        if (VerifyTools.isBlank(value)) {
            return null;
        }
        if (value instanceof Number) {
            cellInfo.setValue(((Number) value).doubleValue() / rate);
        } else {
            Double number = TypeUtils.castToDouble(value);
            cellInfo.setValue(number == null ? null : number.doubleValue() / rate);
        }
        return null;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{rate:").append(rate).append("}");
        return buffer.toString();
    }
}
