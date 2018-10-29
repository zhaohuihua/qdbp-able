package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.Map;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 忽略错误
 *
 * @author zhaohuihua
 * @version 181027
 */
public class IgnoreError implements PresetRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 上级规则 **/
    private PresetRule parent;

    public IgnoreError(PresetRule parent) {
        this.parent = parent;
    }

    @Override
    public final void imports(Map<String, Object> map, CellInfo cell) throws ServiceException {
        if (parent == null) {
            map.put(cell.getField(), cell.getValue());
        } else {
            try {
                parent.imports(map, cell);
            } catch (Exception e) {
                map.put(cell.getField(), null);
            }
        }
    }

    @Override
    public final void exports(Map<String, Object> map, CellInfo cell) throws ServiceException {
        if (parent == null) {
            map.put(cell.getField(), cell.getValue());
        } else {
            try {
                parent.exports(map, cell);
            } catch (Exception e) {
                map.put(cell.getField(), null);
            }
        }
    }

}
