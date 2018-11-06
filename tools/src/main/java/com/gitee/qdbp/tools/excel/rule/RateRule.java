package com.gitee.qdbp.tools.excel.rule;

import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.model.CellInfo;

public class RateRule extends BaseRule {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private double rate;

    public RateRule(double rate) {
        this(null, rate);
    }

    public RateRule(CellRule parent, double rate) {
        super(parent);
        this.rate = rate;
    }

    @Override
    public void doImports(CellInfo cellInfo) throws ServiceException {
        if (VerifyTools.isBlank(cellInfo.getValue())) {
            cellInfo.setValue(null);
        } else if (cellInfo.getValue() instanceof String) {
            Double number = TypeUtils.castToDouble(cellInfo.getValue());
            cellInfo.setValue(number == null ? null : number.doubleValue() * rate);
        } else if (cellInfo.getValue() instanceof Number) {
            Number number = (Number) cellInfo.getValue();
            cellInfo.setValue(number.doubleValue() * rate);
        } else {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
        }
    }

    @Override
    public void doExports(CellInfo cellInfo) throws ServiceException {
        if (VerifyTools.isBlank(cellInfo.getValue())) {
            cellInfo.setValue(null);
        } else if (cellInfo.getValue() instanceof Number) {
            cellInfo.setValue(((Number) cellInfo.getValue()).doubleValue() / rate);
        } else {
            Double number = TypeUtils.castToDouble(cellInfo.getValue());
            cellInfo.setValue(number == null ? null : number.doubleValue() / rate);
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.getParent() != null) {
            buffer.append(this.getParent().toString()).append(", ");
        }
        buffer.append("{rate:").append(rate).append("}");
        return buffer.toString();
    }
}
