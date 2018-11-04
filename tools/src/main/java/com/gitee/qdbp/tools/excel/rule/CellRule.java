package com.gitee.qdbp.tools.excel.rule;

import java.util.Map;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 单元格转换规则, 同时负责导入和导出的转换
 *
 * @author zhaohuihua
 * @version 160302
 */
public interface CellRule {

    /** 导入转换 **/
    void imports(Map<String, Object> map, CellInfo cellInfo) throws ServiceException;

    /** 导出转换 **/
    void exports(Map<String, Object> map, CellInfo cellInfo) throws ServiceException;

}
