package com.gitee.qdbp.tools.excel.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.alibaba.fastjson.JSON;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.model.CellInfo;
import com.gitee.qdbp.tools.utils.ConvertTools;

/**
 * 映射规则
 *
 * @author zhaohuihua
 * @version 160302
 */
public class MapRule extends BaseRule {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private Map<String, String> imports = new HashMap<>();

    private Map<String, String> exports = new HashMap<>();

    /**
     * 构造函数
     *
     * @param rule 映射规则, 如 { "PROVINCE":"1|省", "CITY":"2|市", "DISTRICT":"3|区|县|区/县" }
     */
    public MapRule(String rule) {
        this(null, rule);
    }

    /**
     * 构造函数
     *
     * @param parent 上级规则
     * @param rule 映射规则 如 { "PROVINCE":"1|省", "CITY":"2|市", "DISTRICT":"3|区|县|区/县" }
     */
    public MapRule(CellRule parent, String rule) {
        this(parent, JSON.parseObject(rule));
    }

    /**
     * 构造函数
     *
     * @param parent 上级规则
     * @param rule 映射规则 如 { "PROVINCE":"1|省", "CITY":"2|市", "DISTRICT":"3|区|县|区/县" }
     */
    public MapRule(CellRule parent, Map<String, Object> rule) {
        super(parent);
        for (Entry<String, Object> entry : rule.entrySet()) {
            if (VerifyTools.isNoneBlank(entry.getKey(), entry.getValue())) {
                addRule(entry.getKey(), StringTools.split(entry.getValue().toString()));
            }
        }
    }

    /**
     * 构造函数
     *
     * @param map 映射规则<br>
     *            如 { "PROVINCE":["省","1"], "CITY":["市","2"], "DISTRICT":["区/县", "区", "县", "3"] }
     */
    public MapRule(Map<String, String[]> map) {
        addRules(map);
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
    public void doImports(CellInfo cellInfo) throws ServiceException {
        if (VerifyTools.isBlank(cellInfo.getValue())) {
            cellInfo.setValue(null);
        } else {
            String string = cellInfo.getValue().toString();
            if (imports.containsKey(string)) {
                cellInfo.setValue(imports.get(string));
            } else {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        }
    }

    @Override
    public void doExports(CellInfo cellInfo) throws ServiceException {
        if (VerifyTools.isBlank(cellInfo.getValue())) {
            cellInfo.setValue(null);
        } else {
            String string = cellInfo.getValue().toString();

            if (exports.containsKey(string)) {
                cellInfo.setValue(exports.get(string));
            } else {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.getParent() != null) {
            buffer.append(this.getParent().toString()).append(", ");
        }
        String keys = this.exports == null ? null : ConvertTools.joinToString(this.exports.keySet());
        buffer.append("{map:").append(keys).append("}");
        return buffer.toString();
    }

}
