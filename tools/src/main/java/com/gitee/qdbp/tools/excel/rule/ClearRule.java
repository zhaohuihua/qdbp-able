package com.gitee.qdbp.tools.excel.rule;

import java.util.Map;
import java.util.regex.Pattern;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.utils.JsonTools;

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
    public void doImports(Map<String, Object> map, CellInfo cellInfo, String field, Object value)
            throws ServiceException {
        if (value instanceof String) {
            map.put(field, clear.matcher((String) value).replaceAll(""));
        } else {
            map.put(field, value);
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.getParent() != null) {
            buffer.append(this.getParent().toString()).append(", ");
        }
        buffer.append("{clear:").append(JsonTools.toJsonString(clear)).append("}");
        return buffer.toString();
    }
}
