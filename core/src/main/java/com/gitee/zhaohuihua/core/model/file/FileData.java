package com.gitee.zhaohuihua.core.model.file;

import java.io.Serializable;

/**
 * 文件数据
 *
 * @author zhaohuihua
 * @version 151202
 */
public class FileData implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = -3655668885059255810L;

    /** 文件名 **/
    private String name;

    /** 字段名 **/
    private String field;

    /** 文件内容 **/
    private byte[] bytes;

    public FileData() {
    }

    public FileData(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
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

    /** 获取文件内容 **/
    public byte[] getBytes() {
        return bytes;
    }

    /** 设置文件内容 **/
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

}
