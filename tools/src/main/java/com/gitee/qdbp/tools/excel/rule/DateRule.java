package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 日期转换规则
 *
 * @author zhaohuihua
 * @version 160302
 */
public class DateRule extends BaseRule implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private String pattern;

    /**
     * 构造函数
     *
     * @param pattern 日期格式
     */
    public DateRule(String pattern) {
        this(null, pattern);
    }

    /**
     * 构造函数
     * 
     * @param parent 上级规则
     * @param pattern 日期格式
     */
    public DateRule(CellRule parent, String pattern) {
        super(parent);
        this.pattern = pattern;
        // 检查日期格式
        new SimpleDateFormat(pattern).format(new Date());
    }

    @Override
    public void doImports(CellInfo cellInfo)
            throws ServiceException {
        if (cellInfo.getValue() instanceof String) {
            try {
                cellInfo.setValue(new SimpleDateFormat(pattern).parse((String) cellInfo.getValue()));
            } catch (ParseException e) {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
            }
        }
    }

    @Override
    public void doExports(CellInfo cellInfo)
            throws ServiceException {
        if (VerifyTools.isBlank(cellInfo.getValue())) {
            cellInfo.setValue(null);
        } else if (cellInfo.getValue() instanceof Date) {
            cellInfo.setValue(new SimpleDateFormat(pattern).format((Date) cellInfo.getValue()));
        } else {
            Date date = TypeUtils.castToDate(cellInfo.getValue());
            cellInfo.setValue(new SimpleDateFormat(pattern).format(date));
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.getParent() != null) {
            buffer.append(this.getParent().toString()).append(", ");
        }
        buffer.append("{pattern:").append(pattern).append("}");
        return buffer.toString();
    }

}
