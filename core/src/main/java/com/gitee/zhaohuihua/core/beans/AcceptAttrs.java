package com.gitee.zhaohuihua.core.beans;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 用来做数据隔离的数据权限属性
 *
 * @author zhaohuihua
 * @version 171031
 */
public class AcceptAttrs<T> implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 是否全部允许 **/
    protected boolean all;
    /** 是否采用前模匹配(如部门编号, 具有A1部门权限就允许访问A1B1,A1B2,A1B3C3等部门) **/
    protected boolean starts;
    /** 默认值 **/
    protected T defaultValue;
    /** 是否必填 **/
    protected boolean required;
    /** 允许的值 **/
    protected Collection<T> acceptValues;

    public AcceptAttrs() {
        acceptValues = new HashSet<>();
    }

    /** 默认值 **/
    public T getDefaultValue() {
        return defaultValue;
    }

    /** 默认值 **/
    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    /** 判断是否全部允许 **/
    public boolean isAll() {
        return all;
    }

    /** 设置是否全部允许 **/
    public void setAll(boolean all) {
        this.all = all;
    }

    /** 是否采用前模匹配(如部门编号, 具有A1部门权限就允许访问A1B1,A1B2,A1B3C3等部门) **/
    public boolean isStarts() {
        return starts;
    }

    /** 是否采用前模匹配(如部门编号, 具有A1部门权限就允许访问A1B1,A1B2,A1B3C3等部门) **/
    public void setStarts(boolean starts) {
        this.starts = starts;
    }

    /** 是否必填 **/
    public boolean isRequired() {
        return required;
    }

    /** 是否必填 **/
    public void setRequired(boolean required) {
        this.required = required;
    }

    /** 判断是否被允许 **/
    public boolean accept(Object value) {
        if (all) {
            return true;
        } else if (isBlank(value)) {
            return false;
        } else if (equals(defaultValue, value)) {
            return true;
        } else {
            if (starts && value instanceof CharSequence) {
                // 如果是前模匹配模式, 并且是字符串, 判断前缀
                for (T i : this.acceptValues) {
                    if (value.toString().startsWith(i.toString())) {
                        return true;
                    }
                }
                return false;
            } else {
                return this.acceptValues.contains(value);
            }
        }
    }

    /** 获取允许值 **/
    public List<T> getAcceptValues() {
        List<T> list = new ArrayList<>();
        list.addAll(this.acceptValues);
        return list;
    }

    /** 设置允许值 **/
    public void setAcceptValues(List<T> values) {
        this.acceptValues.clear();
        this.addAcceptValues(values);
    }

    /** 增加允许值 **/
    public void addAcceptValue(T value) {
        this.addAcceptValues(Arrays.asList(value));
    }

    /** 增加允许值 **/
    public void addAcceptValues(List<T> values) {
        for (T value : values) {
            if (!isBlank(value)) {
                this.acceptValues.add(value);
            }
        }
    }

    private static boolean isBlank(Object object) {
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

    private static boolean equals(Object o, Object n) {
        if (o == null && n == null) {
            return true;
        } else if (o == null && n != null || o != null && n == null) {
            return false;
        } else {
            return o.equals(n);
        }
    }
}
