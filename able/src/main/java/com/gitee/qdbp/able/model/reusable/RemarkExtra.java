package com.gitee.qdbp.able.model.reusable;

import java.util.ArrayList;
import java.util.List;

/**
 * 备注列表
 *
 * @author zhaohuihua
 * @version 171212
 */
public class RemarkExtra extends ExtraData {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 备注列表 **/
    private List<RemarkItem> remarks;

    /** 备注列表 **/
    public List<RemarkItem> getRemarks() {
        return remarks;
    }

    /** 备注列表 **/
    public void setRemarks(List<RemarkItem> remarks) {
        this.remarks = remarks;
    }

    /** 增加备注项 **/
    public void addRemark(RemarkItem item) {
        if (this.remarks == null) {
            this.remarks = new ArrayList<>();
        }
        this.remarks.add(item);
    }

    /** 增加备注项 **/
    public void addRemark(String creator, String content) {
        this.addRemark(new RemarkItem(creator, content));
    }
}
