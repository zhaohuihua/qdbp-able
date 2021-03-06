package com.gitee.qdbp.tools.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gitee.qdbp.able.beans.KeyString;
import com.gitee.qdbp.able.beans.KeyValue;
import com.gitee.qdbp.able.exception.ResourceNotFoundException;
import com.gitee.qdbp.tools.files.PathTools;

/**
 * 配置文件工具类<br>
 * <b>getXxx(properties, key, ...)方法</b><br>
 * 支持引用其他配置项<br>
 * key.a = {property:key.b}<br>
 * <b>load(...)方法</b><br>
 * 可以通过&lt;&lt;include&gt;&gt;标签导入其他配置文件<br>
 * &lt;&lt;include&gt;&gt; = rules.txt<br>
 * &lt;&lt;include.rules&gt;&gt; = rules.txt<br>
 * &lt;&lt;include.sql&gt;&gt; = ../sql/sql.txt<br>
 * 有顺序要求的, 可带上序号<br>
 * &lt;&lt;include.1&gt;&gt; = rules.txt<br>
 * &lt;&lt;include.2&gt;&gt; = ../sql/sql.txt<br>
 *
 * @author zhaohuihua
 * @version 180424
 */
public abstract class PropertyTools {

    /** 导入其他配置文件 **/
    private static final Pattern INCLUDE = Pattern.compile("^<<(include(\\.\\w+)*)>>$");
    /** 关联配置项的正则表达式(配置项指向另一个配置项) **/
    private static final Pattern REFERENCED = Pattern.compile("\\{(?:property|config)\\:(.*?)\\}");

    /** 默认的文件编码格式 **/
    public static String DEFAULT_CHARSET = "UTF-8";
    /** XML文件扩展名 **/
    private static final String XML = ".xml";
    /** TXT文件扩展名 **/
    private static final String TXT = ".txt";

    /**
     * 获取String类型的配置项值(已经trim过了)<br>
     * 支持引用其他配置项<br>
     * key.a = {property:key.b}<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param properties Properties
     * @param key KEY
     * @return VALUE
     */
    public static String getString(Properties properties, String key) {
        return getString(properties, key, true);
    }

    /**
     * 获取String类型的配置项值(已经trim过了)<br>
     * 支持引用其他配置项<br>
     * key.a = {property:key.b}<br>
     *
     * @param properties Properties
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    public static String getString(Properties properties, String key, boolean throwOnNotFound) {
        return getRealValue(properties, key, throwOnNotFound);
    }

    /**
     * 获取String类型的配置项值, 如果配置项值为null则返回默认值
     *
     * @param properties Properties
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    public static String getStringUseDefValue(Properties properties, String key, String defvalue) {
        String value = getString(properties, key, false);
        return VerifyTools.nvl(value, defvalue);
    }

    /**
     * 获取String类型的配置项值, 如果配置项值为null则继续取备用KEY的值
     *
     * @param properties Properties
     * @param key KEY
     * @param keys 备用KEY
     * @return VALUE
     */
    public static String getStringUseDefKeys(Properties properties, String key, String... keys) {
        String value = getString(properties, key, false);
        if (VerifyTools.isBlank(value) && keys != null && keys.length > 0) {
            for (String k : keys) {
                value = getString(properties, k, false);
                if (VerifyTools.isNotBlank(value)) {
                    break;
                }
            }
        }
        return value;
    }

    /**
     * 以key.suffix1.suffix2的方式逐级取值<br>
     * 如key = a.b.c, suffixes=x.y.z<br>
     * 取值顺序为: a.b.c.x.y.z - a.b.c.x.y - a.b.c.x - a.b.c
     *
     * @param properties Properties
     * @param key KEY
     * @param suffixes 多级后缀
     * @return VALUE
     */
    public static String getStringUseSuffix(Properties properties, String key, String suffixes) {
        if (VerifyTools.isNotBlank(suffixes) && suffixes.startsWith(".")) {
            suffixes = suffixes.substring(1);
        }
        if (VerifyTools.isBlank(suffixes)) {
            return getString(properties, key);
        }

        char dot = '.';
        String first = key + dot + suffixes;
        List<String> keys = new ArrayList<>();
        int index = suffixes.length();
        while (true) {
            index = suffixes.lastIndexOf(dot, index - 1);
            if (index <= 0) {
                break;
            }
            keys.add(key + dot + suffixes.substring(0, index));
        }
        keys.add(key);
        return getStringUseDefKeys(properties, first, ConvertTools.toArray(keys, String.class));
    }

    /**
     * 获取Long类型的配置项值
     *
     * @param properties Properties
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     * @throws NumberFormatException 数字格式错误
     */
    public static Long getLong(Properties properties, String key, boolean throwOnNotFound) {
        String value = getString(properties, key, throwOnNotFound);
        if (VerifyTools.isBlank(value)) {
            return null;
        }

        try {
            return ConvertTools.toLong(value);
        } catch (NumberFormatException e) {
            String m = "Property '" + key + "' format error, '" + value + "' can't convert to number.";
            throw new NumberFormatException(m);
        }
    }

    /**
     * 获取Long类型的配置项值<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param properties Properties
     * @param key KEY
     * @return VALUE
     */
    public static Long getLong(Properties properties, String key) {
        return getLong(properties, key, true);
    }

    /**
     * 获取Long类型的配置项值<br>
     * 如果配置项值为null则返回默认值
     *
     * @param properties Properties
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    public static Long getLongUseDefValue(Properties properties, String key, Long defvalue) {
        Long value = getLong(properties, key, false);
        return VerifyTools.nvl(value, defvalue);
    }

    /**
     * 获取Integer类型的配置项值<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param properties Properties
     * @param key KEY
     * @return VALUE
     */
    public static Integer getInteger(Properties properties, String key) {
        return getInteger(properties, key, true);
    }

    /**
     * 获取Integer类型的配置项值
     *
     * @param properties Properties
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    public static Integer getInteger(Properties properties, String key, boolean throwOnNotFound) {
        Long number = getLong(properties, key, throwOnNotFound);
        return number == null ? null : number.intValue();
    }

    /**
     * 获取Integer类型的配置项值<br>
     * 如果配置项值为null则返回默认值
     *
     * @param properties Properties
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    public static Integer getIntegerUseDefValue(Properties properties, String key, Integer defvalue) {
        Integer value = getInteger(properties, key, false);
        return VerifyTools.nvl(value, defvalue);
    }

    /**
     * 获取Boolean类型的配置项值<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param properties Properties
     * @param key KEY
     * @return VALUE
     */
    public static Boolean getBoolean(Properties properties, String key) {
        return getBoolean(properties, key, true);
    }

    /**
     * 获取Boolean类型的配置项值
     *
     * @param properties Properties
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    public static Boolean getBoolean(Properties properties, String key, boolean throwOnNotFound) {
        String value = getString(properties, key, throwOnNotFound);
        return value == null ? null : StringTools.isPositive(value, false);
    }

    /**
     * 获取Boolean类型的配置项值<br>
     * 如果配置项值为null则返回默认值
     *
     * @param properties Properties
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    public static Boolean getBooleanUseDefValue(Properties properties, String key, Boolean defvalue) {
        Boolean value = getBoolean(properties, key, false);
        return VerifyTools.nvl(value, defvalue);
    }

    /**
     * 获取数组类型的配置项值, 以竖杠分隔的字符串拆分为数组<br>
     * 如果值不存在, 将会抛出异常
     *
     * @param properties Properties
     * @param key KEY
     * @return VALUE
     */
    public static String[] getArray(Properties properties, String key) {
        return getArray(properties, key, true);
    }

    /**
     * 获取数组类型的配置项值, 以竖杠分隔的字符串拆分为数组<br>
     * 每一个子字符串都已经trim()过了<br>
     *
     * @param properties Properties
     * @param key KEY
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return VALUE
     */
    public static String[] getArray(Properties properties, String key, boolean throwOnNotFound) {
        String value = getString(properties, key, throwOnNotFound);
        return value == null ? null : StringTools.split(value);
    }

    /**
     * 返回所有配置项条目
     *
     * @param properties Properties
     * @author zhaohuihua
     * @return 所有配置项条目
     */
    public static List<KeyString> entries(Properties properties) {
        List<KeyString> entries = new ArrayList<>();

        Set<Map.Entry<Object, Object>> original = properties.entrySet();
        for (Map.Entry<Object, Object> entry : original) {
            // 从Properties.getProperty()来看, 只支持字符串
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                entries.add(new KeyString(key, value));
            }
        }
        return entries;
    }

    /**
     * 合并资源文件
     * 
     * @param container 容器
     * @param properties 资源文件
     * @return 返回容器
     */
    public static Properties concat(Properties container, Properties... properties) {
        if (container == null || VerifyTools.isBlank(properties)) {
            return container;
        }
        for (Properties p : properties) {
            container.putAll(p);
        }
        return container;
    }

    /**
     * 根据前缀查找, 得到一个子集
     * 
     * @param original 原集合
     * @param prefixes 前缀, 如果不带分隔符, 会自动加上点(如prefix=prefix.)
     * @return 子集
     */
    public static Properties filter(Properties original, String... prefixes) {
        return filter(original, true, prefixes);
    }

    /**
     * 根据前缀查找, 得到一个子集
     * 
     * @param original 原集合
     * @param cutPrefix 是否截掉前缀
     * @param prefixes 前缀, 带分隔符(如prefix.)
     * @return 子集
     */
    public static Properties filter(Properties original, boolean cutPrefix, String... prefixes) {
        if (original == null) {
            return null;
        }
        Properties p = new Properties();
        if (original.isEmpty() || VerifyTools.isBlank(prefixes)) {
            return p;
        }
        if (prefixes.length == 1) {
            String prefix = prefixes[0].endsWith(".") ? prefixes[0] : prefixes[0] + '.';
            for (String key : original.stringPropertyNames()) {
                if (key.startsWith(prefix)) {
                    p.setProperty(cutPrefix ? key.substring(prefix.length()) : key, original.getProperty(key));
                }
            }
        } else {
            for (String key : original.stringPropertyNames()) {
                for (String item : prefixes) {
                    String prefix = item.endsWith(".") ? item : item + '.';
                    if (key.startsWith(prefix)) {
                        p.setProperty(cutPrefix ? key.substring(prefix.length()) : key, original.getProperty(key));
                    }
                }
            }
        }
        return p;
    }

    /**
     * 根据前缀查找所有配置项列表<br>
     * 只有后缀是数字的才会获取, 返回值会根据数字排序<br>
     * 如果值不存在, 将会抛出异常<br>
     * test.items.1 = xxx<br>
     * test.items.2 = yyy<br>
     * test.items.3 = zzz<br>
     * test.items.a = aaa (不会返回)<br>
     * findValuesByPrefix("test.items") = [xxx,yyy,zzz]
     * 
     * @param properties Properties
     * @param keyPrefix KEY前缀, 如果不带分隔符, 会自动加上点(如prefix=prefix.)
     * @return 配置值列表
     */
    public static List<String> findValueList(Properties properties, String keyPrefix) {
        return findValueList(properties, keyPrefix, true);
    }

    /**
     * 根据前缀查找所有配置项列表<br>
     * 只有后缀是数字的才会获取, 返回值会根据数字排序<br>
     * test.items.1 = xxx<br>
     * test.items.2 = yyy<br>
     * test.items.3 = zzz<br>
     * test.items.a = aaa (不会返回)<br>
     * findValuesByPrefix("test.items") = [xxx,yyy,zzz]
     * 
     * @param properties Properties
     * @param keyPrefix KEY前缀, 如果不带分隔符, 会自动加上点(如prefix=prefix.)
     * @param throwOnNotFound 值不存在时,是否抛出异常
     * @return 配置值列表
     */
    public static List<String> findValueList(Properties properties, String keyPrefix, boolean throwOnNotFound) {
        // 按前缀筛选
        Properties filtered = filter(properties, true, keyPrefix);
        // 再按后缀是不是数字筛选
        List<ListItem> list = new ArrayList<>();
        for (Map.Entry<?, ?> entry : filtered.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof String && value instanceof String) {
                String skey = (String) key;
                String svalue = (String) value;
                if (StringTools.isDigit(skey)) {
                    list.add(new ListItem(Integer.valueOf(skey), svalue));
                }
            }
        }
        if (list.isEmpty()) {
            if (throwOnNotFound) {
                throw new IllegalArgumentException("Property of starts with '" + keyPrefix + "' not found.");
            }
            return new ArrayList<String>();
        }

        // 排序后返回
        Collections.sort(list);
        List<String> values = new ArrayList<>();
        for (ListItem item : list) {
            values.add(item.value);
        }
        return values;
    }

    private static class ListItem implements Comparable<ListItem> {

        int index;
        String value;

        public ListItem(int index, String value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public int compareTo(ListItem o) {
            return this.index - o.index;
        }
    }

    private static String getRealValue(Properties properties, String key, boolean throwOnNotFound) {
        Object value = properties.get(key);
        if (value == null) {
            if (throwOnNotFound) {
                throw new IllegalArgumentException("Property '" + key + "' not found.");
            }
            return null;
        }
        if (!(value instanceof String)) {
            if (throwOnNotFound) {
                String type = value.getClass().getSimpleName();
                throw new IllegalArgumentException("Property '" + key + "' value type is " + type + ".");
            }
            return null;
        }
        return getReferencedValue(properties, key, (String) value, new ArrayList<String>());
    }

    private static String getReferencedValue(Properties properties, String key, String value, List<String> keys) {
        keys.add(key);
        Matcher matcher = REFERENCED.matcher(value);
        StringBuilder buffer = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            buffer.append(value.substring(index, matcher.start()));
            // 配置项内容指向另一个配置项
            String subkey = matcher.group(1).trim();
            if (keys.contains(subkey)) { // 循环引用
                throw new IllegalArgumentException("Cyclic referenced keys: " + keys.toString());
            } else {
                Object subvalue = properties.get(subkey);
                if (subvalue != null) {
                    if (subvalue instanceof String) {
                        buffer.append(getReferencedValue(properties, subkey, (String) subvalue, keys));
                    } else {
                        throw new IllegalArgumentException("Property '" + subkey + "' referenced object.");
                    }
                } else {
                    // 这里已经明确指向的配置项必须存在, 不需要判断throwOnNotFound
                    throw new IllegalArgumentException("Property '" + subkey + "' not found.");
                }
            }
            index = matcher.end();
        }
        if (index == 0) {
            return value;
        } else {
            if (index < value.length()) {
                buffer.append(value.substring(index));
            }
            return buffer.toString();
        }
    }

    /**
     * 加载资源文件
     * 
     * @param text 资源文件内容
     * @return Properties
     */
    public static Properties loadByString(String text) {
        Properties properties = new Properties();
        try (InputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
                InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException("load properties error", e);
        }
        return properties;
    }

    /**
     * 加载path指定的配置文件
     * 
     * @param path 配置文件路径
     * @return 配置文件对象
     */
    public static Properties load(String path) {
        return load(new String[] { path }, DEFAULT_CHARSET, null, null);
    }

    /**
     * 加载path指定的配置文件
     * 
     * @param path 配置文件路径
     * @param encoding 文件编码
     * @param classpaths 查找文件的classpath
     * @return 配置文件对象
     */
    public static Properties load(String path, String encoding, Class<?>... classpaths) {
        return load(new String[] { path }, encoding, null, classpaths);
    }

    /**
     * 加载path指定的配置文件
     * 
     * @param path 配置文件路径
     * @param options 配置文件选项
     * @return 配置文件对象
     */
    public static Properties load(String path, Options options) {
        return load(new String[] { path }, options);
    }

    /**
     * 加载paths指定的多个配置文件<br>
     * 多个配置文件按顺序加载, 有相同key的后加载的覆盖先加载的
     * 
     * @param paths 配置文件路径
     * @param options 配置文件选项
     * @return 配置文件对象
     */
    public static Properties load(String[] paths, Options options) {
        String encoding = options == null ? null : options.getEncoding();
        Filter[] filters = options == null ? null : options.getFilters();
        Class<?>[] classpaths = options == null ? null : options.getClasspaths();
        return load(paths, encoding, filters, classpaths);
    }

    private static Properties load(String[] paths, String encoding, Filter[] filters, Class<?>[] classpaths) {
        URL[] urls = findResource(paths, classpaths);
        return load(urls, encoding, filters, classpaths, null);
    }

    /**
     * 加载url指定的配置文件
     * 
     * @param url 配置文件路径
     * @return 配置文件对象
     */
    public static Properties load(URL url) {
        return load(new URL[] { url }, DEFAULT_CHARSET, null, null, null);
    }

    /**
     * 加载url指定的配置文件
     * 
     * @param url 配置文件路径
     * @param encoding 文件编码
     * @param classpaths 查找文件的classpath, 用于配置文件的include标签
     * @return 配置文件对象
     */
    public static Properties load(URL url, String encoding, Class<?>... classpaths) {
        return load(new URL[] { url }, encoding, null, classpaths, null);
    }

    /**
     * 加载url指定的配置文件
     * 
     * @param url 配置文件路径
     * @param options 配置文件选项
     * @return 配置文件对象
     */
    public static Properties load(URL url, Options options) {
        return load(new URL[] { url }, options);
    }

    /**
     * 加载urls指定的多个配置文件<br>
     * 多个配置文件按顺序加载, 有相同key的后加载的覆盖先加载的
     * 
     * @param urls 配置文件路径
     * @param options 配置文件选项
     * @return 配置文件对象
     */
    public static Properties load(URL[] urls, Options options) {
        String encoding = options == null ? null : options.getEncoding();
        Filter[] filters = options == null ? null : options.getFilters();
        Class<?>[] classpaths = options == null ? null : options.getClasspaths();
        Properties defaults = options == null ? null : options.getDefaults();
        return load(urls, encoding, filters, classpaths, defaults);
    }

    private static Properties load(URL[] urls, String encoding, Filter[] filters, Class<?>[] classpaths,
            Properties defaults) {
        Properties temp = new Properties(defaults);

        for (URL url : urls) {
            load(temp, url, encoding, classpaths);
        }

        Properties properties = new Properties();
        for (Map.Entry<Object, Object> entry : temp.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = PropertyTools.getString(temp, key, true);
                if (filters == null || filters.length == 0) {
                    properties.put(key, value);
                } else {
                    for (Filter filter : filters) {
                        KeyValue<?> newer = filter.filter(new KeyString(key, value));
                        if (newer != null) {
                            properties.put(newer.getKey(), newer.getValue());
                        }
                    }
                }
            }
        }
        return properties;
    }

    private static void load(Properties properties, URL url, String encoding, Class<?>[] classpaths) {
        Properties temp = doLoad(url, encoding);

        List<KeyString> includes = new ArrayList<>();
        for (Entry<Object, Object> entry : temp.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            Matcher m = INCLUDE.matcher(key);
            if (m.matches()) {
                includes.add(new KeyString(m.group(1), value));
            }
        }
        Collections.sort(includes);
        for (KeyString item : includes) {
            String key = item.getKey();
            String value = item.getValue();
            try {
                URL subpath = PathTools.findRelativeResource(url, value, classpaths);
                load(properties, subpath, encoding, classpaths);
            } catch (ResourceNotFoundException e) {
                String path = PathTools.toUriPath(url);
                e.prependMessage("Include file not found. Referenced by [" + path + "]" + key + ". ");
                throw e;
            }
        }
        properties.putAll(temp);
    }

    /** 加载配置文件, 根据文件扩展名决定加载方式, 支持:txt|xml|properties, 其他都按properties处理 **/
    private static Properties doLoad(URL url, String encoding) {
        Charset charset = Charset.forName(VerifyTools.nvl(encoding, DEFAULT_CHARSET));
        String path = PathTools.toUriPath(url);
        String extension = PathTools.getExtension(path).toLowerCase();
        try (InputStream input = url.openStream()) {
            Properties properties = new Properties();
            if (extension.endsWith(TXT)) {
                try (InputStreamReader isr = new InputStreamReader(input, charset)) {
                    // 加载TXT文件, 不作转义处理, 以便正确读取正则表达式
                    doLoadFromTxt(isr, properties);
                } catch (IOException e) {
                    throw new IllegalArgumentException("load text properties error: " + path, e);
                }
            } else if (extension.endsWith(XML)) {
                try {
                    // 加载XML文件
                    properties.loadFromXML(input);
                } catch (IOException e) {
                    throw new IllegalArgumentException("load xml properties error: " + path, e);
                }
            } else {
                try (InputStreamReader isr = new InputStreamReader(input, charset)) {
                    // 加载属性文件
                    properties.load(isr);
                } catch (IOException e) {
                    throw new IllegalArgumentException("load properties error: " + path, e);
                }
            }
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException("load properties error: " + path, e);
        }
    }

    /** 加载TXT文件, 按原文读取,带斜杠的字符不作转义处理 **/
    private static void doLoadFromTxt(Reader reader, Properties properties) throws IOException {

        try (BufferedReader br = new BufferedReader(reader);) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) {
                    continue; // 空行
                }
                String trimed = line.trim();
                if (trimed.startsWith("#") || trimed.startsWith("!")) {
                    continue; // 注释
                }
                int index = trimed.indexOf('=');
                if (index <= 0) {
                    continue; // 没有=号或=号位于行首
                }

                // =号前面的是KEY, 后面的是VALUE
                String key = trimed.substring(0, index).trim();
                String value = trimed.substring(index + 1).trim();
                if (key.length() == 0 && value.length() == 0) {
                    continue; // 没有KEY也没有VALUE
                }

                properties.setProperty(key, value);
            }
        }
    }

    private static URL[] findResource(String[] paths, Class<?>[] classpaths) {
        URL[] urls = new URL[paths.length];
        for (int i = 0; i < paths.length; i++) {
            urls[i] = PathTools.findResource(paths[i], classpaths);
        }
        return urls;
    }

    /** 配置文件内容过滤器 **/
    public static interface Filter {

        /** 过滤操作, 返回null表示丢弃 **/
        KeyString filter(KeyString entry);
    }

    /** 配置文件加载选项 **/
    public static class Options {

        private Properties defaults;
        private String encoding;
        private List<Filter> filters;
        private List<Class<?>> classpaths;

        /** 默认配置 **/
        public Properties getDefaults() {
            return defaults;
        }

        /** 默认配置 **/
        public void setDefaults(Properties defaults) {
            this.defaults = defaults;
        }

        /** 获取编码格式 **/
        public String getEncoding() {
            return encoding;
        }

        /** 设置编码格式 **/
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        /** 获取过滤器 **/
        public Filter[] getFilters() {
            return filters == null ? null : filters.toArray(new Filter[0]);
        }

        /** 设置过滤器 **/
        public void setFilters(Filter... filters) {
            VerifyTools.requireNotBlank(filters, "filters");
            this.filters = Arrays.asList(filters);
        }

        /** 增加过滤器 **/
        public void addFilter(Filter... filters) {
            VerifyTools.requireNotBlank(filters, "filters");
            if (this.filters == null) {
                this.filters = new ArrayList<>();
            }
            this.filters.addAll(Arrays.asList(filters));
        }

        /** 获取查找文件位置的classpath **/
        public Class<?>[] getClasspaths() {
            return classpaths == null ? null : classpaths.toArray(new Class<?>[0]);
        }

        /** 设置查找文件位置的classpath **/
        public void setClasspaths(Class<?>... classpaths) {
            VerifyTools.requireNotBlank(classpaths, "classpaths");
            this.classpaths = Arrays.asList(classpaths);
        }

        /** 增加查找文件位置的classpath **/
        public void addClasspath(Class<?>... classpaths) {
            VerifyTools.requireNotBlank(classpaths, "classpaths");
            if (this.classpaths == null) {
                this.classpaths = new ArrayList<>();
            }
            this.classpaths.addAll(Arrays.asList(classpaths));
        }
    }
}
