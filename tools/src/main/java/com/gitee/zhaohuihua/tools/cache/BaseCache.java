package com.gitee.zhaohuihua.tools.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.zhaohuihua.core.beans.Duration;
import com.gitee.zhaohuihua.tools.cache.ICacheKeys.FieldList;
import com.gitee.zhaohuihua.tools.cache.ICacheKeys.FieldValue;
import com.gitee.zhaohuihua.tools.cache.ICacheKeys.KeyList;
import com.gitee.zhaohuihua.tools.cache.ICacheKeys.KeyValue;
import com.gitee.zhaohuihua.tools.utils.ConvertTools;
import com.gitee.zhaohuihua.tools.utils.VerifyTools;

/**
 * 基础缓存类
 *
 * @author zhaohuihua
 * @version 170606
 */
public abstract class BaseCache implements ICacheService {

    private static final Logger log = LoggerFactory.getLogger(BaseCache.class);

    private Map<String, ICacheKeys<?>> keys;

    public BaseCache() {
        this.keys = new ConcurrentHashMap<>();
    }

    /**
     * 保存对象
     *
     * @param key 关键字
     * @param value 值
     */
    @Override
    public <T> void set(ICacheKeys.KeyValue<T> key, T value) {
        set(topath(key), null, value, totime(key));
    }

    /**
     * 保存对象
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param value 值
     */
    @Override
    public <T> void set(ICacheKeys.FieldValue<T> key, String subkey, T value) {
        set(topath(key), subkey, value, totime(key));
    }

    /**
     * 保存列表
     *
     * @param key 关键字
     * @param value 值
     */
    @Override
    public <T> void set(ICacheKeys.KeyList<T> key, List<T> value) {
        set(topath(key), null, value, totime(key));
    }

    /**
     * 保存列表
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param value 值
     */
    @Override
    public <T> void set(ICacheKeys.FieldList<T> key, String subkey, List<T> value) {
        set(topath(key), subkey, value, totime(key));
    }

    /**
     * 保存对象
     *
     * @param key 关键字
     * @param value 值
     * @param duration 过期时间
     */
    @Override
    public <T> void set(ICacheKeys.KeyValue<T> key, T value, Duration expire) {
        set(topath(key), null, value, totime(expire));
    }

    /**
     * 保存对象
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param value 值
     * @param expire 过期时间
     */
    @Override
    public <T> void set(ICacheKeys.FieldValue<T> key, String subkey, T value, Duration expire) {
        set(topath(key), subkey, value, totime(expire));
    }

    /**
     * 保存列表
     *
     * @param key 关键字
     * @param value 值
     * @param expire 过期时间
     */
    @Override
    public <T> void set(ICacheKeys.KeyList<T> key, List<T> value, Duration expire) {
        set(topath(key), null, value, totime(expire));
    }

    /**
     * 保存列表
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param value 值
     * @param expire 过期时间
     */
    @Override
    public <T> void set(ICacheKeys.FieldList<T> key, String subkey, List<T> value, Duration expire) {
        set(topath(key), subkey, value, totime(expire));
    }

    /**
     * 从缓存中取出对象
     *
     * @param key 关键字
     * @return 缓存对象
     */
    @Override
    public <T> T get(ICacheKeys.KeyValue<T> key) {
        return get(topath(key), null, key.type());
    }

    /**
     * 从缓存中取出对象
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @return 缓存对象
     */
    @Override
    public <T> T get(ICacheKeys.FieldValue<T> key, String subkey) {
        return get(topath(key), subkey, key.type());
    }

    /**
     * 从缓存中取出对象
     *
     * @param key 关键字
     * @return 缓存对象
     */
    @Override
    public <T> List<T> get(ICacheKeys.KeyList<T> key) {
        return list(topath(key), null, key.type());
    }

    /**
     * 从缓存中取出对象
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @return 缓存对象
     */
    @Override
    public <T> List<T> get(ICacheKeys.FieldList<T> key, String subkey) {
        return list(topath(key), subkey, key.type());
    }

    /**
     * 判断缓存是否存在key
     *
     * @param key 关键字
     * @return 是否存在
     */
    @Override
    public boolean exist(ICacheKeys.KeyValue<?> key) {
        return exist(topath(key), null);
    }

    /**
     * 判断缓存是否存在key
     *
     * @param key 关键字
     * @return 是否存在
     */
    @Override
    public boolean exist(ICacheKeys.KeyList<?> key) {
        return exist(topath(key), null);
    }

    /**
     * 判断缓存是否存在key
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @return 是否存在
     */
    @Override
    public boolean exist(ICacheKeys.FieldValue<?> key, String subkey) {
        return exist(topath(key), subkey);
    }

    /**
     * 判断缓存是否存在key
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @return 是否存在
     */
    @Override
    public boolean exist(ICacheKeys.FieldList<?> key, String subkey) {
        return exist(topath(key), subkey);
    }

    /**
     * 删除缓存中的指定key的值
     *
     * @param key 关键字
     */
    @Override
    public void del(ICacheKeys.KeyValue<?> key) {
        del(topath(key), null);
    }

    /**
     * 删除缓存中的指定key的值
     *
     * @param key 关键字
     */
    @Override
    public void del(ICacheKeys.KeyList<?> key) {
        del(topath(key), null);
    }

    /**
     * 删除缓存中的指定key的值
     *
     * @param key 关键字
     * @param subkey 子关键字
     */
    @Override
    public void del(ICacheKeys.FieldValue<?> key, String subkey) {
        del(topath(key), subkey);
    }

    /**
     * 删除缓存中的指定key的值
     *
     * @param key 关键字
     * @param subkey 子关键字
     */
    @Override
    public void del(ICacheKeys.FieldList<?> key, String subkey) {
        del(topath(key), subkey);
    }

    /**
     * 设置过期时间
     *
     * @param key 关键字
     * @param time 过期时间
     */
    @Override
    public void expire(ICacheKeys.KeyValue<?> key, Duration time) {
        expire(topath(key), null, totime(time));
    }

    /**
     * 设置过期时间
     *
     * @param key 关键字
     * @param time 过期时间
     */
    @Override
    public void expire(ICacheKeys.KeyList<?> key, Duration time) {
        expire(topath(key), null, totime(time));
    }

    /**
     * 设置过期时间
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param time 过期时间
     */
    @Override
    public void expire(ICacheKeys.FieldValue<?> key, String subkey, Duration time) {
        expire(topath(key), subkey, totime(time));
    }

    /**
     * 设置过期时间
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param time 过期时间
     */
    @Override
    public void expire(ICacheKeys.FieldList<?> key, String subkey, Duration time) {
        expire(topath(key), subkey, totime(time));
    }

    /**
     * 移除过期时间
     *
     * @param key 关键字
     */
    @Override
    public void persist(KeyValue<?> key) {
        this.persist(topath(key), null);
    }

    /**
     * 移除过期时间
     *
     * @param key 关键字
     */
    @Override
    public void persist(KeyList<?> key) {
        this.persist(topath(key), null);
    }

    /**
     * 移除过期时间
     *
     * @param key 关键字
     */
    @Override
    public void persist(FieldValue<?> key, String subkey) {
        this.persist(topath(key), subkey);
    }

    /**
     * 移除过期时间
     *
     * @param key 关键字
     */
    @Override
    public void persist(FieldList<?> key, String subkey) {
        this.persist(topath(key), subkey);
    }

    /**
     * 设置过期时间
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param time 过期时间
     */
    public void expire(String key, String subkey, Duration time) {
        this.expire(key, subkey, totime(time));
    }

    protected void set(String key, Object value, Long expire) {
        set(key, null, value, expire);
    }

    protected <T> T get(String key, Class<T> clazz) {
        return get(key, null, clazz);
    }

    protected <T> List<T> list(String key, Class<T> clazz) {
        return list(key, null, clazz);
    }

    public void expire(String key, Duration time) {
        this.expire(key, null, totime(time));
    }

    protected void expire(String key, Long expire) {
        expire(key, null, expire);
    }

    public void persist(String key) {
        this.persist(key, null);
    }

    public boolean exist(String key) {
        return exist(key, null);
    }

    public void del(String key) {
        del(key, null);
    }

    @Override
    public <T> void hset(String key, String field, T value) {
        this.hset(key, null, field, value);
    }

    @Override
    public <T> T hget(String key, String field, Class<T> clazz) {
        return this.hget(key, null, field, clazz);
    }

    @Override
    public <T> List<T> hlist(String key, String field, Class<T> clazz) {
        return this.hlist(key, null, field, clazz);
    }

    @Override
    public boolean hexist(String key, String field) {
        return this.hexist(key, null, field);
    }

    @Override
    public void hdel(String key, String field) {
        this.hdel(key, null, field);
    }

    @Override
    public <T> void hmset(String key, Map<String, T> map) {
        this.hmset(key, null, map);
    }

    @Override
    public Map<String, String> hmget(String key, List<String> fields) {
        return this.hmget(key, null, fields);
    }

    @Override
    public <T> Map<String, T> hmget(String key, List<String> fields, Class<T> clazz) {
        return this.hmget(key, null, fields, clazz);
    }

    @Override
    public long hmdel(String key, List<String> fields) {
        return this.hmdel(key, null, fields);
    }

    @Override
    public <T> void hoset(String key, T object) {
        this.hoset(key, null, object);
    }

    @Override
    public <T> T hoget(String key, Class<T> clazz) {
        return this.hoget(key, null, clazz);
    }

    @Override
    public <T> Map<String, T> haget(String key, Class<T> clazz) {
        return this.haget(key, null, clazz);
    }

    @Override
    public Map<String, String> haget(String key) {
        return this.haget(key, (String) null);
    }

    @Override
    public Set<String> hkeys(String key) {
        return this.hkeys(key, null);
    }

    @Override
    public int hlen(String key) {
        return this.hlen(key, null);
    }

    protected abstract void set(String key, String subkey, Object value, Long expire);

    protected abstract <T> T get(String key, String subkey, Class<T> clazz);

    protected abstract <T> List<T> list(String key, String subkey, Class<T> clazz);

    protected abstract void expire(String key, String subkey, Long expire);

    public abstract void persist(String key, String subkey);

    public abstract boolean exist(String key, String subkey);

    public abstract void del(String key, String subkey);

    @Override
    public <T> void hset(String key, String subkey, String field, T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T hget(String key, String subkey, String field, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> hlist(String key, String subkey, String field, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hexist(String key, String subkey, String field) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void hdel(String key, String subkey, String field) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void hmset(String key, String subkey, Map<String, T> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> hmget(String key, String subkey, List<String> fields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Map<String, T> hmget(String key, String subkey, List<String> fields, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long hmdel(String key, String subkey, List<String> fields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void hoset(String key, String subkey, T object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T hoget(String key, String subkey, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Map<String, T> haget(String key, String subkey, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> haget(String key, String subkey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> hkeys(String key, String subkey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hlen(String key, String subkey) {
        throw new UnsupportedOperationException();
    }

    protected Long totime(Duration duration) {
        return duration == null ? null : duration.toMillis();
    }

    protected Long totime(ICacheKeys<?> key) {
        if (key instanceof ICacheKeys.ExpireFixed) {
            return ((ICacheKeys.ExpireFixed) key).time();
        } else {
            return null;
        }
    }

    private String topath(ICacheKeys<?> key) {
        checkKeyTypeDuplicate(key);
        return key.name();
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
            String string = JSON.toJSONString(value);
            if (string.startsWith("\"") && string.startsWith("\"")) {
                return string.substring(1, string.length() - 1);
            } else if (string.startsWith("'") && string.startsWith("'")) {
                return string.substring(1, string.length() - 1);
            } else {
                return string;
            }
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

    /** 检查KEY类型是否冲突 **/
    private void checkKeyTypeDuplicate(ICacheKeys<?> key) throws IllegalArgumentException {
        String path = key.name();
        if (!keys.containsKey(path)) {
            keys.put(path, key);
        } else {
            ICacheKeys<?> old = keys.get(path);
            if (key.type() == String.class || old.type() == String.class) {
                return; // String类型的不作判断
            }
            if (key.type() == Object.class || old.type() == Object.class) {
                return; // Object类型的不作判断
            }
            if (!key.equals(old) && key.getClass() != old.getClass() && key.type() != old.type()) {
                String k = key.name();
                String o = old.getClass().getName();
                String n = key.getClass().getName();
                throw new IllegalArgumentException("Key duplicate, " + k + " in " + o + " and " + n + ".");
            }
        }
    }
}
