package com.gitee.qdbp.tools.excel;

import com.gitee.qdbp.able.result.IResultMessage;

/**
 * 文件类结果返回码枚举类
 *
 * @author zhaohuihua
 * @version 150915
 */
public enum ExcelErrorCode implements IResultMessage {

    /** 数据格式错误 **/
    EXCEL_DATA_FORMAT_ERROR("数据格式错误"),

    /** Sheet不存在 **/
    EXCEL_SHEET_NOT_FOUND("Sheet不存在"),

    /** 文件不存在 **/
    FILE_NOT_FOUND("文件不存在"),

    /** 文件读取失败 **/
    FILE_READ_ERROR("文件读取失败"),

    /** 文件写入失败 **/
    FILE_WRITE_ERROR("文件写入失败"),

    /** 文件格式错误 **/
    FILE_FORMAT_ERROR("文件格式错误"),

    /** 文件模板格式错误 **/
    FILE_TEMPLATE_ERROR("文件模板格式错误");

    /** 返回码描述 **/
    private final String message;

    /**
     * 构造函数
     *
     * @param message 返回码描述
     */
    private ExcelErrorCode(String message) {
        this.message = message;
    }

    /** {@inheritDoc} **/
    @Override
    public String getCode() {
        return this.name();
    }

    /** {@inheritDoc} **/
    @Override
    public String getMessage() {
        return message;
    }
}
