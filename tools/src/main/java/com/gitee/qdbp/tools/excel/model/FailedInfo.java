package com.gitee.qdbp.tools.excel.model;

import java.io.Serializable;
import com.gitee.qdbp.able.result.IBatchResult.Failed;
import com.gitee.qdbp.able.result.IResultMessage;

/**
 * 失败信息
 *
 * @author zhaohuihua
 * @version 160302
 */
public class FailedInfo implements Failed, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** Sheet页签的名称 **/
    private String sheetName;

    /** 序号 **/
    private Integer index;

    /** 字段名称 **/
    private String field;

    /** 错误代码 **/
    private String code;

    /** 错误描述 **/
    private String message;

    public FailedInfo() {

    }

    public FailedInfo(String sheetName, Integer index, IResultMessage result) {
        this(sheetName, index, null, result);
    }

    public FailedInfo(String sheetName, Integer index, String field, IResultMessage result) {
        this.sheetName = sheetName;
        this.index = index;
        this.field = field;
        this.code = result.getCode();
        this.message = result.getMessage();
    }

    /** 获取Sheet页签的名称 **/
    public String getSheetName() {
        return sheetName;
    }

    /** 设置Sheet页签的名称 **/
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    /** 获取序号 **/
    @Override
    public Integer getIndex() {
        return index;
    }

    /** 获取序号 **/
    public void setIndex(Integer index) {
        this.index = index;
    }

    /** 获取字段名称 **/
    public String getField() {
        return field;
    }

    /** 设置字段名称 **/
    public void setField(String field) {
        this.field = field;
    }

    /** 错误代码 **/
    @Override
    public String getCode() {
        return code;
    }

    /** 错误代码 **/
    public void setCode(String code) {
        this.code = code;
    }

    /** 错误描述 **/
    @Override
    public String getMessage() {
        return message;
    }

    /** 错误描述 **/
    public void setMessage(String message) {
        this.message = message;
    }

}
