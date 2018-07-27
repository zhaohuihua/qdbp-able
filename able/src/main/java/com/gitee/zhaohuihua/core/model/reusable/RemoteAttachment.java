package com.gitee.zhaohuihua.core.model.reusable;

import java.io.Serializable;

/**
 * 附件
 *
 * @author zhaohuihua
 * @version 171213
 */
public class RemoteAttachment implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 附件名称 **/
    private String name;

    /** 下载地址 **/
    private String url;

    public RemoteAttachment() {
    }

    public RemoteAttachment(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /** 附件名称 **/
    public String getName() {
        return name;
    }

    /** 附件名称 **/
    public void setName(String name) {
        this.name = name;
    }

    /** 下载地址 **/
    public String getUrl() {
        return url;
    }

    /** 下载地址 **/
    public void setUrl(String url) {
        this.url = url;
    }

}
