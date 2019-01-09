package com.gitee.qdbp.able.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * KeyValue类
 *
 * @author zhaohuihua
 * @version 140521
 */
public class KeyValue<V> implements Comparable<KeyValue<V>> {

    /** KEY **/
    private String key;

    /** VALUE **/
    private V value;

    /** 构造函数 **/
    public KeyValue() {
    }

    /**
     * 构造函数
     *
     * @param key KEY
     * @param value VALUE
     */
    public KeyValue(String key, V value) {
        this.key = key;
        this.value = value;
    }

    /** 获取KEY **/
    public String getKey() {
        return key;
    }

    /** 设置KEY **/
    public void setKey(String key) {
        this.key = key;
    }

    /** 获取VALUE **/
    public V getValue() {
        return value;
    }

    /** 设置VALUE **/
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * List转换为Map
     *
     * @author zhaohuihua
     * @param list KeyValue列表
     * @return Map
     */
    public static <V, C extends V, KV extends KeyValue<C>> Map<String, V> toMap(List<KV> list) {
        if (list == null) {
            return null;
        }

        Map<String, V> map = new HashMap<String, V>();
        for (KeyValue<C> i : list) {
            map.put(i.key, i.value);
        }
        return map;
    }

    /**
     * Map转换为List
     *
     * @author zhaohuihua
     * @param map Map对象
     * @return List
     */
    public static <V, C extends V> List<KeyValue<V>> toList(Map<String, C> map) {
        List<KeyValue<V>> entries = new ArrayList<>();

        Set<Map.Entry<String, C>> original = map.entrySet();
        for (Map.Entry<String, C> entry : original) {
            String key = entry.getKey();
            V value = entry.getValue();
            entries.add(new KeyValue<V>(key, value));
        }
        return entries;
    }

    /**
     * Map转换为List
     *
     * @author zhaohuihua
     * @param map Map对象
     * @return List
     */
    public static <V, C extends V, KV extends KeyValue<C>> List<KV> toList(Map<String, C> map, Class<KV> clazz) {
        List<KV> entries = new ArrayList<>();

        Set<Map.Entry<String, C>> original = map.entrySet();
        try {
            for (Map.Entry<String, C> entry : original) {
                KV kv = clazz.newInstance();
                kv.setKey(entry.getKey());
                kv.setValue(entry.getValue());
                entries.add(kv);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to clazz.newInstance()", e);
        }
        return entries;
    }

    @Override
    public String toString() {
        return String.format("{%s=%s}", key, value);
    }

    @Override
    public int compareTo(KeyValue<V> o) {
        return key == null ? 0 : key.compareTo(o.getKey());
    }
}
