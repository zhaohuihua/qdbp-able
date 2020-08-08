package com.gitee.qdbp.tools.property;

import java.util.List;
import com.gitee.qdbp.able.beans.KeyString;

/**
 * 配置项容器
 *
 * @author zhaohuihua
 * @version 160831
 */
public interface PropertyContainer {

    /**
     * PropertyContainer设置接口
     *
     * @author zhaohuihua
     * @version 160831
     */
    public static interface Aware {

        void setPropertyContainer(PropertyContainer config);
    }

    /**
     * 获取String类型的配置项值(已经trim过了)<br>
     * 支持引用其他配置项<br>
     * key.a = {property:key.b}<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    String getString(String key);

    /**
     * 获取String类型的配置项值(已经trim过了)<br>
     * 支持引用其他配置项<br>
     * key.a = {property:key.b}<br>
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    String getString(String key, boolean throwOnNotFound);

    /**
     * 获取String类型的配置项值, 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    String getStringUseDefValue(String key, String defvalue);

    /**
     * 获取String类型的配置项值, 如果配置项值为null则继续取备用KEY的值
     *
     * @param key KEY
     * @param keys 备用KEY
     * @return VALUE
     */
    String getStringUseDefKeys(String key, String... keys);

    /**
     * 以key.suffix1.suffix2的方式逐级取值<br>
     * 如key = a.b.c, suffixes=x.y.z<br>
     * 取值顺序为: a.b.c.x.y.z - a.b.c.x.y - a.b.c.x - a.b.c
     *
     * @param key KEY
     * @param suffixes 多级后缀
     * @return VALUE
     */
    String getStringUseSuffix(String key, String suffixes);

    /**
     * 获取Integer类型的配置项值<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    Long getLong(String key);

    /**
     * 获取Long类型的配置项值
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    Long getLong(String key, boolean throwOnNotFound);

    /**
     * 获取Long类型的配置项值<br>
     * 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    Long getLongUseDefValue(String key, Long defvalue);

    /**
     * 获取Integer类型的配置项值<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    Integer getInteger(String key);

    /**
     * 获取Integer类型的配置项值
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    Integer getInteger(String key, boolean throwOnNotFound);

    /**
     * 获取Integer类型的配置项值<br>
     * 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    Integer getIntegerUseDefValue(String key, Integer defvalue);

    /**
     * 获取Boolean类型的配置项值<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    Boolean getBoolean(String key);

    /**
     * 获取Boolean类型的配置项值
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    Boolean getBoolean(String key, boolean throwOnNotFound);

    /**
     * 获取Boolean类型的配置项值<br>
     * 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    Boolean getBooleanUseDefValue(String key, Boolean defvalue);

    /**
     * 获取数组类型的配置项值, 以竖杠分隔的字符串拆分为数组<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    String[] getArray(String key);

    /**
     * 获取数组类型的配置项值, 以竖杠分隔的字符串拆分为数组<br>
     * 每一个子字符串都已经trim()过了<br>
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    String[] getArray(String key, boolean throwOnNotFound);

    /**
     * 根据前缀查找所有配置项列表<br>
     * 只有后缀是数字的才会获取, 返回值会根据数字排序<br>
     * test.items.1 = xxx<br>
     * test.items.2 = yyy<br>
     * test.items.3 = zzz<br>
     * test.items.a = aaa (不会返回)<br>
     * findValuesByPrefix("test.items") = [xxx,yyy,zzz]
     * 
     * @param keyPrefix KEY前缀, 如果不带分隔符, 会自动加上点(如prefix=prefix.)
     * @return 配置值列表
     */
    List<String> findValueList(String keyPrefix);

    /**
     * 根据前缀查找所有配置项列表<br>
     * 只有后缀是数字的才会获取, 返回值会根据数字排序<br>
     * test.items.1 = xxx<br>
     * test.items.2 = yyy<br>
     * test.items.3 = zzz<br>
     * test.items.a = aaa (不会返回)<br>
     * findValuesByPrefix("test.items") = [xxx,yyy,zzz]
     * 
     * @param keyPrefix KEY前缀, 如果不带分隔符, 会自动加上点(如prefix=prefix.)
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return 配置值列表
     */
    List<String> findValueList(String keyPrefix, boolean throwOnNotFound);

    /**
     * 返回所有配置项条目
     *
     * @return 所有配置项条目
     */
    List<KeyString> entries();
}
