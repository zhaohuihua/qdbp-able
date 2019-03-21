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

    /**
     * 导入转换<br>
     * cellInfo.getValue()是来自excel的原始值(或上一规则转换后的值)<br>
     * 转换时通过cellInfo.setValue()修改为转换后的值<br>
     * 如需将一个字段拆分为多个字段则返回map, 可通过cellInfo.getCells()获取其他单元格的内容
     * 
     * @param cellInfo 单元格信息
     * @return 如需将一个字段拆分为多个字段则返回map
     * @throws ServiceException 转换失败
     */
    Map<String, Object> imports(CellInfo cellInfo) throws ServiceException;

    /**
     * 导出转换<br>
     * cellInfo.getValue()是来自db的原始值(或上一规则转换后的值)<br>
     * 转换时通过cellInfo.setValue()修改为转换后的值<br>
     * 如需将一个字段拆分为多个字段则返回map, 可通过cellInfo.getCells()获取其他单元格的内容
     * 
     * @param cellInfo 单元格信息
     * @return 如需将一个字段拆分为多个字段则返回map
     * @throws ServiceException 转换失败
     */
    Map<String, Object> exports(CellInfo cellInfo) throws ServiceException;

}
