package com.gitee.zhaohuihua.tools.excel.rule;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.zhaohuihua.core.exception.ServiceException;
import com.gitee.zhaohuihua.core.result.ResultCode;
import com.gitee.zhaohuihua.tools.excel.model.CellInfo;
import com.gitee.zhaohuihua.tools.utils.VerifyTools;

/**
 * 日期转换规则
 *
 * @author zhaohuihua
 * @version 160302
 */
public class DateRule implements PresetRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private String pattern;

    /**
     * 构造函数
     *
     * @param pattern 日期格式
     */
    public DateRule(String pattern) {
        this.pattern = pattern;
        // 检查日期格式
        new SimpleDateFormat(pattern).format(new Date());
    }

    @Override
    public void imports(Map<String, Object> map, CellInfo cell) throws ServiceException {
        String field = cell.getField();
        Object value = cell.getValue();
        if (VerifyTools.isBlank(value) || !(value instanceof String)) {
            map.put(field, value);
        } else {
            try {
                map.put(field, new SimpleDateFormat(pattern).parse((String) value));
            } catch (ParseException e) {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        }
    }

    @Override
    public void exports(Map<String, Object> map, CellInfo cell) throws ServiceException {
        String field = cell.getField();
        Object value = cell.getValue();
        if (VerifyTools.isBlank(value)) {
            map.put(field, null);
        } else if (value instanceof Date) {
            map.put(field, new SimpleDateFormat(pattern).format((Date) value));
        } else {
            try {
                Date date = TypeUtils.castToDate(value);
                map.put(field, new SimpleDateFormat(pattern).format(date));
            } catch (Exception e) {
                map.put(field, "#N/A");
            }
        }
    }
}
