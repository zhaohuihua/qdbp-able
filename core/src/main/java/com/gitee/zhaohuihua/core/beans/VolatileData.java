package com.gitee.zhaohuihua.core.beans;

/**
 * 带有过期时间的数据
 *
 * @author zhaohuihua
 * @version 180223
 */
public class VolatileData<T> {

    // 过期时间, null表示永不过期
    private Long expireTime = null;
    private T value;

    public VolatileData() {
    }

    public VolatileData(T value) {
        this.value = value;
    }

    public VolatileData<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public T getValue() {
        return this.value;
    }

    // 设置过期时间(相对时间)
    public VolatileData<T> expire(Long expire) {
        if (expire != null) {
            this.expireTime = System.currentTimeMillis() + expire;
        }
        return this;
    }

    // 移除过期时间
    public VolatileData<T> persist() {
        this.expireTime = null;
        return this;
    }

    public boolean expired() {
        return this.expireTime == null ? false : this.expireTime < System.currentTimeMillis();
    }
}
