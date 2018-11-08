package com.gitee.qdbp.able.instance;

import java.util.Comparator;
import java.util.Map;
import com.gitee.qdbp.able.utils.VerifyTools;

/**
 * Map字段比较
 *
 * @author zhaohuihua
 * @version 181108
 */
public class MapFieldComparator<K, V> implements Comparator<Map<K, V>> {

    /** 字段名 **/
    private final String fieldName;
    /** 升序降序 **/
    private final boolean ascending;
    /** 空值优先级 **/
    private final boolean nullsLow;
    /** FieldValue比较器 **/
    private final Comparator<V> valueComparator;

    /**
     * 构造函数
     * 
     * @param fieldName 字段名
     */
    public MapFieldComparator(String fieldName) {
        this(fieldName, true, true, null);
    }

    /**
     * 构造函数
     * 
     * @param fieldName 字段名
     * @param ascending 升序降序: true=升序, false=降序
     */
    public MapFieldComparator(String fieldName, boolean ascending) {
        this(fieldName, ascending, true, null);
    }

    /**
     * 构造函数
     * 
     * @param fieldName 字段名
     * @param ascending 升序降序: true=升序, false=降序
     * @param nullsLow 空值优先级: true=空值排最后, false=空值排最前
     */
    public MapFieldComparator(String fieldName, boolean ascending, boolean nullsLow) {
        this(fieldName, ascending, nullsLow, null);
    }

    /**
     * 构造函数
     * 
     * @param fieldName 字段名
     * @param valueComparator FieldValue比较器
     */
    public MapFieldComparator(String fieldName, Comparator<V> valueComparator) {
        this(fieldName, true, true, valueComparator);
    }

    /**
     * 构造函数
     * 
     * @param fieldName 字段名
     * @param ascending 升序降序: true=升序, false=降序
     * @param valueComparator FieldValue比较器
     */
    public MapFieldComparator(String fieldName, boolean ascending, Comparator<V> valueComparator) {
        this(fieldName, ascending, true, valueComparator);
    }

    /**
     * 构造函数
     * 
     * @param fieldName 字段名
     * @param ascending 升序降序: true=升序, false=降序
     * @param nullsLow 空值优先级: true=空值排最后, false=空值排最前
     * @param valueComparator FieldValue比较器
     */
    public MapFieldComparator(String fieldName, boolean ascending, boolean nullsLow, Comparator<V> valueComparator) {
        this.fieldName = fieldName;
        this.ascending = ascending;
        this.nullsLow = nullsLow;
        this.valueComparator = valueComparator;
    }

    /** {@inheritDoc} **/
    @Override
    public int compare(Map<K, V> o1, Map<K, V> o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return (this.nullsLow ? -1 : 1);
        }
        if (o2 == null) {
            return (this.nullsLow ? 1 : -1);
        }
        // 获取字段值
        V fieldValue1 = o1.get(fieldName);
        V fieldValue2 = o2.get(fieldName);
        if (fieldValue1 == fieldValue2) {
            return 0;
        }
        if (fieldValue1 == null) {
            return (this.nullsLow ? -1 : 1);
        }
        if (fieldValue2 == null) {
            return (this.nullsLow ? 1 : -1);
        }
        // 对比
        int result;
        if (this.valueComparator != null) {
            result = this.valueComparator.compare(fieldValue1, fieldValue2);
        } else if (fieldValue1 instanceof Comparable) {
            @SuppressWarnings("unchecked")
            Comparable<Object> comparator = (Comparable<Object>) fieldValue1;
            result = comparator.compareTo(fieldValue2);
        } else if (fieldValue2 instanceof Comparable) {
            @SuppressWarnings("unchecked")
            Comparable<Object> comparator = (Comparable<Object>) fieldValue2;
            result = -comparator.compareTo(fieldValue1);
        } else {
            return 0;
        }
        // 升序降序
        return ascending ? result : result * -1;
    }

    /** {@inheritDoc} **/
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result + (ascending ? 1231 : 1237);
        result = prime * result + (nullsLow ? 1231 : 1237);
        result = prime * result + ((valueComparator == null) ? 0 : valueComparator.hashCode());
        return result;
    }

    /** {@inheritDoc} **/
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MapFieldComparator)) {
            return false;
        }
        MapFieldComparator<K, V> o = (MapFieldComparator<K, V>) other;
        if (VerifyTools.notEquals(this.fieldName, o.fieldName)) {
            return false;
        }
        if (this.ascending != o.ascending) {
            return false;
        }
        if (this.nullsLow != o.nullsLow) {
            return false;
        }
        if (VerifyTools.notEquals(this.valueComparator, o.valueComparator)) {
            return false;
        }
        return true;
    }

    /** {@inheritDoc} **/
    @Override
    public String toString() {
        String s = "{class:%s,fieldName:%s,ascending:%s,nullsLow:%s,valueComparator:%s}";
        String className = this.getClass().getSimpleName();
        String valueComparator = this.valueComparator == null ? null : this.valueComparator.getClass().getSimpleName();
        return String.format(s, className, this.ascending, this.nullsLow, valueComparator);
    }
}
