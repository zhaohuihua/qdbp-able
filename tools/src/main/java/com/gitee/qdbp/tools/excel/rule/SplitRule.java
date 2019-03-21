package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.utils.ConvertTools;
import com.gitee.qdbp.tools.excel.model.CellInfo;

/** 拆分规则 **/
public class SplitRule implements CellRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 正则表达式转义字符 **/
    private static final Pattern REG_CHAR = Pattern.compile("([\\{\\}\\[\\]\\(\\)\\^\\$\\.\\*\\?\\-\\+\\\\])");

    private String separator;
    private Pattern regexp;

    public SplitRule(char... separator) {
        String string = REG_CHAR.matcher(new String(separator)).replaceAll("\\\\$1");
        this.regexp = Pattern.compile("[" + string + "]");
        this.separator = String.valueOf(separator[0]);
    }

    @Override
    public Map<String, Object> imports(CellInfo cellInfo) throws ServiceException {
        if (cellInfo.getValue() instanceof String) {
            String string = (String) cellInfo.getValue();
            List<String> result = ConvertTools.toList(regexp.split(string));
            cellInfo.setValue(result);
        }
        return null;
    }

    @Override
    public Map<String, Object> exports(CellInfo cellInfo) throws ServiceException {
        if (cellInfo.getValue() instanceof Collection) {
            Collection<?> collection = (Collection<?>) cellInfo.getValue();
            cellInfo.setValue(ConvertTools.joinToString(collection, separator));
        }
        return null;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{split:").append(separator).append("}");
        return buffer.toString();
    }
}
