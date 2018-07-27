package com.gitee.qdbp.able.model.reusable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 附加数据
 *
 * @author zhaohuihua
 * @version 151020
 */
public class ExtraData implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 附加数据 **/
    private Map<String, Object> extra;

    /** 附加数据 **/
    public Map<String, Object> getExtra() {
        return extra;
    }

    /** 附加数据 **/
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    /** 附加数据 **/
    public Object get(String key) {
        return this.extra == null ? null : this.extra.get(key);
    }

    /** 附加数据 **/
    public void put(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.put(key, value);
    }

    /** 设置附加信息 **/
    public void put(Map<String, ?> extra) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.putAll(extra);
    }
}
