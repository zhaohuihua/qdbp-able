package com.gitee.qdbp.tools.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.FieldDeserializer;
import com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.DoubleSerializer;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.JavaBeanSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
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
     * 将Map内容设置到Java对象中<br>
     * copy from JavaBeanDeserializer.createInstance(Map<String, Object>, ParserConfig);
     * 
     * @param <T> 目标类型
     * @param map Map
     * @param bean 目标Java对象
     */
    public static <T> void mapFillBean(Map<String, ?> map, T bean) {
        Class<?> clazz = bean.getClass();
        ParserConfig config = ParserConfig.getGlobalInstance();
        ObjectDeserializer deserializer = config.getDeserializer(clazz);
        if (!(deserializer instanceof JavaBeanDeserializer)) {
            throw new JSONException("can not get javaBeanDeserializer. " + clazz.getName());
        }
        JavaBeanDeserializer javaBeanDeser = (JavaBeanDeserializer) deserializer;
        try {
            mapFillBean(map, bean, config, javaBeanDeser);
        } catch (Exception e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    private static void mapFillBean(Map<String, ?> map, Object bean, ParserConfig config,
            JavaBeanDeserializer javaBeanDeser) throws IllegalArgumentException, IllegalAccessException {
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            FieldDeserializer fieldDeser = javaBeanDeser.smartMatch(key);
            if (fieldDeser == null) {
                continue;
            }

            final FieldInfo fieldInfo = fieldDeser.fieldInfo;
            Field field = fieldDeser.fieldInfo.field;
            Type paramType = fieldInfo.fieldType;

            if (field != null) {
                Class<?> fieldType = field.getType();
                if (fieldType == boolean.class) {
                    if (value == Boolean.FALSE) {
                        field.setBoolean(bean, false);
                        continue;
                    }

                    if (value == Boolean.TRUE) {
                        field.setBoolean(bean, true);
                        continue;
                    }
                } else if (fieldType == int.class) {
                    if (value instanceof Number) {
                        field.setInt(bean, ((Number) value).intValue());
                        continue;
                    }
                } else if (fieldType == long.class) {
                    if (value instanceof Number) {
                        field.setLong(bean, ((Number) value).longValue());
                        continue;
                    }
                } else if (fieldType == float.class) {
                    if (value instanceof Number) {
                        field.setFloat(bean, ((Number) value).floatValue());
                        continue;
                    } else if (value instanceof String) {
                        String strVal = (String) value;
                        float floatValue;
                        if (strVal.length() <= 10) {
                            floatValue = TypeUtils.parseFloat(strVal);
                        } else {
                            floatValue = Float.parseFloat(strVal);
                        }

                        field.setFloat(bean, floatValue);
                        continue;
                    }
                } else if (fieldType == double.class) {
                    if (value instanceof Number) {
                        field.setDouble(bean, ((Number) value).doubleValue());
                        continue;
                    } else if (value instanceof String) {
                        String strVal = (String) value;
                        double doubleValue;
                        if (strVal.length() <= 10) {
                            doubleValue = TypeUtils.parseDouble(strVal);
                        } else {
                            doubleValue = Double.parseDouble(strVal);
                        }

                        field.setDouble(bean, doubleValue);
                        continue;
                    }
                } else if (value != null && paramType == value.getClass()) {
                    field.set(bean, value);
                    continue;
                }
            }

            String format = fieldInfo.format;
            if (format != null && paramType == Date.class) {
                value = TypeUtils.castToDate(value, format);
            } else {
                if (paramType instanceof ParameterizedType) {
                    value = TypeUtils.cast(value, (ParameterizedType) paramType, config);
                } else {
                    value = TypeUtils.cast(value, paramType, config);
                }
            }

            fieldDeser.setValue(bean, value);
        }
    }

    /**
     * 将Map转换为Java对象
     * 
     * @param <T> 目标类型
     * @param map Map
     * @param clazz 目标Java类
     * @return Java对象
     */
    public static <T> T mapToBean(Map<String, ?> map, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Map<String, Object> json = (Map<String, Object>) map;
        return TypeUtils.castToJavaBean(json, clazz, ParserConfig.getGlobalInstance());
    }

    /**
     * 将Java对象转换为Map<br>
     * copy from fastjson JSON.toJSON(), 保留enum和date
     * 
     * @param bean JavaBean对象
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object bean) {
        if (bean == null) {
            return null;
        }
        return beanToMap(bean, true, true);
    }

    /**
     * 将Java对象转换为Map
     * 
     * @param object Java对象
     * @param deep 是否递归转换子对象
     * @param clearBlankValue 是否清除空值
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object object, boolean deep, boolean clearBlankValue) {
        if (object == null) {
            return null;
        }
        Map<String, Object> map = getBeanFieldValuesMap(object, deep, SerializeConfig.getGlobalInstance());
        if (clearBlankValue) {
            ConvertTools.clearBlankValue(map);
        }
        return map;
    }

    protected static JSONObject getBeanFieldValuesMap(Object bean, boolean deep, SerializeConfig config) {
        if (bean == null) {
            return null;
        }

        if (bean instanceof JSONObject) {
            return (JSONObject) bean;
        }

        if (bean instanceof JSONArray) {
            throw new IllegalArgumentException(bean.getClass().getSimpleName() + " can't convert to map.");
        }

        if (bean instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) bean;

            Map<String, Object> innerMap;
            if (map instanceof LinkedHashMap) {
                innerMap = new LinkedHashMap<>(map.size());
            } else if (map instanceof TreeMap) {
                innerMap = new TreeMap<>();
            } else {
                innerMap = new HashMap<>(map.size());
            }

            JSONObject json = new JSONObject(innerMap);

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String jsonKey = TypeUtils.castToString(entry.getKey());
                Object jsonValue = !deep ? entry.getValue() : beanToJson(entry.getValue(), config);
                json.put(jsonKey, jsonValue);
            }

            return json;
        }

        if (bean instanceof Collection) {
            throw new IllegalArgumentException(bean.getClass().getSimpleName() + " can't convert to map.");
        }

        if (bean instanceof JSONSerializable) {
            String string = JSON.toJSONString(bean);
            Object json = JSON.parse(string);
            return getBeanFieldValuesMap(json, deep, config);
        }

        Class<?> clazz = bean.getClass();

        if (clazz.isEnum()) {
            throw new IllegalArgumentException(bean.getClass().getSimpleName() + " can't convert to map.");
        }
        if (clazz == String.class) {
            throw new IllegalArgumentException(bean.getClass().getSimpleName() + " can't convert to map.");
        }
        if (CharSequence.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(bean.getClass().getSimpleName() + " can't convert to map.");
        }
        if (isPrimitive(clazz)) {
            throw new IllegalArgumentException(bean.getClass().getSimpleName() + " can't convert to map.");
        }
        if (clazz.isArray()) {
            throw new IllegalArgumentException(bean.getClass().getSimpleName() + " can't convert to map.");
        }

        ObjectSerializer serializer = config.getObjectWriter(clazz);
        if (serializer instanceof JavaBeanSerializer) {
            JavaBeanSerializer javaBeanSerializer = (JavaBeanSerializer) serializer;

            JSONObject json = new JSONObject();
            try {
                Map<String, Object> values = javaBeanSerializer.getFieldValuesMap(bean);
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    Object jsonValue = !deep ? entry.getValue() : beanToJson(entry.getValue(), config);
                    json.put(entry.getKey(), jsonValue);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(bean.getClass().getSimpleName() + " can't convert to map.", e);
            }
            return json;
        }

        throw new IllegalArgumentException(bean.getClass().getSimpleName() + " can't convert to map.");
    }

    private static Object beanToJson(Object bean, SerializeConfig config) {
        if (bean == null) {
            return null;
        }

        if (bean instanceof JSON) {
            return bean;
        }

        if (bean instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) bean;

            Map<String, Object> innerMap;
            if (map instanceof LinkedHashMap) {
                innerMap = new LinkedHashMap<>(map.size());
            } else if (map instanceof TreeMap) {
                innerMap = new TreeMap<>();
            } else {
                innerMap = new HashMap<>(map.size());
            }

            JSONObject json = new JSONObject(innerMap);

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String jsonKey = TypeUtils.castToString(entry.getKey());
                Object jsonValue = beanToJson(entry.getValue(), config);
                json.put(jsonKey, jsonValue);
            }

            return json;
        }

        if (bean instanceof Collection) {
            Collection<?> collection = (Collection<?>) bean;

            JSONArray array = new JSONArray(collection.size());

            for (Object item : collection) {
                Object jsonValue = beanToJson(item, config);
                array.add(jsonValue);
            }

            return array;
        }

        if (bean instanceof JSONSerializable) {
            String json = JSON.toJSONString(bean);
            return JSON.parse(json);
        }

        Class<?> clazz = bean.getClass();

        if (clazz.isEnum()) {
            // return ((Enum<?>) bean).name();
            return bean;
        }
        if (clazz == String.class) {
            return bean;
        }
        if (CharSequence.class.isAssignableFrom(clazz)) {
            return bean.toString();
        }
        if (isPrimitive(clazz)) {
            return bean;
        }

        if (clazz.isArray()) {
            int len = Array.getLength(bean);

            JSONArray array = new JSONArray(len);

            for (int i = 0; i < len; ++i) {
                Object item = Array.get(bean, i);
                Object jsonValue = beanToJson(item, config);
                array.add(jsonValue);
            }

            return array;
        }

        ObjectSerializer serializer = config.getObjectWriter(clazz);
        if (serializer instanceof JavaBeanSerializer) {
            JavaBeanSerializer javaBeanSerializer = (JavaBeanSerializer) serializer;

            JSONObject json = new JSONObject();
            try {
                Map<String, Object> values = javaBeanSerializer.getFieldValuesMap(bean);
                for (Map.Entry<String, Object> entry : values.entrySet()) {
                    json.put(entry.getKey(), beanToJson(entry.getValue(), config));
                }
            } catch (Exception e) {
                throw new JSONException("BeanConvertToJsonError", e);
            }
            return json;
        }

        String text = JSON.toJSONString(bean);
        return JSON.parse(text);
    }

    private static boolean isPrimitive(Class<?> clazz) {
        // @formatter:off
        return clazz.isPrimitive()
            || clazz.isEnum()
            || clazz == Boolean.class
            || clazz == Character.class
            || clazz == String.class
            || Number.class.isAssignableFrom(clazz)
            || Date.class.isAssignableFrom(clazz);
        // @formatter:on
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
