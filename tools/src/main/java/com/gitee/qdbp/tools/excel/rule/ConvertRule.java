package com.gitee.zhaohuihua.tools.excel.rule;

import java.util.Map;
import com.gitee.zhaohuihua.core.exception.ServiceException;
import com.gitee.zhaohuihua.tools.excel.model.CellInfo;

/**
 * 业务转换规则
 *
 * @author zhaohuihua
 * @version 160302
 */
public interface ConvertRule {

    /** 单元格字段转换 **/
    void convert(Map<String, Object> map, CellInfo cell) throws ServiceException;

}
