package com.gitee.zhaohuihua.tools.utils;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import com.gitee.zhaohuihua.core.beans.KeyString;
import com.gitee.zhaohuihua.tools.utils.PropertyTools.Options;

/**
 * 加载配置文件<br>
 * 根据文件扩展名决定加载方式, 支持:txt|xml|properties, 其他都按properties处理<br>
 * 支持引用其他配置项<br>
 * key.a = {config:key.b}<br>
 * 可以通过&lt;&lt;include&gt;&gt;标签导入其他配置文件<br>
 * &lt;&lt;include&gt;&gt; = rules.txt<br>
 * &lt;&lt;include.rules&gt;&gt; = rules.txt<br>
 * &lt;&lt;include.sql&gt;&gt; = ../sql/sql.txt<br>
 * 有顺序要求的, 可带上序号<br>
 * &lt;&lt;include.1&gt;&gt; = rules.txt<br>
 * &lt;&lt;include.2&gt;&gt; = ../sql/sql.txt<br>
 *
 * @author zhaohuihua
 * @version 140724
 */
public class Config implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /**
     * Config设置接口
     *
     * @author zhaohuihua
     * @version 160831
     */
    public static interface Aware {

        void setConfig(Config config);
    }

    /** 配置项容器 **/
    private final Properties properties;

    public Config() {
        this.properties = new Properties();
    }

    public Config(Properties properties) {
        this.properties = properties;
    }

    /**
     * 构造函数
     *
     * @param filePath 配置文件路径
     */
    public Config(String path) {
        this(new String[] { path }, null);
    }

    /**
     * 构造函数
     *
     * @param filePath 配置文件路径
     * @param options 选项
     */
    public Config(String path, Options options) {
        this(new String[] { path }, options);
    }

    /**
     * 构造函数
     *
     * @param paths 配置文件路径列表
     */
    public Config(String[] paths) {
        this(paths, null);
    }

    /**
     * 构造函数
     *
     * @param paths 配置文件路径列表
     * @param encoding 编码格式
     * @param filter 过滤器
     */
    public Config(String[] paths, Options options) {
        this.properties = PropertyTools.load(paths, options);
    }

    /**
     * 构造函数
     *
     * @param url 配置文件路径
     */
    public Config(URL url) {
        this(new URL[] { url }, null);
    }

    /**
     * 构造函数
     *
     * @param url 配置文件路径
     * @param options 选项
     */
    public Config(URL url, Options options) {
        this(new URL[] { url }, options);
    }

    /**
     * 构造函数
     *
     * @param urls 配置文件路径列表
     * @param filter 过滤器
     */
    public Config(URL[] urls) {
        this(urls, null);
    }

    /**
     * 构造函数
     *
     * @param urls 配置文件路径列表
     * @param options 选项
     */
    public Config(URL[] urls, Options options) {
        this.properties = PropertyTools.load(urls, options);
    }

    // 从Properties.getProperty()来看, 只支持字符串
    // (oval instanceof String) ? (String)oval : null
    public void put(String key, String value) {
        this.properties.put(key, value);
    }

    public void put(KeyString... items) {
        for (KeyString i : items) {
            this.properties.put(i.getKey(), i.getValue());
        }
    }

    /**
     * 获取String类型的配置项值(已经trim过了)<br>
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    public String getString(String key) {
        return PropertyTools.getString(properties, key, true);
    }

    /**
     * 获取String类型的配置项值(已经trim过了)
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    public String getString(String key, boolean warning) {
        return PropertyTools.getString(properties, key, warning);
    }

    /**
     * 获取String类型的配置项值, 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    public String getStringUseDefValue(String key, String defvalue) {
        return PropertyTools.getStringUseDefValue(properties, key, defvalue);
    }

    /**
     * 获取String类型的配置项值, 如果配置项值为null则继续取备用KEY的值
     *
     * @param key KEY
     * @param keys 备用KEY
     * @return VALUE
     */
    public String getStringUseDefKeys(String key, String... keys) {
        return PropertyTools.getStringUseDefKeys(properties, key, keys);
    }

    /**
     * 以key.suffix1.suffix2的方式逐级取值<br>
     * 如key = a.b.c, suffixes=x.y.z<br>
     * 取值顺序为: a.b.c.x.y.z - a.b.c.x.y - a.b.c.x - a.b.c
     *
     * @param key KEY
     * @param suffixes 多级后缀
     * @return VALUE
     */
    public String getStringUseSuffix(String key, String suffixes) {
        return PropertyTools.getStringUseSuffix(properties, key, suffixes);
    }

    /**
     * 获取Integer类型的配置项值<br>
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    public Long getLong(String key) {
        return PropertyTools.getLong(properties, key, true);
    }

    /**
     * 获取Long类型的配置项值
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    public Long getLong(String key, boolean warning) {
        return PropertyTools.getLong(properties, key, warning);
    }

    /**
     * 获取Integer类型的配置项值<br>
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    public Integer getInteger(String key) {
        return PropertyTools.getInteger(properties, key, true);
    }

    /**
     * 获取Integer类型的配置项值
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    public Integer getInteger(String key, boolean warning) {
        return PropertyTools.getInteger(properties, key, warning);
    }

    /**
     * 获取Boolean类型的配置项值<br>
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    public Boolean getBoolean(String key) {
        return PropertyTools.getBoolean(properties, key, true);
    }

    /**
     * 获取Boolean类型的配置项值
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    public Boolean getBoolean(String key, boolean warning) {
        return PropertyTools.getBoolean(properties, key, warning);
    }

    /**
     * 获取数组类型的配置项值, 以竖杠分隔的字符串拆分为数组<br>
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    public String[] getArray(String key) {
        return PropertyTools.getArray(properties, key, true);
    }

    /**
     * 获取数组类型的配置项值, 以竖杠分隔的字符串拆分为数组<br>
     * 每一个子字符串都已经trim()过了<br>
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    public String[] getArray(String key, boolean warning) {
        return PropertyTools.getArray(properties, key, warning);
    }

    /**
     * 返回所有配置项条目
     *
     * @author zhaohuihua
     * @return 所有配置项条目
     */
    public List<KeyString> entries() {
        return PropertyTools.entries(properties);
    }

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return properties.isEmpty();
    }
}
