package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Pattern;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 清除规则
 *
 * @author zhaohuihua
 * @version 181104
 */
public class ClearRule implements CellRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private Pattern clear;

    public ClearRule(String regexp) {
        this.clear = Pattern.compile(regexp);
    }

    public ClearRule(Pattern regexp) {
        this.clear = regexp;
    }

    @Override
    public Map<String, Object> imports(CellInfo cellInfo) throws ServiceException {
        return doConvert(cellInfo);
    }

    @Override
    public Map<String, Object> exports(CellInfo cellInfo) throws ServiceException {
        return doConvert(cellInfo);
    }

    private Map<String, Object> doConvert(CellInfo cellInfo) throws ServiceException {
        if (cellInfo.getValue() instanceof String) {
            cellInfo.setValue(clear.matcher((String) cellInfo.getValue()).replaceAll(""));
        }
        return null;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{clear:").append(clear).append("}");
        return buffer.toString();
    }
}
