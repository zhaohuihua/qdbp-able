package com.gitee.qdbp.tools.property;

import java.util.List;
import com.gitee.qdbp.able.beans.KeyString;

/**
 * 配置项容器
 *
 * @author zhaohuihua
 * @version 200128
 */
public interface PropertyContainer {

    /**
     * 获取String类型的配置项值(已经trim过了)<br>
     * 支持引用其他配置项<br>
     * key.a = {property:key.b}<br>
     * 如果值不存在, 将输出警告日志
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
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    String getString(String key, boolean warning);

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
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    Long getLong(String key);

    /**
     * 获取Long类型的配置项值
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    Long getLong(String key, boolean warning);

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
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    Integer getInteger(String key);

    /**
     * 获取Integer类型的配置项值
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    Integer getInteger(String key, boolean warning);

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
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    Boolean getBoolean(String key);

    /**
     * 获取Boolean类型的配置项值
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    Boolean getBoolean(String key, boolean warning);

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
     * 如果值不存在, 将输出警告日志
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
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    String[] getArray(String key, boolean warning);

    /**
     * 返回所有配置项条目
     *
     * @return 所有配置项条目
     */
    List<KeyString> entries();
}
