package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.Map;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

public class BaseRule implements PresetRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 上级规则 **/
    private PresetRule parent;

    public BaseRule() {
        this(null);
    }

    public BaseRule(PresetRule parent) {
        this.parent = parent;
    }

    @Override
    public final void imports(Map<String, Object> map, CellInfo cell) throws ServiceException {
        if (parent != null) {
            parent.imports(map, cell);
        }
        String field = cell.getField();
        Object value = cell.getValue();
        this.doImports(map, cell, field, value);
    }

    protected void doImports(Map<String, Object> map, CellInfo cell, String field, Object value)
            throws ServiceException {
        map.put(field, value);
    }

    @Override
    public final void exports(Map<String, Object> map, CellInfo cell) throws ServiceException {
        if (parent != null) {
            parent.exports(map, cell);
        }
        String field = cell.getField();
        Object value = cell.getValue();
        this.doExports(map, cell, field, value);
    }

    protected void doExports(Map<String, Object> map, CellInfo cell, String field, Object value)
            throws ServiceException {
        map.put(field, value);
    }
}
