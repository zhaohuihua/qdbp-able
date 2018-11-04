package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.Map;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

public class BaseRule implements CellRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 上级规则 **/
    private CellRule parent;

    public BaseRule() {
        this(null);
    }

    public BaseRule(CellRule parent) {
        this.parent = parent;
    }

    @Override
    public final void imports(Map<String, Object> map, CellInfo cellInfo) throws ServiceException {
        if (parent != null) {
            parent.imports(map, cellInfo);
        }
        String field = cellInfo.getField();
        Object value = cellInfo.getValue();
        this.doImports(map, cellInfo, field, value);
    }

    protected void doImports(Map<String, Object> map, CellInfo cellInfo, String field, Object value)
            throws ServiceException {
        map.put(field, value);
    }

    @Override
    public final void exports(Map<String, Object> map, CellInfo cellInfo) throws ServiceException {
        if (parent != null) {
            parent.exports(map, cellInfo);
        }
        String field = cellInfo.getField();
        Object value = cellInfo.getValue();
        this.doExports(map, cellInfo, field, value);
    }

    protected void doExports(Map<String, Object> map, CellInfo cellInfo, String field, Object value)
            throws ServiceException {
        map.put(field, value);
    }

    /** 上级规则 **/
    public void setParent(CellRule parent) {
        this.parent = parent;
    }

    /** 上级规则 **/
    public CellRule getParent() {
        return parent;
    }
}
