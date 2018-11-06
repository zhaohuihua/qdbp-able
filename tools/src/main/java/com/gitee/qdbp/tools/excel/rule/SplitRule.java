package com.gitee.qdbp.tools.excel.rule;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.utils.ConvertTools;

/** 拆分规则 **/
public class SplitRule extends BaseRule {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 正则表达式转义字符 **/
    private static final Pattern REG_CHAR = Pattern.compile("([\\{\\}\\[\\]\\(\\)\\^\\$\\.\\*\\?\\-\\+\\\\])");

    private String separator;
    private Pattern regexp;

    public SplitRule(char... separator) {
        this(null, separator);
    }

    public SplitRule(CellRule parent, char... separator) {
        super(parent);
        String string = REG_CHAR.matcher(new String(separator)).replaceAll("\\\\$1");
        this.regexp = Pattern.compile("[" + string + "]");
        this.separator = String.valueOf(separator[0]);
    }

    @Override
    public void doImports(CellInfo cellInfo) throws ServiceException {
        if (cellInfo.getValue() instanceof String) {
            String string = (String) cellInfo.getValue();
            List<String> result = ConvertTools.toList(regexp.split(string));
            cellInfo.setValue(result);
        }
    }

    @Override
    public void doExports(CellInfo cellInfo) throws ServiceException {
        if (cellInfo.getValue() instanceof Collection) {
            Collection<?> collection = (Collection<?>) cellInfo.getValue();
            cellInfo.setValue(ConvertTools.joinToString(collection, separator));
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.getParent() != null) {
            buffer.append(this.getParent().toString()).append(", ");
        }
        buffer.append("{split:").append(separator).append("}");
        return buffer.toString();
    }
}
