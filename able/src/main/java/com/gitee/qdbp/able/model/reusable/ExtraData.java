package com.gitee.qdbp.able.model.reusable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.gitee.qdbp.tools.utils.ConvertTools;

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

    /** 获取附加数据 **/
    public Map<String, Object> getExtra() {
        return extra;
    }

    /** 设置附加数据 **/
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    /** 是否存在指定的附加数据 **/
    public boolean containsExtra(String key) {
        return this.extra == null ? false : this.extra.containsKey(key);
    }

    /** 获取指定的附加数据 **/
    public Object getExtra(String key) {
        return this.extra == null ? null : this.extra.get(key);
    }

    /** 获取指定类型的附加数据 **/
    public <T> T getExtra(String key, Class<T> clazz) {
        return this.extra == null ? null : ConvertTools.getMapValue(this.extra, key, clazz);
    }

    /** 设置指定的附加数据 **/
    public void putExtra(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.put(key, value);
    }

    /** 设置附加信息 **/
    public void putExtra(Map<String, ?> extra) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.putAll(extra);
    }
}
