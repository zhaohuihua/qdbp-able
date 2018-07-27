package com.gitee.zhaohuihua.core.model.file;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 输入流数据
 *
 * @author zhaohuihua
 * @version 151202
 */
public class InputData implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = -4729104654201326796L;

    /** 文件名 **/
    private String name;

    /** 字段名 **/
    private String field;

    /** 文件流 **/
    private InputStream input;

    /** 文件大小 **/
    private long size;

    public InputData() {
    }

    public InputData(String name, InputStream input, long size) {
        this.name = name;
        this.input = input;
        this.size = size;
    }

    /** 获取文件名 **/
    public String getName() {
        return name;
    }

    /** 设置文件名 **/
    public void setName(String name) {
        this.name = name;
    }

    /** 获取字段名 **/
    public String getField() {
        return field;
    }

    /** 设置字段名 **/
    public void setField(String field) {
        this.field = field;
    }

    /** 获取文件流 **/
    public InputStream getInput() {
        return input;
    }

    /** 设置文件流 **/
    public void setInput(InputStream input) {
        this.input = input;
    }

    /** 获取文件大小 **/
    public long getSize() {
        return size;
    }

    /** 设置文件大小 **/
    public void setSize(long size) {
        this.size = size;
    }

}
