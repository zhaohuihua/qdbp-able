package com.gitee.zhaohuihua.tools.cache;

/**
 * ICacheKeys的基础实现类
 *
 * @author zhaohuihua
 * @version 170606
 */
public class BaseKeys<T> {

    private String name;

    private Class<T> type;

    public BaseKeys(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public Class<T> type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String toString() {
        return name;
    }

    public static class KeyValue<T> extends BaseKeys<T> implements ICacheKeys.KeyValue<T> {

        public KeyValue(String name, Class<T> type) {
            super(name, type);
        }
    }

    public static class KeyList<T> extends BaseKeys<T> implements ICacheKeys.KeyList<T> {

        public KeyList(String name, Class<T> type) {
            super(name, type);
        }
    }

    public static class FieldValue<T> extends BaseKeys<T> implements ICacheKeys.FieldValue<T> {

        public FieldValue(String name, Class<T> type) {
            super(name, type);
        }
    }

    public static class FieldList<T> extends BaseKeys<T> implements ICacheKeys.FieldList<T> {

        public FieldList(String name, Class<T> type) {
            super(name, type);
        }
    }

    public static class KeyString extends KeyValue<String> {

        public KeyString(String name) {
            super(name, String.class);
        }
    }

    public static class FieldString extends FieldValue<String> {

        public FieldString(String name) {
            super(name, String.class);
        }
    }
}
