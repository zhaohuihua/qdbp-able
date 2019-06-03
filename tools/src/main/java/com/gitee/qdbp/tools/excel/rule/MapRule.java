package com.gitee.qdbp.tools.excel.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.alibaba.fastjson.JSON;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.StringTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 映射规则
 *
 * @author zhaohuihua
 * @version 160302
 */
public class MapRule implements CellRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private Map<String, String> imports = new HashMap<>();

    private Map<String, String> exports = new HashMap<>();

    /**
     * 构造函数
     *
     * @param rule 映射规则 如 { "PROVINCE":"1|省", "CITY":"2|市", "DISTRICT":"3|区|县|区/县" }
     */
    public MapRule(String rule) {
        this(JSON.parseObject(rule));
    }

    /**
     * 构造函数
     *
     * @param rule 映射规则 如 { "PROVINCE":"1|省", "CITY":"2|市", "DISTRICT":"3|区|县|区/县" }
     */
    public MapRule(Map<String, Object> rule) {
        for (Entry<String, Object> entry : rule.entrySet()) {
            if (VerifyTools.isNoneBlank(entry.getKey(), entry.getValue())) {
                addRule(entry.getKey(), StringTools.split(entry.getValue().toString()));
            }
        }
    }

    protected void addRules(Map<String, String[]> map) {
        for (Entry<String, String[]> entry : map.entrySet()) {
            addRule(entry.getKey(), entry.getValue());
        }
    }

    protected void addRule(String key, String[] values) {
        if (VerifyTools.isAnyBlank(key, values)) {
            return;
        }
        // 导入时, "1", "省" 都转换为PROVINCE
        for (String value : values) {
            imports.put(value, key);
        }
        // 导出时, PROVINCE 转换为 "省", 即第1个值
        exports.put(key, values[0]);
    }

    @Override
    public Map<String, Object> imports(CellInfo cellInfo) throws ServiceException {
        if (VerifyTools.isNotBlank(cellInfo.getValue())) {
            String string = cellInfo.getValue().toString();
            if (imports.containsKey(string)) {
                cellInfo.setValue(imports.get(string));
            } else {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> exports(CellInfo cellInfo) throws ServiceException {
        if (VerifyTools.isNotBlank(cellInfo.getValue())) {
            String string = cellInfo.getValue().toString();

            if (exports.containsKey(string)) {
                cellInfo.setValue(exports.get(string));
            } else {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        String keys = this.exports == null ? null : ConvertTools.joinToString(this.exports.keySet());
        buffer.append("{map:").append(keys).append("}");
        return buffer.toString();
    }

}
