package com.gitee.zhaohuihua.tools.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.zhaohuihua.core.beans.VolatileData;

/**
 * 内存中的缓存
 *
 * @author zhaohuihua
 * @version 170527
 */
public class InMemoryCache extends BaseCache {

    /** 静态实例 **/
    public static final InMemoryCache me = new InMemoryCache();

    private Map<String, VolatileData<?>> container;

    public InMemoryCache() {
        super();
        this.container = new ConcurrentHashMap<>();
    }

    /** {@inheritDoc} **/
    @Override
    protected void set(String key, String subkey, Object value, Long expire) {
        if (value == null) {
            del(key, subkey);
        } else {
            container.put(concat(key, subkey), new SimpleItem(value));
            if (expire != null) {
                expire(concat(key, subkey), expire);
            }
        }
    }

    /** {@inheritDoc} **/
    @Override
    protected <T> T get(String key, String subkey, Class<T> clazz) {
        SimpleItem item = getSimpleItem(key, subkey);
        if (item == null) return null;

        return TypeUtils.castToJavaBean(item.getValue(), clazz);
    }

    /** {@inheritDoc} **/
    @Override
    protected <T> List<T> list(String key, String subkey, Class<T> clazz) {
        SimpleItem item = getSimpleItem(key, subkey);
        if (item == null) return null;

        Object value = item.getValue();
        String k = concat(key, subkey);
        return castToList(k, value, clazz);
    }

    /** {@inheritDoc} **/
    @Override
    public boolean exist(String key, String subkey) {
        SimpleItem item = getSimpleItem(key, subkey);
        return item != null;
    }

    /** {@inheritDoc} **/
    @Override
    protected void expire(String key, String subkey, Long expire) {
        SimpleItem item = getSimpleItem(key, subkey);
        if (item == null) return;

        item.expire(expire);
    }

    /** {@inheritDoc} **/
    @Override
    public void persist(String key, String subkey) {
        SimpleItem item = getSimpleItem(key, subkey);
        if (item == null) return;

        item.persist();
    }

    /** {@inheritDoc} **/
    @Override
    public void del(String key, String subkey) {
        container.remove(concat(key, subkey));
    }

    /** {@inheritDoc} **/
    @Override
    public <T> void hset(String key, String subkey, String field, T value) {
        MapItem item = getOrCreateMapItem(key, subkey);

        Map<String, String> hash = item.getValue();
        hash.put(field, serializeValue(value));
    }

    /** {@inheritDoc} **/
    @Override
    public <T> T hget(String key, String subkey, String field, Class<T> clazz) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return null;

        Map<String, String> hash = item.getValue();
        String value = hash.get(field);
        if (value == null) return null;

        return deserializeValue(value, clazz);
    }

    /** {@inheritDoc} **/
    @Override
    public <T> List<T> hlist(String key, String subkey, String field, Class<T> clazz) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return null;

        Map<String, String> hash = item.getValue();
        String value = hash.get(field);
        if (value == null) return null;

        return deserializeList(value, clazz);
    }

    /** {@inheritDoc} **/
    @Override
    public boolean hexist(String key, String subkey, String field) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return false;

        Map<String, ?> hash = item.getValue();
        return hash.containsKey(field);
    }

    /** {@inheritDoc} **/
    @Override
    public void hdel(String key, String subkey, String field) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return;

        Map<String, ?> hash = item.getValue();
        hash.remove(field);
    }

    /** {@inheritDoc} **/
    @Override
    public <T> void hmset(String key, String subkey, Map<String, T> params) {
        if (params == null || params.isEmpty()) return;

        MapItem item = getOrCreateMapItem(key, subkey);

        Map<String, String> hash = item.getValue();
        for (Map.Entry<String, T> entry : params.entrySet()) {
            hash.put(entry.getKey(), serializeValue(entry.getValue()));
        }
    }

    /** {@inheritDoc} **/
    @Override
    public Map<String, String> hmget(String key, String subkey, List<String> fields) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return null;

        Map<String, String> hash = item.getValue();

        Map<String, String> map = new HashMap<>();
        if (fields == null) {
            map.putAll(hash);
        } else if (fields.size() == 0) {
            // do nothing
        } else {
            for (String field : fields) {
                map.put(field, hash.get(field));
            }
        }
        return map;
    }

    /** {@inheritDoc} **/
    @Override
    public <T> Map<String, T> hmget(String key, String subkey, List<String> fields, Class<T> clazz) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return null;

        Map<String, String> hash = item.getValue();

        Map<String, T> map = new HashMap<>();
        if (fields == null) {
            for (Map.Entry<String, String> entry : hash.entrySet()) {
                map.put(entry.getKey(), deserializeValue(entry.getValue(), clazz));
            }
        } else if (fields.size() == 0) {
            // do nothing
        } else {
            for (String field : fields) {
                map.put(field, deserializeValue(hash.get(field), clazz));
            }
        }
        return map;
    }

    /** {@inheritDoc} **/
    @Override
    public long hmdel(String key, String subkey, List<String> fields) {
        if (fields == null || fields.size() == 0) {
            return 0;
        } else {
            MapItem item = getMapItem(key, subkey);
            if (item == null) return 0;

            Map<String, String> hash = item.getValue();
            int total = 0;
            for (String field : fields) {
                if (hash.remove(field) != null) {
                    total++;
                }
            }
            return total;
        }
    }

    /** {@inheritDoc} **/
    @Override
    public <T> void hoset(String key, String subkey, T object) {
        Objects.requireNonNull(object, "object");
        Map<String, String> map = serializeFields(object);

        MapItem item = getOrCreateMapItem(key, subkey);

        Map<String, String> hash = item.getValue();
        hash.putAll(map);
    }

    /** {@inheritDoc} **/
    @Override
    public <T> T hoget(String key, String subkey, Class<T> clazz) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return null;

        Map<String, String> hash = item.getValue();
        if (hash.isEmpty()) {
            return null;
        } else {
            return deserializeFeilds(hash, clazz);
        }
    }

    /** {@inheritDoc} **/
    @Override
    public <T> Map<String, T> haget(String key, String subkey, Class<T> clazz) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return null;

        Map<String, String> hash = item.getValue();
        Map<String, T> values = new HashMap<>();
        if (!hash.isEmpty()) {
            for (Map.Entry<String, String> entry : hash.entrySet()) {
                values.put(entry.getKey(), deserializeValue(entry.getValue(), clazz));
            }
        }
        return values;
    }

    /** {@inheritDoc} **/
    @Override
    public Map<String, String> haget(String key, String subkey) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return null;

        Map<String, String> hash = item.getValue();
        Map<String, String> values = new HashMap<>();
        values.putAll(hash);
        return values;
    }

    /** {@inheritDoc} **/
    @Override
    public Set<String> hkeys(String key, String subkey) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return null;

        Map<String, String> hash = item.getValue();
        return hash.keySet();
    }

    /** {@inheritDoc} **/
    @Override
    public int hlen(String key, String subkey) {
        MapItem item = getMapItem(key, subkey);
        if (item == null) return 0;

        Map<String, String> hash = item.getValue();
        return hash.size();
    }

    /** 将value转换为List, key只是用于记日志 **/
    @SuppressWarnings("unchecked")
    protected <T> List<T> castToList(String key, Object value, Class<T> clazz) {
        if (value instanceof String) {
            return JSONArray.parseArray((String) value, clazz);
        } else if (value instanceof Collection) {
            if (value instanceof List) { // value就是List对象
                List<?> list = (List<?>) value;
                boolean matches = true;
                for (Object i : list) { // 逐一判断list内的对象是否匹配目标类
                    if (i != null && !clazz.isAssignableFrom(i.getClass())) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    return (List<T>) list;
                }
            }

            // value不是List对象, 或value=List但内容不能匹配目标类
            List<T> list = new ArrayList<>();
            Collection<?> values = (Collection<?>) value;
            for (Object i : values) { // 逐一转换为目标类
                list.add(TypeUtils.castToJavaBean(i, clazz));
            }
            return list;
        } else {
            throw new IllegalArgumentException("Can not cast to list, key: " + key + ", value: " + value);
        }
    }

    /** 获取简单缓存对象 **/
    protected SimpleItem getSimpleItem(String key, String subkey) {
        VolatileData<?> item = this.container.get(concat(key, subkey));
        if (item != null && !(item instanceof SimpleItem)) {
            throw new IllegalArgumentException("WRONGTYPE Operation against a key holding the wrong kind of value");
        }
        return item == null || item.expired() || item.getValue() == null ? null : (SimpleItem) item;
    }

    /** 获取Map缓存对象 **/
    protected MapItem getMapItem(String key, String subkey) {
        VolatileData<?> item = this.container.get(concat(key, subkey));
        if (item != null && !(item instanceof MapItem)) {
            throw new IllegalArgumentException("WRONGTYPE Operation against a key holding the wrong kind of value");
        }
        return item == null || item.expired() || item.getValue() == null ? null : (MapItem) item;
    }

    /** 获取或创建Map缓存对象 **/
    protected MapItem getOrCreateMapItem(String key, String subkey) {
        String k = concat(key, subkey);
        String m = "WRONGTYPE Operation against a key holding the wrong kind of value";
        VolatileData<?> item = this.container.get(k);
        if (item != null && !(item instanceof MapItem)) {
            throw new IllegalArgumentException(m);
        }
        MapItem mi = (MapItem) item;
        if (mi == null || mi.expired() || mi.getValue() == null) {
            synchronized (k.intern()) {
                item = this.container.get(k);
                if (item != null && !(item instanceof MapItem)) {
                    throw new IllegalArgumentException(m);
                }
                mi = (MapItem) item;
                if (mi == null || mi.expired() || mi.getValue() == null) {
                    mi = new MapItem(new ConcurrentHashMap<>());
                    this.container.put(k, mi);
                }
            }
        }
        return mi;
    }

    /** 简单缓存类 **/
    protected static class SimpleItem extends VolatileData<Object> {

        public SimpleItem(Object value) {
            super(value);
        }
    }

    /** Map缓存类 **/
    protected static class MapItem extends VolatileData<Map<String, String>> {

        public MapItem(Map<String, String> value) {
            super(value);
        }
    }
}
