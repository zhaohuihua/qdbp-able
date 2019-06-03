package com.gitee.qdbp.tools.http;

import java.io.Serializable;
import java.net.URL;
import java.util.Properties;
import com.gitee.qdbp.tools.files.PathTools;
import com.gitee.qdbp.tools.utils.Config;
import com.gitee.qdbp.tools.utils.StringTools;

/**
 * URL配置项工具类
 *
 * @author zhaohuihua
 * @version 150923
 */
public class HostUrlConfig extends Config implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 主机URL **/
    protected String host;

    public HostUrlConfig() {
        super();
    }

    /**
     * 构造函数
     *
     * @param properties 配置文件
     * @param host 主机配置项KEY
     */
    public HostUrlConfig(Properties properties, String host) {
        super(properties);
        if (StringTools.isUrl(host)) {
            this.host = host;
        } else {
            this.host = this.getString(host);
        }
    }

    /**
     * 构造函数
     *
     * @param config 配置文件路径
     * @param host 主机配置项KEY
     */
    public HostUrlConfig(URL config, String host) {
        super(config);
        if (StringTools.isUrl(host)) {
            this.host = host;
        } else {
            this.host = this.getString(host);
        }
    }

    /** 主机URL **/
    public String getHost() {
        return host;
    }

    /** 主机URL **/
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 获取配置的URL
     *
     * @param key KEY
     * @return
     */
    public HttpUrl getUrl(String key) {
        String value = this.getString(key);
        if (value == null) {
            return null;
        }
        HttpMethod method = HttpMethod.GET;
        for (HttpMethod m : HttpMethod.values()) {
            if (value.startsWith(m.name())) {
                value = value.substring(m.name().length()).trim();
                method = m;
            }
        }
        if (!value.startsWith("http://")) {
            value = PathTools.concat(host, value);
        }
        return new KeyedHttpUrl(key, method, value);
    }

    public static class KeyedHttpUrl extends HttpUrl {

        private String key;

        protected KeyedHttpUrl(String key, HttpMethod method, String url) {
            super(method, url);
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }
}
