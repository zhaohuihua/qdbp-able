package com.gitee.qdbp.able.model.file;

import java.io.Serializable;

/**
 * 图片信息
 *
 * @author zhaohuihua
 * @version 151125
 */
public class ImageInfo implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 8869603913136182603L;

    /** 小尺寸图片 **/
    private String small;

    /** 大尺寸图片 **/
    private String large;

    /** 原始图片 **/
    private String original;

    /** 获取小尺寸图片 **/
    public String getSmall() {
        return small;
    }

    /** 设置小尺寸图片 **/
    public void setSmall(String small) {
        this.small = small;
    }

    /** 获取大尺寸图片 **/
    public String getLarge() {
        return large;
    }

    /** 设置大尺寸图片 **/
    public void setLarge(String large) {
        this.large = large;
    }

    /** 获取原始图片 **/
    public String getOriginal() {
        return original;
    }

    /** 设置原始图片 **/
    public void setOriginal(String original) {
        this.original = original;
    }

}
