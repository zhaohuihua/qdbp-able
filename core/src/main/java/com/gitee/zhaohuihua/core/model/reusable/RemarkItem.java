package com.gitee.zhaohuihua.core.model.reusable;

import java.io.Serializable;
import java.util.Date;

/**
 * 备注
 *
 * @author zhaohuihua
 * @version 171212
 */
public class RemarkItem implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private Date time;
    private String creator;
    private String content;

    public RemarkItem() {
    }

    public RemarkItem(String creator, String content) {
        this.time = new Date();
        this.creator = creator;
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
