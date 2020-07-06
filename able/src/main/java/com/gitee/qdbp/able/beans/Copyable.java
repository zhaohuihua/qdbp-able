package com.gitee.qdbp.able.beans;

/**
 * 可克隆接口<br>
 * Cloneable接口为何没有clone()方法?<br>
 * 判断了object instanceof Cloneable之后, 依然无法调用clone来克隆新对象, 要这个接口何用
 *
 * @author zhaohuihua
 * @version 20200707
 */
public interface Copyable {

    /**
     * 克隆为新对象
     * 
     * @return 新对象
     */
    Object copy();
}
