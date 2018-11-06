package com.gitee.qdbp.tools.excel.rule;

import java.util.regex.Pattern;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/**
 * 清除规则
 *
 * @author zhaohuihua
 * @version 181104
 */
public class ClearRule extends BaseRule {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private Pattern clear;

    public ClearRule(String regexp) {
        this(null, regexp);
    }

    public ClearRule(CellRule parent, String regexp) {
        super(parent);
        this.clear = Pattern.compile(regexp);
    }

    @Override
    public void doImports(CellInfo cellInfo) throws ServiceException {
        if (cellInfo.getValue() instanceof String) {
            cellInfo.setValue(clear.matcher((String) cellInfo.getValue()).replaceAll(""));
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.getParent() != null) {
            buffer.append(this.getParent().toString()).append(", ");
        }
        buffer.append("{clear:").append(clear).append("}");
        return buffer.toString();
    }
}
