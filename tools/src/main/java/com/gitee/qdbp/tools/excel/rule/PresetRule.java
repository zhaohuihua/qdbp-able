package com.gitee.qdbp.tools.excel.rule;

import java.util.Map;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 预置的转换规则, 同时负责导入和导出的转换
 *
 * @author zhaohuihua
 * @version 160302
 */
public interface PresetRule {

    /** 导入转换 **/
    void imports(Map<String, Object> map, CellInfo cell) throws ServiceException;

    /** 导出转换 **/
    void exports(Map<String, Object> map, CellInfo cell) throws ServiceException;

}
