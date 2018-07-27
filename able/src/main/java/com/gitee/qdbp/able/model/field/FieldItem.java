package com.gitee.qdbp.able.model.field;

import java.io.Serializable;

/**
 * 字段项
 *
 * @author zhaohuihua
 * @version 180503
 */
public class FieldItem implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 字段名 **/
    private String name;
    /** 字段描述 **/
    private String text;

    public FieldItem() {
    }

    public FieldItem(String name, String text) {
        this.name = name;
        this.text = text;
    }

    /** 字段名 **/
    public String getName() {
        return name;
    }

    /** 字段名 **/
    public void setName(String name) {
        this.name = name;
    }

    /** 字段描述 **/
    public String getText() {
        return text;
    }

    /** 字段描述 **/
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "FieldItem{" + name + ":" + text + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if (getClass() != object.getClass()) return false;
        FieldItem other = (FieldItem) object;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }

}
