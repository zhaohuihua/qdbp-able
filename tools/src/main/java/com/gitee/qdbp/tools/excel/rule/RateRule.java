package com.gitee.qdbp.tools.excel.rule;

import java.util.Map;
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
    public void doImports(Map<String, Object> map, CellInfo cellInfo, String field, Object value) throws ServiceException {
        if (VerifyTools.isBlank(value)) {
            map.put(field, null);
        } else if (value instanceof String) {
            try {
                Double number = TypeUtils.castToDouble(value);
                map.put(field, number == null ? null : number.doubleValue() * rate);
            } catch (Exception e) {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        } else if (value instanceof Number) {
            Number number = (Number) value;
            map.put(field, number.doubleValue() * rate);
        } else {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
        }
    }

    @Override
    public void doExports(Map<String, Object> map, CellInfo cellInfo, String field, Object value) throws ServiceException {
        if (VerifyTools.isBlank(value)) {
            map.put(field, null);
        } else if (value instanceof Number) {
            map.put(field, ((Number) value).doubleValue() / rate);
        } else {
            try {
                Double number = TypeUtils.castToDouble(value);
                map.put(field, number == null ? null : number.doubleValue() / rate);
            } catch (Exception e) {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        }
    }
}
