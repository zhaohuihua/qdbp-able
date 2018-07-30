package com.gitee.qdbp.tools.utils;

import java.util.Map;
import com.alibaba.fastjson.JSON;

public class KeyValueTest {

    public static void main(String[] args) {
        // KeyValue implements Entry
        KeyValue<String> item = new KeyValue<>("kkk", "vvv");
        // fastjson-1.2.23.jar 输出: {"key":"kkk","value":"vvv"}
        // fastjson-1.2.24.jar 输出: {"kkk":"vvv"}
        System.out.println(JSON.toJSONString(item));
    }

    public static class KeyValue<V> implements Map.Entry<String, V> {

        /** KEY **/
        private String key;
        /** VALUE **/
        private V value;

        /** 构造函数 **/
        public KeyValue() {
        }

        /** 构造函数 **/
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
        public V setValue(V value) {
            V original = this.value;
            this.value = value;
            return original;
        }
    }
}
