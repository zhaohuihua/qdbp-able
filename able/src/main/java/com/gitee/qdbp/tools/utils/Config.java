package com.gitee.qdbp.tools.utils;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import com.gitee.qdbp.able.beans.KeyString;
import com.gitee.qdbp.tools.property.PropertyContainer;
import com.gitee.qdbp.tools.utils.PropertyTools.Options;

/**
 * 加载配置文件<br>
 * 根据文件扩展名决定加载方式, 支持:txt|xml|properties, 其他都按properties处理<br>
 * 支持引用其他配置项<br>
 * key.a = {property:key.b}<br>
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
public class Config implements PropertyContainer, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

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
     * @param path 配置文件路径
     */
    public Config(String path) {
        this(new String[] { path }, null);
    }

    /**
     * 构造函数
     *
     * @param path 配置文件路径
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
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    @Override
    public String getString(String key) {
        return PropertyTools.getString(properties, key, true);
    }

    /**
     * 获取String类型的配置项值(已经trim过了)
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    @Override
    public String getString(String key, boolean throwOnNotFound) {
        return PropertyTools.getString(properties, key, throwOnNotFound);
    }

    /**
     * 获取String类型的配置项值, 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    @Override
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
    @Override
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
    @Override
    public String getStringUseSuffix(String key, String suffixes) {
        return PropertyTools.getStringUseSuffix(properties, key, suffixes);
    }

    /**
     * 获取Long类型的配置项值<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    @Override
    public Long getLong(String key) {
        return PropertyTools.getLong(properties, key, true);
    }

    /**
     * 获取Long类型的配置项值
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    @Override
    public Long getLong(String key, boolean throwOnNotFound) {
        return PropertyTools.getLong(properties, key, throwOnNotFound);
    }

    /**
     * 获取Long类型的配置项值<br>
     * 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    @Override
    public Long getLongUseDefValue(String key, Long defvalue) {
        return PropertyTools.getLongUseDefValue(properties, key, defvalue);
    }

    /**
     * 获取Integer类型的配置项值<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    @Override
    public Integer getInteger(String key) {
        return PropertyTools.getInteger(properties, key, true);
    }

    /**
     * 获取Integer类型的配置项值
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    @Override
    public Integer getInteger(String key, boolean throwOnNotFound) {
        return PropertyTools.getInteger(properties, key, throwOnNotFound);
    }

    /**
     * 获取Integer类型的配置项值<br>
     * 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    @Override
    public Integer getIntegerUseDefValue(String key, Integer defvalue) {
        return PropertyTools.getIntegerUseDefValue(properties, key, defvalue);
    }

    /**
     * 获取Boolean类型的配置项值<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    @Override
    public Boolean getBoolean(String key) {
        return PropertyTools.getBoolean(properties, key, true);
    }

    /**
     * 获取Boolean类型的配置项值
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    @Override
    public Boolean getBoolean(String key, boolean throwOnNotFound) {
        return PropertyTools.getBoolean(properties, key, throwOnNotFound);
    }

    /**
     * 获取Boolean类型的配置项值<br>
     * 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    @Override
    public Boolean getBooleanUseDefValue(String key, Boolean defvalue) {
        return PropertyTools.getBooleanUseDefValue(properties, key, defvalue);
    }

    /**
     * 获取数组类型的配置项值, 以竖杠分隔的字符串拆分为数组<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param key KEY
     * @return VALUE
     */
    @Override
    public String[] getArray(String key) {
        return PropertyTools.getArray(properties, key, true);
    }

    /**
     * 获取数组类型的配置项值, 以竖杠分隔的字符串拆分为数组<br>
     * 每一个子字符串都已经trim()过了<br>
     *
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    @Override
    public String[] getArray(String key, boolean throwOnNotFound) {
        return PropertyTools.getArray(properties, key, throwOnNotFound);
    }

    /**
     * 根据前缀查找所有配置项列表<br>
     * 只有后缀是数字的才会获取, 返回值会根据数字排序<br>
     * 如果值不存在, 将会抛出异常<br>
     * test.items.1 = xxx<br>
     * test.items.2 = yyy<br>
     * test.items.3 = zzz<br>
     * test.items.a = aaa (不会返回)<br>
     * findValueList("test.items") = [xxx,yyy,zzz]
     * 
     * @param keyPrefix KEY前缀, 如果不带分隔符, 会自动加上点(如prefix=prefix.)
     * @return 配置值列表
     */
    @Override
    public List<String> findValueList(String keyPrefix) {
        return PropertyTools.findValueList(properties, keyPrefix, true);
    }

    /**
     * 根据前缀查找所有配置项列表<br>
     * 只有后缀是数字的才会获取, 返回值会根据数字排序<br>
     * test.items.1 = xxx<br>
     * test.items.2 = yyy<br>
     * test.items.3 = zzz<br>
     * test.items.a = aaa (不会返回)<br>
     * findValueList("test.items") = [xxx,yyy,zzz]
     * 
     * @param keyPrefix KEY前缀, 如果不带分隔符, 会自动加上点(如prefix=prefix.)
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return 配置值列表
     */
    @Override
    public List<String> findValueList(String keyPrefix, boolean throwOnNotFound) {
        return PropertyTools.findValueList(properties, keyPrefix, throwOnNotFound);
    }

    /**
     * 获取Class类型的配置值并返回实例化对象
     * 
     * @param <T> 类型
     * @param <S> 子类型
     * @param key KEY
     * @param type 期望返回的类型
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return 配置的实例化对象
     */
    public <T, S extends T> S getClassInstance(String key, Class<T> type, boolean throwOnNotFound) {
        String value = this.getString(key, throwOnNotFound);
        if (VerifyTools.isBlank(value)) {
            return null;
        } else {
            return newClassInstance(key, value, type);
        }
    }

    /**
     * 获取Class类型的配置值并返回实例化对象<br>
     * 如果没有找到配置项, 返回默认类型的实例化对象
     * 
     * @param <T> 类型
     * @param <S> 子类型
     * @param key KEY
     * @param type 期望返回的类型
     * @param deftype 默认类型
     * @return 配置的实例化对象
     */
    public <T, S extends T> S getClassInstance(String key, Class<T> type, Class<S> deftype) {
        String value = this.getString(key, false);
        if (VerifyTools.isNotBlank(value)) {
            return newClassInstance(key, value, type);
        } else {
            if (deftype == null) {
                return null;
            } else {
                try {
                    return deftype.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    String m = "Default class can't instantiate, " + deftype.getName() + ", key=" + key;
                    throw new IllegalStateException(m, e);
                }
            }
        }
    }

    private <T, S extends T> S newClassInstance(String key, String className, Class<T> type) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            String m = "Class not found, " + key + "=" + className;
            throw new IllegalStateException(m, e);
        }
        if (!type.isAssignableFrom(clazz)) {
            String m = "Class isn't expected type, " + key + "=" + className + ", expected: " + type.getName();
            throw new IllegalStateException(m);
        }
        try {
            @SuppressWarnings("unchecked")
            S instance = (S) clazz.newInstance();
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            String m = "Class can't instantiate, " + key + "=" + className;
            throw new IllegalStateException(m, e);
        }
    }

    /**
     * 返回Properties
     *
     * @return Properties
     */
    public Properties properties() {
        return this.properties;
    }

    /**
     * 返回所有配置项条目
     *
     * @return 所有配置项条目
     */
    @Override
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
