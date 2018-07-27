package com.gitee.zhaohuihua.tools.excel.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.zhaohuihua.core.exception.ServiceException;
import com.gitee.zhaohuihua.core.result.ResultCode;
import com.gitee.zhaohuihua.core.utils.StringTools;
import com.gitee.zhaohuihua.core.utils.VerifyTools;
import com.gitee.zhaohuihua.tools.excel.model.CellInfo;

/**
 * 映射规则
 *
 * @author zhaohuihua
 * @version 160302
 */
public class MapRule implements PresetRule, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private Map<String, String> imports = new HashMap<>();

    private Map<String, String> exports = new HashMap<>();

    /**
     * 构造函数
     *
     * @param rule 映射规则<br>
     *            如 { "PROVINCE":"1|省", "CITY":"2|市", "DISTRICT":"3|区|县|区/县" }
     */
    public MapRule(String rule) {
        JSONObject json = JSON.parseObject(rule);
        for (String key : json.keySet()) {
            addRule(key, StringTools.split(json.getString(key)));
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
    public void imports(Map<String, Object> map, CellInfo cell) throws ServiceException {
        String field = cell.getField();
        Object value = cell.getValue();
        if (VerifyTools.isBlank(value)) {
            map.put(field, value);
        } else {
            String string = value.toString();
            if (imports.containsKey(string)) {
                map.put(field, imports.get(string));
            } else {
                throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR);
            }
        }
    }

    @Override
    public void exports(Map<String, Object> map, CellInfo cell) throws ServiceException {
        String field = cell.getField();
        Object value = cell.getValue();
        if (VerifyTools.isBlank(value)) {
            map.put(field, null);
        } else {
            String string = value.toString();

            if (exports.containsKey(string)) {
                map.put(field, exports.get(string));
            } else {
                map.put(field, "#N/A");
            }
        }
    }

}
