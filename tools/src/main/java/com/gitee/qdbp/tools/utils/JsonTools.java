package com.gitee.qdbp.tools.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.DoubleSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.beans.DepthMap;
import com.gitee.qdbp.able.beans.KeyString;

/**
 * Json工具类
 *
 * @author zhaohuihua
 * @version 180621
 */
public abstract class JsonTools {

    /**
     * 将对象转换为以换行符分隔的日志文本<br>
     * 如 newlineLogs(params, operator) 返回 \n\t{paramsJson}\n\t{operatorJson}<br>
     * 
     * @param objects 对象
     * @return 日志文本
     */
    public static String newlineLogs(Object... objects) {
        StringBuilder buffer = new StringBuilder();
        for (Object object : objects) {
            buffer.append("\n\t");
            if (object == null) {
                buffer.append("null");
            } else if (object instanceof String) {
                buffer.append(object);
            } else {
                buffer.append(object.getClass().getSimpleName()).append(": ");
                buffer.append(toLogString(object));
            }
        }
        return buffer.toString();
    }

    private static SerializeConfig JSON_CONFIG = new SerializeConfig();
    static {
        JSON_CONFIG.put(Double.class, new DoubleSerializer("#.##################"));
    }

    public static String toLogString(Object object) {
        if (object == null) {
            return "null";
        }
        try (SerializeWriter out = new SerializeWriter()) {
            JSONSerializer serializer = new JSONSerializer(out, JSON_CONFIG);
            serializer.config(SerializerFeature.QuoteFieldNames, false);
            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            serializer.write(object);
            return out.toString();
        }
    }

    public static String toJsonString(Object object) {
        if (object == null) {
            return "null";
        }
        try (SerializeWriter out = new SerializeWriter()) {
            JSONSerializer serializer = new JSONSerializer(out, JSON_CONFIG);
            serializer.config(SerializerFeature.QuoteFieldNames, true);
            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            serializer.write(object);
            return out.toString();
        }
    }

    /**
     * 将Java对象转换为Map
     * 
     * @param object Java对象
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object object) {
        return beanToMap(object, false);
    }

    /**
     * 将Java对象转换为Map
     * 
     * @param object Java对象
     * @param clearBlankValue 是否清除空值
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object object, boolean clearBlankValue) {
        if (object == null) {
            return null;
        }
        Map<String, Object> map = (JSONObject) JSON.toJSON(object);
        return clearBlankValue ? ConvertTools.clearBlankValue(map, false) : map;
    }

    /**
     * 将字符串转换为KeyString对象<br>
     * toKeyString("{'key':1,'value':'冷水'}")<br>
     * 或: toKeyString("{'1':'冷水'}")<br>
     *
     * @param text 字符串
     * @return KeyString对象
     */
    public static KeyString toKeyString(String text) {
        if (VerifyTools.isBlank(text)) {
            return null;
        }
        JSONObject json = JSON.parseObject(text);
        return toKeyString(json);
    }

    /**
     * 将字符串转换为KeyString对象数组<br>
     * toKeyStrings("[{'key':1,'value':'冷水'},{'key':2,'value':'热水'}]")<br>
     * 或: toKeyStrings("{'1':'冷水','2':'热水','3':'直饮水'}")<br>
     * --&gt; List&lt;KeyString&gt;
     *
     * @param text 字符串
     * @return List&lt;KeyString&gt; 对象数组
     */
    public static List<KeyString> toKeyStrings(String text) {
        if (VerifyTools.isBlank(text)) {
            return null;
        }
        List<KeyString> list = new ArrayList<>();

        Object object = JSON.parse(text);
        if (object instanceof JSONArray) {
            JSONArray array = (JSONArray) object;
            for (Object i : array) {
                list.add(toKeyString((JSONObject) i));
            }
        } else if (object instanceof JSONObject) {
            JSONObject json = (JSONObject) object;
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String string = TypeUtils.castToJavaBean(value, String.class);
                list.add(new KeyString(key, string));
            }
        }

        Collections.sort(list);
        return list;

    }

    private static KeyString toKeyString(JSONObject json) {
        if (json.containsKey("key")) {
            return JSON.toJavaObject(json, KeyString.class);
        } else {
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String string = TypeUtils.castToJavaBean(value, String.class);
                return new KeyString(key, string);
            }
            throw new IllegalArgumentException("json is empty.");
        }
    }

    /**
     * 过滤数据字段<br>
     * <pre>
        Map&lt;String, Object&gt; data = { main:{}, detail:{}, subject:{}, finance:[], target:[] };
        String mainFields = "main:id,projectName,publishStatus";
        String detailFields = "detail:id,totalAmount,holdingTime";
        String subjectFields = "subject:id,customerName,industryCategory";
        String financeFields = "finance:id,totalAssets,netAssets,netProfits,busiIncome";
        String targetFields = "target:id,industryCategory,latestMkt,latestPer";
        MapTools.filter(data, mainFields, detailFields, subjectFields, financeFields, targetFields);
     * </pre>
     * 
     * @param data 过滤前的数据
     * @param fields 需要保留的字段列表
     * @return 过滤后的数据
     */
    public Map<String, Object> filterFields(Map<String, Object> data, String... fields) {
        if (VerifyTools.isAnyBlank(data, fields)) {
            return data;
        }
        Map<String, Set<String>> conditions = parseConditioin(fields);
        DepthMap result = new DepthMap();
        for (Map.Entry<String, Set<String>> entry : conditions.entrySet()) {
            String group = entry.getKey();
            Set<String> keys = entry.getValue();
            if (group == null) {
                fillToDepthMap(data, keys, result);
            } else {
                Object value = ReflectTools.getDepthValue(result, group);
                if (VerifyTools.isNotBlank(value)) {
                    fillToDepthMap(value, group, keys, result);
                }
            }
        }
        return result.map();
    }

    private static void fillToDepthMap(Map<String, Object> data, Set<String> keys, DepthMap container) {
        Map<String, Object> filtered = filterObject(data, keys);
        if (VerifyTools.isBlank(filtered)) {
            for (Map.Entry<String, Object> temp : filtered.entrySet()) {
                container.put(temp.getKey(), temp.getValue());
            }
        }
    }

    private static void fillToDepthMap(Object data, String group, Set<String> keys, DepthMap container) {
        if (data.getClass().isArray()) {
            List<?> list = ConvertTools.toList((Object[]) data);
            List<Map<String, Object>> filtered = filterCollection(list, keys);
            if (VerifyTools.isNotBlank(filtered)) {
                container.put(group, filtered);
            }
        } else if (data instanceof Collection) {
            List<Map<String, Object>> filtered = filterCollection((Collection<?>) data, keys);
            if (VerifyTools.isNotBlank(filtered)) {
                container.put(group, filtered);
            }
        } else {
            Map<String, Object> filtered = filterObject(data, keys);
            if (VerifyTools.isNotBlank(filtered)) {
                container.put(group, filtered);
            }
        }
    }

    private static Map<String, Object> filterObject(Object data, Set<String> keys) {
        DepthMap container = new DepthMap();
        for (String key : keys) {
            Object value = ReflectTools.getDepthValue(data, key);
            if (VerifyTools.isNotBlank(value)) {
                container.put(key, value);
            }
        }
        return container.map();
    }

    private static List<Map<String, Object>> filterCollection(Collection<?> data, Set<String> keys) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object object : data) {
            list.add(filterObject(object, keys));
        }
        return list;
    }

    private static Map<String, Set<String>> parseConditioin(String... fields) {
        Map<String, Set<String>> conditions = new HashMap<>();
        for (String field : fields) {
            String group = null;
            String keys = field;
            int index = field.indexOf(':');
            if (index == 0) {
                keys = field.substring(index + 1).trim();
            } else if (index > 0) {
                group = field.substring(0, index).trim();
                keys = field.substring(index + 1).trim();
            }
            String[] fieldNames = StringTools.split(keys, true, ',', ' ');
            if (conditions.containsKey(group)) {
                for (String i : fieldNames) {
                    conditions.get(group).add(i);
                }
            } else {
                Set<String> list = new HashSet<>();
                for (String i : fieldNames) {
                    list.add(i);
                }
                conditions.put(group, list);
            }
        }
        return conditions;
    }
}
