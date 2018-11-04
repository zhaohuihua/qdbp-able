package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
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
    public void doImports(Map<String, Object> map, CellInfo cellInfo, String field, Object value) throws ServiceException {
        if (value instanceof String) {
            try {
                map.put(field, new SimpleDateFormat(pattern).parse((String) value));
            } catch (ParseException e) {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        } else if (value instanceof Date) {
            map.put(field, value);
        } else {
            map.put(field, value);
        }
    }

    @Override
    public void doExports(Map<String, Object> map, CellInfo cellInfo, String field, Object value) throws ServiceException {
        if (VerifyTools.isBlank(value)) {
            map.put(field, null);
        } else if (value instanceof Date) {
            map.put(field, new SimpleDateFormat(pattern).format((Date) value));
        } else {
            try {
                Date date = TypeUtils.castToDate(value);
                map.put(field, new SimpleDateFormat(pattern).format(date));
            } catch (Exception e) {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        }
    }

}
