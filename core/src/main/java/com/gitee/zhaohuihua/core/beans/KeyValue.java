package com.gitee.zhaohuihua.core.beans;

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
    public static <V> Map<String, V> toMap(List<KeyValue<V>> list) {
        if (list == null) {
            return null;
        }

        Map<String, V> map = new HashMap<String, V>();
        for (KeyValue<V> i : list) {
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
    public static <V> List<KeyValue<V>> toList(Map<String, V> map) {
        List<KeyValue<V>> entries = new ArrayList<KeyValue<V>>();

        Set<Map.Entry<String, V>> original = map.entrySet();
        for (Map.Entry<String, V> entry : original) {
            String key = entry.getKey();
            V value = entry.getValue();
            entries.add(new KeyValue<V>(key, value));
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
