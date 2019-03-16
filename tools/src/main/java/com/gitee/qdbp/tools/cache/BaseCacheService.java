package com.gitee.qdbp.tools.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.qdbp.able.beans.Duration;
import com.gitee.qdbp.able.utils.ConvertTools;
import com.gitee.qdbp.able.utils.VerifyTools;

/**
 * 基础缓存类
 *
 * @author zhaohuihua
 * @version 170606
 */
public abstract class BaseCacheService implements ICacheService {

    private static final Logger log = LoggerFactory.getLogger(BaseCacheService.class);

    @Override
    public <T> void set(String key, String subkey, T value, Duration duration) {
        this.set(key, subkey, value, totime(duration));
    }

    @Override
    public void expire(String key, String subkey, Duration duration) {
        this.expire(key, subkey, totime(duration));
    }

    protected abstract <T> void set(String key, String subkey, T value, Long expire);

    protected abstract void expire(String key, String subkey, Long expire);

    protected Long totime(Duration duration) {
        return duration == null ? null : duration.toMillis();
    }

    protected String concat(String key, String... fields) {
        if (fields == null || fields.length == 0) return key;

        StringBuilder buffer = new StringBuilder();
        buffer.append(key);
        for (String field : fields) {
            if (VerifyTools.isNotBlank(field)) {
                buffer.append(":").append(field);
            }
        }
        return buffer.toString();
    }

    protected String[] toArray(List<String> strings) {
        return ConvertTools.toArray(strings, String.class);
    }

    protected <T> Map<String, String> serializeFields(T value) {
        Object object = JSON.toJSON(value);
        if (object instanceof JSONObject) {
            JSONObject json = (JSONObject) object;
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                if (VerifyTools.isNotBlank(entry.getValue())) {
                    map.put(entry.getKey(), serializeValue(entry.getValue()));
                }
            }
            return map;
        } else {
            throw new IllegalArgumentException("value must be a plain object");
        }
    }

    protected <T> T deserializeFeilds(Map<String, String> map, Class<T> clazz) {
        // JSONObject json = new JSONObject();
        // json.putAll(map);
        // return JSON.toJavaObject(json, clazz);
        StringBuilder buffer = new StringBuilder();
        for (Entry<String, String> entry : map.entrySet()) {
            if (VerifyTools.isAnyBlank(entry.getKey(), entry.getValue())) continue;
            if (buffer.length() > 0) buffer.append(",");
            buffer.append('"').append(entry.getKey()).append('"').append(':');
            String value = entry.getValue();
            if (value.startsWith("{") && value.endsWith("}")) { // 对象
                buffer.append(value);
            } else if (value.startsWith("[") && value.endsWith("]")) { // 数组
                buffer.append(value);
            } else { // 普通字符串
                buffer.append(JSON.toJSONString(value)); // 替换字符串中的引号反斜杠
            }
        }
        String string = "{" + buffer.toString() + "}";

        try {
            return JSON.parseObject(string, clazz);
        } catch (Exception e) {
            log.error("JsonParseError:{}, class={}, text={}", e.toString(), clazz.getSimpleName(), string);
            throw e;
        }
    }

    protected <T> String serializeValue(T value) {
        if (value == null) {
            return null;
        } else if (value instanceof CharSequence) {
            return value.toString();
        } else if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        } else {
            return JSON.toJSONString(value);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T deserializeValue(String string, Class<T> clazz) {
        if (string == null) {
            return null;
        } else if (clazz.isAssignableFrom(string.getClass())) {
            return (T) string;
        } else {
            try {
                return JSON.parseObject(string, clazz);
            } catch (Exception e) {
                log.error("JsonParseError:{}, class={}, text={}", e.toString(), clazz.getSimpleName(), string);
                throw e;
            }
        }
    }

    protected <T> List<T> deserializeList(String string, Class<T> clazz) {
        if (string == null) {
            return null;
        } else {
            try {
                return JSON.parseArray(string, clazz);
            } catch (Exception e) {
                log.error("JsonParseError:{}, class={}, text={}", e.toString(), clazz.getSimpleName(), string);
                throw e;
            }
        }
    }
}
