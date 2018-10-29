package com.gitee.qdbp.tools.excel.rule;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    public SplitRule(PresetRule parent, char... separator) {
        super(parent);
        String string = REG_CHAR.matcher(new String(separator)).replaceAll("\\\\$1");
        this.regexp = Pattern.compile("[" + string + "]");
        this.separator = String.valueOf(separator[0]);
    }

    @Override
    public void doImports(Map<String, Object> map, CellInfo cell, String field, Object value) throws ServiceException {
        if (value instanceof String) {
            String string = (String) value;
            List<String> result = ConvertTools.toList(regexp.split(string));
            map.put(field, result);
        } else {
            map.put(field, value);
        }
    }

    @Override
    public void doExports(Map<String, Object> map, CellInfo cell, String field, Object value) throws ServiceException {
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            map.put(field, ConvertTools.joinToString(collection, separator));
        } else {
            map.put(field, value);
        }
    }
}
