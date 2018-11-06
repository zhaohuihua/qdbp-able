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
        this.doImports(cellInfo);
        map.put(cellInfo.getField(), cellInfo.getValue());
    }

    protected void doImports(CellInfo cellInfo) throws ServiceException {
    }

    @Override
    public final void exports(Map<String, Object> map, CellInfo cellInfo) throws ServiceException {
        if (parent != null) {
            parent.exports(map, cellInfo);
        }
        this.doExports(cellInfo);
        map.put(cellInfo.getField(), cellInfo.getValue());
    }

    protected void doExports(CellInfo cellInfo) throws ServiceException {
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
