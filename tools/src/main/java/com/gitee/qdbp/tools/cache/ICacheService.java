package com.gitee.qdbp.tools.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.gitee.qdbp.able.beans.Duration;

/**
 * 缓存服务接口
 *
 * @author zhaohuihua
 * @version 170527
 */
public interface ICacheService {

    /** 是否可持久化 **/
    boolean storable();

    /**
     * 保存对象
     *
     * @param key 关键字
     * @param value 值
     */
    <T> void set(String key, T value);

    /**
     * 保存对象
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param value 值
     */
    <T> void set(String key, String subkey, T value);

    /**
     * 保存对象
     *
     * @param key 关键字
     * @param value 值
     * @param expire 过期时间
     */
    <T> void set(String key, T value, Duration expire);

    /**
     * 保存对象
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param value 值
     * @param expire 过期时间
     */
    <T> void set(String key, String subkey, T value, Duration expire);

    /**
     * 从缓存中取出对象
     *
     * @param key 关键字
     * @param clazz 对象类型
     * @return 缓存对象
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 从缓存中取出对象
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param clazz 对象类型
     * @return 缓存对象
     */
    <T> T get(String key, String subkey, Class<T> clazz);

    /**
     * 从缓存中取出对象
     *
     * @param key 关键字
     * @param clazz 对象类型
     * @return 缓存对象
     */
    <T> List<T> list(String key, Class<T> clazz);

    /**
     * 从缓存中取出对象
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param clazz 对象类型
     * @return 缓存对象
     */
    <T> List<T> list(String key, String subkey, Class<T> clazz);

    /**
     * KEY是否存在
     *
     * @param key 关键字
     */
    boolean exist(String key);

    /**
     * KEY是否存在
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param field 字段
     */
    boolean exist(String key, String subkey);

    /**
     * 删除KEY
     *
     * @param key 关键字
     */
    void del(String key);

    /**
     * 删除KEY
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param field 字段
     */
    void del(String key, String subkey);

    /**
     * 设置过期时间
     *
     * @param key 关键字
     * @param time 过期时间
     */
    void expire(String key, Duration time);

    /**
     * 设置过期时间
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param time 过期时间
     */
    void expire(String key, String subkey, Duration time);

    /**
     * 移除过期时间
     *
     * @param key 关键字
     */
    void persist(String key);

    /**
     * 移除过期时间
     *
     * @param key 关键字
     * @param subkey 子关键字
     */
    void persist(String key, String subkey);

    /**
     * 保存哈希表指定字段值
     *
     * @param key 关键字
     * @param field 字段
     * @param value 值
     */
    <T> void hset(String key, String field, T value);

    /**
     * 保存哈希表指定字段值
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param field 字段
     * @param value 值
     */
    <T> void hset(String key, String subkey, String field, T value);

    /**
     * 从哈希表取出指定字段值
     *
     * @param key 关键字
     * @param field 字段
     * @param clazz 对象类型
     * @return 缓存对象
     */
    <T> T hget(String key, String field, Class<T> clazz);

    /**
     * 从哈希表取出指定字段值
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param field 字段
     * @param clazz 对象类型
     * @return 缓存对象
     */
    <T> T hget(String key, String subkey, String field, Class<T> clazz);

    /**
     * 从哈希表取出指定字段值
     *
     * @param key 关键字
     * @param field 字段
     * @param clazz 对象类型
     * @return 缓存对象
     */
    <T> List<T> hlist(String key, String field, Class<T> clazz);

    /**
     * 从哈希表取出指定字段值
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param field 字段
     * @param clazz 对象类型
     * @return 缓存对象
     */
    <T> List<T> hlist(String key, String subkey, String field, Class<T> clazz);

    /**
     * 判断哈希表是否存在field
     *
     * @param key 关键字
     * @param field 字段
     * @param clazz 对象类型
     * @return 是否存在
     */
    boolean hexist(String key, String field);

    /**
     * 判断哈希表是否存在field
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param field 字段
     * @param clazz 对象类型
     * @return 是否存在
     */
    boolean hexist(String key, String subkey, String field);

    /**
     * 删除哈希表指定字段
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param field 字段
     */
    void hdel(String key, String field);

    /**
     * 删除哈希表指定字段
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param field 字段
     */
    void hdel(String key, String subkey, String field);

    /**
     * 保存哈希表指定字段值
     *
     * @param key 关键字
     * @param map 同时设置多个字段
     */
    <T> void hmset(String key, Map<String, T> map);

    /**
     * 保存哈希表指定字段值
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param map 同时设置多个字段
     */
    <T> void hmset(String key, String subkey, Map<String, T> map);

    /**
     * 从哈希表取出指定字段值
     *
     * @param key 关键字
     * @param fields 同时获取多个字段, 如果不指定将获取全部字段
     * @return 字段KeValue, value=JSON字符串
     */
    Map<String, String> hmget(String key, List<String> fields);

    /**
     * 从哈希表取出指定字段值
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param fields 同时获取多个字段, 如果不指定将获取全部字段
     * @return 字段KeValue, value=JSON字符串
     */
    Map<String, String> hmget(String key, String subkey, List<String> fields);

    /**
     * 从哈希表取出指定字段值
     *
     * @param key 关键字
     * @param fields 同时获取多个字段, 如果不指定将获取全部字段
     * @param clazz 字段值类型
     * @return 字段KeValue
     */
    <T> Map<String, T> hmget(String key, List<String> fields, Class<T> clazz);

    /**
     * 从哈希表取出指定字段值
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param fields 同时获取多个字段, 如果不指定将获取全部字段
     * @param clazz 字段值类型
     * @return 字段KeValue
     */
    <T> Map<String, T> hmget(String key, String subkey, List<String> fields, Class<T> clazz);

    /**
     * 删除哈希表指定字段值
     *
     * @param key 关键字
     * @param field 字段
     * @param fields 同时删除多个字段
     * @return 成功删除的数量
     */
    long hmdel(String key, List<String> fields);

    /**
     * 删除哈希表指定字段值
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param field 字段
     * @param fields 同时删除多个字段
     * @return 成功删除的数量
     */
    long hmdel(String key, String subkey, List<String> fields);

    /**
     * 整个哈希表的value类型相同, 一次性全部取出来
     * 
     * @param key 关键字
     * @param clazz 对象类型
     * @return 全部KeyValue
     */
    <T> Map<String, T> haget(String key, Class<T> clazz);

    /**
     * 整个哈希表的value类型相同, 一次性全部取出来
     * 
     * @param key 关键字
     * @param subkey 子关键字
     * @param clazz 对象类型
     * @return 全部KeyValue
     */
    <T> Map<String, T> haget(String key, String subkey, Class<T> clazz);

    /**
     * 整个哈希表的value类型相同, 一次性全部取出来
     * 
     * @param key 关键字
     * @return 全部KeyValue, value=JSON字符串
     */
    Map<String, String> haget(String key);

    /**
     * 整个哈希表的value类型相同, 一次性全部取出来
     * 
     * @param key 关键字
     * @param subkey 子关键字
     * @return 全部KeyValue, value=JSON字符串
     */
    Map<String, String> haget(String key, String subkey);

    /**
     * 保存哈希表对象(将整个对象按字段分别存储到缓存中, 之后以hget/hset/hdel的方式分别对字段操作)
     *
     * @param key 关键字
     * @param value 值
     */
    <T> void hoset(String key, T object);

    /**
     * 保存哈希表对象(将整个对象按字段分别存储到缓存中, 之后以hget/hset/hdel的方式分别对字段操作)
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param value 值
     */
    <T> void hoset(String key, String subkey, T object);

    /**
     * 将整个哈希表还原为对象
     *
     * @param key 关键字
     * @param clazz 对象类型
     * @return Map还原后的对象
     */
    <T> T hoget(String key, Class<T> clazz);

    /**
     * 将整个哈希表还原为对象
     *
     * @param key 关键字
     * @param subkey 子关键字
     * @param clazz 对象类型
     * @return Map还原后的对象
     */
    <T> T hoget(String key, String subkey, Class<T> clazz);

    /**
     * 获取所有的KEY
     * 
     * @param key 关键字
     * @return KEY列表
     */
    Set<String> hkeys(String key);

    /**
     * 获取所有的KEY
     * 
     * @param key 关键字
     * @param subkey 子关键字
     * @return KEY列表
     */
    Set<String> hkeys(String key, String subkey);

    /**
     * 获取哈希表的field数量
     * 
     * @param key 关键字
     * @return 数量
     */
    int hlen(String key);

    /**
     * 获取哈希表的field数量
     * 
     * @param key 关键字
     * @param subkey 子关键字
     * @return 数量
     */
    int hlen(String key, String subkey);

    /**
     * 缓存服务设置接口
     *
     * @author zhaohuihua
     * @version 170527
     */
    public static interface Aware {

        void setCacheService(ICacheService cache);
    }
}
