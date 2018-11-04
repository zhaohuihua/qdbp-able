package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.Map;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 忽略格式错误的内容, 如果不加这个规则, 解析失败将返回原文
 *
 * @author zhaohuihua
 * @version 181027
 */
public class IgnoreIllegalValue implements CellRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 上级规则 **/
    private CellRule parent;

    public IgnoreIllegalValue(CellRule parent) {
        this.parent = parent;
    }

    @Override
    public final void imports(Map<String, Object> map, CellInfo cellInfo) throws ServiceException {
        if (parent == null) {
            map.put(cellInfo.getField(), cellInfo.getValue());
        } else {
            try {
                parent.imports(map, cellInfo);
            } catch (Exception e) {
                map.put(cellInfo.getField(), null);
            }
        }
    }

    @Override
    public final void exports(Map<String, Object> map, CellInfo cellInfo) throws ServiceException {
        if (parent == null) {
            map.put(cellInfo.getField(), cellInfo.getValue());
        } else {
            try {
                parent.exports(map, cellInfo);
            } catch (Exception e) {
                map.put(cellInfo.getField(), null);
            }
        }
    }

}
