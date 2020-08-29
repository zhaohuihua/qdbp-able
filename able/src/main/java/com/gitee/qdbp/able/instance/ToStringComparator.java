package com.gitee.qdbp.able.instance;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 转换为字符串之后进行比较
 *
 * @author zhaohuihua
 * @version 20200823
 * @since 5.1.0
 */
public class ToStringComparator implements Comparator<Object>, Serializable {

    public static final ToStringComparator INSTANCE = new ToStringComparator();

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == o2) {
            return 0;
        } else {
            return o1.toString().compareTo(o2.toString());
        }
    }

}
