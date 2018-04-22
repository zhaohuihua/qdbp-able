package com.gitee.zhaohuihua.tools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitee.zhaohuihua.core.beans.KeyString;
import com.gitee.zhaohuihua.core.beans.KeyValue;
import com.gitee.zhaohuihua.core.exception.ResourceNotFoundException;
import com.gitee.zhaohuihua.tools.files.PathTools;

/**
 * 加载配置文件<br>
 * 根据文件扩展名决定加载方式, 支持:txt|xml|properties, 其他都按properties处理<br>
 * 支持引用其他配置项<br>
 * key.a = {config:key.b}<br>
 * 可以通过<<include>>标签导入其他配置文件<br>
 * <<include>> = rules.txt<br>
 * <<include.rules>> = rules.txt<br>
 * <<include.sql>> = ../sql/sql.txt<br>
 * 有顺序要求的, 可带上序号<br>
 * <<include.1>> = rules.txt<br>
 * <<include.2>> = ../sql/sql.txt<br>
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

    /** 日志对象 **/
    private static Logger log = LoggerFactory.getLogger(Config.class);

    /** 导入其他配置文件 **/
    private static final Pattern INCLUDE = Pattern.compile("^<<(include(\\.\\w+)*)>>$");

    /** 关联配置项的正则表达式(配置项指向另一个配置项) **/
    private static final Pattern REFERENCED = Pattern.compile("\\{config\\:(.*?)\\}");

    /** 默认的文件编码格式 **/
    private static String CHARSET = "UTF-8";

    /** XML文件扩展名 **/
    private static final String XML = ".xml";

    /** TXT文件扩展名 **/
    private static final String TXT = ".txt";

    /** 配置项容器 **/
    private final Properties map;

    public Config() {
        this.map = new Properties();
    }

    public Config(Properties properties) {
        this.map = properties;
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
        this.map = load(paths, options);
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
        this.map = load(urls, options);
    }

    public static Properties load(URL url, String encoding) {
        Options options = new Options();
        options.setEncoding(encoding);
        return load(new URL[] { url }, options);
    }

    private static Properties load(String[] paths, Options options) {
        Class<?>[] classpaths = options == null ? null : options.getClasspaths();
        URL[] urls = toUrls(paths, classpaths);
        return load(urls, options);
    }

    private static Properties load(URL[] urls, Options options) {
        String encoding = options == null ? null : options.getEncoding();
        Filter[] filters = options == null ? null : options.getFilters();
        Class<?>[] classpaths = options == null ? null : options.getClasspaths();
        return load(urls, encoding, filters, classpaths);
    }

    private static Properties load(URL[] urls, String encoding, Filter[] filters, Class<?>[] classpaths) {
        Properties temp = new Properties();

        for (URL url : urls) {
            load(temp, url, encoding, classpaths);
        }

        Properties properties = new Properties();
        for (Map.Entry<Object, Object> entry : temp.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                String key = (String) entry.getKey();
                String value = getRealValue(temp, key, true);
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

    private static void load(Properties config, URL url, String encoding, Class<?>[] classpaths) {
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
                load(config, subpath, encoding, classpaths);
            } catch (ResourceNotFoundException e) {
                String path = PathTools.toUriPath(url);
                e.prependMessage("Include file not found. Referenced by [" + path + "]" + key + ". ");
                throw e;
            }
        }
        config.putAll(temp);
    }

    /** 加载配置文件, 根据文件扩展名决定加载方式, 支持:txt|xml|properties, 其他都按properties处理 **/
    private static Properties doLoad(URL url, String encoding) {
        Charset charset = Charset.forName(VerifyTools.nvl(encoding, CHARSET));
        String path = PathTools.toUriPath(url);
        String extension = PathTools.getExtension(path).toLowerCase();
        try (InputStream input = url.openStream()) {
            Properties properties = new Properties();
            if (extension.endsWith(TXT)) {
                try (InputStreamReader isr = new InputStreamReader(input, charset)) {
                    // 加载TXT文件, 不作转义处理, 以便正确读取正则表达式
                    doLoadFromTxt(isr, properties);
                } catch (IOException e) {
                    throw new IllegalArgumentException("load properties config error: " + path, e);
                }
            } else if (extension.endsWith(XML)) {
                try {
                    // 加载XML文件
                    properties.loadFromXML(input);
                } catch (IOException e) {
                    throw new IllegalArgumentException("load xml config error: " + path, e);
                }
            } else {
                try (InputStreamReader isr = new InputStreamReader(input, charset)) {
                    // 加载属性文件
                    properties.load(isr);
                } catch (IOException e) {
                    throw new IllegalArgumentException("load properties config error: " + path, e);
                }
            }
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException("load properties config error: " + path, e);
        }
    }

    /** 加载TXT文件, 按原文读取,带斜杠的字符不作转义处理 **/
    private static void doLoadFromTxt(Reader reader, Properties config) throws IOException {

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

                config.setProperty(key, value);
            }
        }
    }

    private static URL[] toUrls(String[] paths, Class<?>[] classpaths) {
        URL[] urls = new URL[paths.length];
        for (int i = 0; i < paths.length; i++) {
            urls[i] = PathTools.findResource(paths[i], classpaths);
        }
        return urls;
    }

    // 从Properties.getProperty()来看, 只支持字符串
    // (oval instanceof String) ? (String)oval : null
    public void put(String key, String value) {
        this.map.put(key, value);
    }

    public void put(KeyString... items) {
        for (KeyString i : items) {
            this.map.put(i.getKey(), i.getValue());
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
        return getString(key, true);
    }

    /**
     * 获取String类型的配置项值(已经trim过了)
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    public String getString(String key, boolean warning) {
        return getRealValue(map, key, warning);
    }

    /**
     * 获取String类型的配置项值, 如果配置项值为null则返回默认值
     *
     * @param key KEY
     * @param defvalue 默认值
     * @return VALUE
     */
    public String getStringUseDefValue(String key, String defvalue) {
        String value = getString(key, false);
        return VerifyTools.nvl(value, defvalue);
    }

    /**
     * 获取String类型的配置项值, 如果配置项值为null则继续取备用KEY的值
     *
     * @param key KEY
     * @param keys 备用KEY
     * @return VALUE
     */
    public String getStringUseDefKeys(String key, String... keys) {
        String value = getString(key, false);
        if (VerifyTools.isBlank(value) && keys != null && keys.length > 0) {
            for (String k : keys) {
                value = getString(k, false);
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
     * @param key KEY
     * @param suffixes 多级后缀
     * @return VALUE
     */
    public String getStringUseSuffix(String key, String suffixes) {
        if (VerifyTools.isNotBlank(suffixes) && suffixes.startsWith(".")) {
            suffixes = suffixes.substring(1);
        }
        if (VerifyTools.isBlank(suffixes)) {
            return getString(key);
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
        return getStringUseDefKeys(first, ConvertTools.toArray(keys, String.class));
    }

    /**
     * 获取Integer类型的配置项值<br>
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    public Long getLong(String key) {
        return getLong(key, true);
    }

    /**
     * 获取Long类型的配置项值
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    public Long getLong(String key, boolean warning) {
        String value = getString(key, warning);
        if (VerifyTools.isBlank(value)) {
            return null;
        }

        try {
            return ConvertTools.toLong(value);
        } catch (NumberFormatException e) {
            log.warn("Config '{}' format error, '{}' can't convert to integer.", key, value);
            return null;
        }
    }

    /**
     * 获取Integer类型的配置项值<br>
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    public Integer getInteger(String key) {
        return getInteger(key, true);
    }

    /**
     * 获取Integer类型的配置项值
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    public Integer getInteger(String key, boolean warning) {
        Long number = getLong(key, warning);
        return number == null ? null : number.intValue();
    }

    /**
     * 获取Boolean类型的配置项值<br>
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    public Boolean getBoolean(String key) {
        return getBoolean(key, true);
    }

    /**
     * 获取Boolean类型的配置项值
     *
     * @param key KEY
     * @param warning 值不存在时,是否输出警告日志
     * @return VALUE
     */
    public Boolean getBoolean(String key, boolean warning) {
        String value = getString(key, warning);
        return value == null ? null : StringTools.isPositive(value, false);
    }

    /**
     * 获取数组类型的配置项值, 以竖杠分隔的字符串拆分为数组<br>
     * 如果值不存在, 将输出警告日志
     *
     * @param key KEY
     * @return VALUE
     */
    public String[] getArray(String key) {
        return getArray(key, true);
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
        String value = getString(key, warning);
        return value == null ? null : StringTools.split(value);
    }

    /**
     * 是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * 返回所有配置项条目
     *
     * @author zhaohuihua
     * @return 所有配置项条目
     */
    public List<KeyString> entries() {
        List<KeyString> entries = new ArrayList<>();

        Set<Map.Entry<Object, Object>> original = map.entrySet();
        for (Map.Entry<Object, Object> entry : original) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            // 从Properties.getProperty()来看, 只支持字符串
            if (key instanceof String && value instanceof String) {
                entries.add(new KeyString(key, value));
            }
        }
        return entries;
    }

    public static String getRealValue(Properties config, String key, boolean warning) {
        Object value = config.get(key);
        if (value == null) {
            if (warning) {
                log.warn("Config '{}' not found.", key);
            }
            return null;
        }
        if (!(value instanceof String)) {
            if (warning) {
                log.warn("Config '{}' value type is {}.", key, value.getClass().getSimpleName());
            }
            return null;
        }
        return getReferencedValue(config, key, (String) value, new ArrayList<String>());
    }

    private static String getReferencedValue(Properties config, String key, String value, List<String> keys) {
        keys.add(key);
        Matcher matcher = REFERENCED.matcher(value);
        StringBuilder buffer = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            buffer.append(value.substring(index, matcher.start()));
            // 配置项内容指向另一个配置项
            String subkey = matcher.group(1).trim();
            if (keys.contains(subkey)) { // 循环引用
                log.error("Cyclic referenced keys: {}.", keys.toString());
            } else {
                Object subvalue = config.get(subkey);
                if (subvalue != null) {
                    if (subvalue instanceof String) {
                        buffer.append(getReferencedValue(config, subkey, (String) subvalue, keys));
                    } else {
                        log.warn("Config '{}' referenced object.", subkey);
                    }
                } else {
                    // 这里已经明确指向的配置项必须存在, 不需要判断warning
                    log.warn("Config '{}' not found.", subkey);
                }
            }
            index = matcher.end();
        }
        if (index == 0) {
            return value;
        } else {
            buffer.append(value.substring(index));
            return buffer.toString();
        }
    }

    public static interface Filter {

        KeyString filter(KeyString entry);
    }

    public static class Options {

        private String encoding;

        private List<Filter> filters;
        private List<Class<?>> classpaths;

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public Filter[] getFilters() {
            return filters == null ? null : filters.toArray(new Filter[0]);
        }

        public void setFilters(Filter... filters) {
            this.filters = Arrays.asList(filters);
        }

        public void addFilter(Filter... filters) {
            if (this.filters == null) {
                this.filters = new ArrayList<>();
            }
            this.filters.addAll(Arrays.asList(filters));
        }

        public Class<?>[] getClasspaths() {
            return classpaths == null ? null : classpaths.toArray(new Class<?>[0]);
        }

        public void setClasspaths(Class<?>... classpaths) {
            this.classpaths = Arrays.asList(classpaths);
        }

        public void addClasspath(Class<?>... classpaths) {
            if (this.classpaths == null) {
                this.classpaths = new ArrayList<>();
            }
            this.classpaths.addAll(Arrays.asList(classpaths));
        }
    }
}
