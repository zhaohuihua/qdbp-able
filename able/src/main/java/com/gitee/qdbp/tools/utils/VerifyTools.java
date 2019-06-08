package com.gitee.qdbp.tools.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 校验工具类
 *
 * @author zhaohuihua
 * @version 150916
 */
public abstract class VerifyTools {

    /**
     * 检查指定的对象是否不为空, 如果为空(或为空字符串)将抛出IllegalArgumentException异常.<br>
     * 用法: VerifyTools.requireNotBlank(entity.getEmail(), "email");
     * 
     * @param object 待检查的对象
     * @param name 对象的描述或名称
     */
    public static void requireNotBlank(Object object, String name) {
        if (isBlank(object)) {
            throw new IllegalArgumentException(name + " must not be blank.");
        }
    }

    /** 返回第一个非空的参数; 如果全都为空, 返回最后一个参数 **/
    @SuppressWarnings("unchecked")
    public static <T> T nvl(T... objects) {
        T last = null;
        for (T object : objects) {
            last = object;
            if (isNotBlank(object)) {
                return object;
            }
        }
        return last;
    }

    /**
     * 判断对象是否为空<br>
     * 零长度的字符串, length=0的数组, 空的Collection, 空的Map, 空的Iterable都将被判定为空
     * 
     * @param object 目标对象
     * @return true or false
     */
    public static boolean isBlank(Object object) {
        if (object == null) {
            return true;
        }

        if (object instanceof CharSequence) {
            CharSequence string = (CharSequence) object;
            return string.length() == 0;
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        } else if (object instanceof Collection) {
            return ((Collection<?>) object).isEmpty();
        } else if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            return map.isEmpty();
        } else if (object instanceof Iterable) {
            return !((Iterable<?>) object).iterator().hasNext();
        } else {
            return false;
        }
    }

    /**
     * 判断对象是否为非空
     * 
     * @param object 目标对象
     * @return true or false
     */
    public static boolean isNotBlank(Object object) {
        return !isBlank(object);
    }

    /**
     * 只有一个为空就返回true
     *
     * @param objects
     * @return
     */
    public static boolean isAnyBlank(Object... objects) {
        if (objects == null || objects.length == 0) {
            return true;
        }

        for (Object object : objects) {
            if (isBlank(object)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 全都为空就返回true
     *
     * @param objects 目标对象
     * @return 是否全都为空
     */
    public static boolean isAllBlank(Object... objects) {
        if (objects == null || objects.length == 0) {
            return true;
        }

        for (Object object : objects) {
            if (isNotBlank(object)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 全都不为空就返回true
     *
     * @param objects 目标对象
     * @return 是否全都不为空
     */
    public static boolean isNoneBlank(Object... objects) {
        return !isAnyBlank(objects);
    }

    /**
     * 判断对象是否存在于列表中
     * 
     * @param object 目标对象
     * @param objects 列表
     * @return 是否存在
     */
    @SafeVarargs
    public static <T> boolean isExists(T object, T... objects) {
        if (objects == null || objects.length == 0) {
            return false;
        }
        for (Object i : objects) {
            if (i == null && object == null) {
                return true;
            } else if (i != null && i.equals(object)) {
                return true;
            } else if (object != null && object.equals(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断对象是否不存在于列表中
     * 
     * @param object 目标对象
     * @param objects 列表
     * @return 是否不存在
     */
    @SafeVarargs
    public static <T> boolean isNotExists(T object, T... objects) {
        return !isExists(object, objects);
    }

    /**
     * 判断两个对象是不是相等
     *
     * @param o 第一个对象
     * @param n 第二个对象
     * @return 是否相等
     */
    public static boolean equals(Object o, Object n) {
        if (o == null && n == null) {
            return true;
        } else if (o == null && n != null || o != null && n == null) {
            return false;
        } else {
            return o.equals(n);
        }
    }

    /**
     * 判断两个对象是不是不相等
     *
     * @param o 第一个对象
     * @param n 第二个对象
     * @return 是否不相等
     */
    public static boolean notEquals(Object o, Object n) {
        return !equals(o, n);
    }

    /**
     * 判断对象是否改变, 对象不为空且与原值不符则已改变
     *
     * @param value 判断的对象
     * @param older 原值
     * @return 是否改变
     */
    public static boolean isChanged(Object value, Object older) {
        return isNotBlank(value) && notEquals(value, older);
    }
}
