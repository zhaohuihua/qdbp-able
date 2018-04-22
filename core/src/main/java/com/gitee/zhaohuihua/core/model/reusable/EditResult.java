package com.gitee.zhaohuihua.core.model.reusable;

import java.io.Serializable;

/**
 * 编辑结果
 *
 * @author zhaohuihua
 * @version 170708
 */
public class EditResult implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 主键 **/
    private String id;
    /** 编辑序号(每编辑一次递增1) **/
    private Integer editIndex;

    public EditResult() {
    }

    public EditResult(String id, Integer editIndex) {
        this.id = id;
        this.editIndex = editIndex;
    }

    /** 获取主键 **/
    public String getId() {
        return id;
    }

    /** 设置主键 **/
    public void setId(String id) {
        this.id = id;
    }

    /** 获取编辑序号(每编辑一次递增1) **/
    public Integer getEditIndex() {
        return editIndex;
    }

    /** 设置编辑序号(每编辑一次递增1) **/
    public void setEditIndex(Integer editIndex) {
        this.editIndex = editIndex;
    }

}
