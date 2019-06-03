package com.gitee.qdbp.tools.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.DoubleSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.qdbp.able.beans.KeyString;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

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
}
