package com.gitee.qdbp.tools.excel.rule;

import java.util.Map;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 业务转换规则
 *
 * @author zhaohuihua
 * @version 160302
 */
public interface ConvertRule {

    /** 单元格字段转换 **/
    void convert(Map<String, Object> map, CellInfo cellInfo) throws ServiceException;

}
