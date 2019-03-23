package com.gitee.qdbp.tools.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.gitee.qdbp.able.beans.Duration;

/**
 * 实现默认方法的缓存服务接口
 *
 * @author zhaohuihua
 * @version 170527
 */
public abstract class AbstractCacheService implements ICacheService {

    @Override
    public boolean storable() {
        return false;
    }

    @Override
    public <T> void set(String key, T value) {
        this.set(key, null, value);
    }

    @Override
    public <T> void set(String key, T value, Duration expire) {
        this.set(key, null, value, expire);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return this.get(key, null, clazz);
    }

    @Override
    public <T> List<T> list(String key, Class<T> clazz) {
        return this.list(key, null, clazz);
    }

    @Override
    public boolean exist(String key) {
        return this.exist(key, null);
    }

    @Override
    public void del(String key) {
        this.del(key, null);
    }

    @Override
    public void expire(String key, Duration time) {
        this.expire(key, null, time);
    }

    @Override
    public void persist(String key) {
        this.persist(key, null);
    }

    @Override
    public <T> void hset(String key, String field, T value) {
        this.hset(key, null, field, value);
    }

    @Override
    public <T> T hget(String key, String field, Class<T> clazz) {
        return this.hget(key, null, field, clazz);
    }

    @Override
    public <T> List<T> hlist(String key, String field, Class<T> clazz) {
        return this.hlist(key, null, field, clazz);
    }

    @Override
    public boolean hexist(String key, String field) {
        return this.hexist(key, null, field);
    }

    @Override
    public void hdel(String key, String field) {
        this.hdel(key, null, field);
    }

    @Override
    public <T> void hmset(String key, Map<String, T> map) {
        this.hmset(key, null, map);
    }

    @Override
    public Map<String, String> hmget(String key, List<String> fields) {
        return this.hmget(key, null, fields);
    }

    @Override
    public <T> Map<String, T> hmget(String key, List<String> fields, Class<T> clazz) {
        return this.hmget(key, null, fields, clazz);
    }

    @Override
    public long hmdel(String key, List<String> fields) {
        return this.hmdel(key, null, fields);
    }

    @Override
    public <T> Map<String, T> haget(String key, Class<T> clazz) {
        return this.haget(key, null, clazz);
    }

    @Override
    public Map<String, String> haget(String key) {
        return this.haget(key, (String) null);
    }

    @Override
    public <T> void hoset(String key, T object) {
        this.hoset(key, null, object);
    }

    @Override
    public <T> T hoget(String key, Class<T> clazz) {
        return this.hoget(key, null, clazz);
    }

    @Override
    public Set<String> hkeys(String key) {
        return this.hkeys(key, null);
    }

    @Override
    public int hlen(String key) {
        return this.hlen(key, null);
    }

    public static interface Aware {

        void setCacheService(AbstractCacheService cache);
    }
}
