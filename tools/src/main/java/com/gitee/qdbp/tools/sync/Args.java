package com.gitee.zhaohuihua.tools.sync;

import java.util.Arrays;

/**
 * 调用方法的参数
 *
 * @author zhh
 * @version 170526
 */
public interface Args {

    int length();

    Arg<?>[] get();

    Class<?>[] types();

    Object[] values();

    public static class Arg<T> {

        private Class<T> type;
        private T value;

        public Arg(Class<T> type) {
            this.type = type;
        }

        public <V extends T> Arg(Class<T> type, V value) {
            this.type = type;
            this.value = value;
        }

        public Class<T> getType() {
            return type;
        }

        public T getValue() {
            return value;
        }

    }

    public static class AnyArgs implements Args {

        private final Arg<?>[] args;

        public AnyArgs(Object... objects) {
            int length = objects == null ? 0 : objects.length;
            this.args = new Arg<?>[length];
            for (int i = 0; i < length; i++) {
                Object o = objects[i];
                if (o == null) {
                    throw new IllegalArgumentException("null value[" + i + "] must use new Arg(xxx.class, value)");
                } else if (o instanceof Arg) {
                    Arg<?> arg = (Arg<?>) o;
                    this.args[i] = arg;
                } else {
                    @SuppressWarnings("unchecked")
                    Class<Object> type = (Class<Object>) o.getClass();
                    this.args[i] = new Arg<Object>(type, o);
                }
            }
        }

        @Override
        public int length() {
            return args == null ? 0 : args.length;
        }

        @Override
        public Arg<?>[] get() {
            return args;
        }

        @Override
        public Class<?>[] types() {
            int length = length();
            Class<?>[] types = new Class<?>[length];
            for (int i = 0; i < length; i++) {
                types[i] = args[i].getType();
            }
            return types;
        }

        @Override
        public Object[] values() {
            int length = length();
            Object[] types = new Object[length];
            for (int i = 0; i < length; i++) {
                types[i] = args[i].getValue();
            }
            return types;
        }

        public String toString() {
            return Arrays.toString(this.values());
        }
    }
}
