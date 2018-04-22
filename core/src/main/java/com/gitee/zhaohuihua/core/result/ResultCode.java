package com.gitee.zhaohuihua.core.result;

/**
 * 结果返回码枚举类
 *
 * @author zhaohuihua
 * @version 150915
 */
public enum ResultCode implements IResultMessage {

    /** 操作成功 **/
    SUCCESS("操作成功"),

    /** 必填参数不能为空 **/
    PARAMETER_IS_REQUIRED("必填参数不能为空"),

    /** 参数格式错误 **/
    PARAMETER_FORMAT_ERROR("参数格式错误"),

    /** 参数值错误 **/
    PARAMETER_VALUE_ERROR("参数值错误"),

    /** 参数值超出允许范围(一般是指数字或日期) **/
    PARAMETER_OUT_RANGE("参数值超出允许范围"),

    /** 服务器内部异常 **/
    SERVER_INNER_ERROR("服务器内部异常"),

    /** 远程服务调用失败 **/
    REMOTE_SERVICE_ERROR("远程服务调用失败"),

    /** 远程服务对方返回失败 **/
    REMOTE_SERVICE_FAIL("远程服务对方返回失败"),

    /** 数据库INSERT失败 **/
    DB_INSERT_ERROR("数据保存失败"),

    /** 数据库UPDATE失败 **/
    DB_UPDATE_ERROR("数据更新失败"),

    /** 数据库DELETE失败 **/
    DB_DELETE_ERROR("数据删除失败"),

    /** 数据库SELECT失败 **/
    DB_SELECT_ERROR("数据查询失败"),

    /** 违反唯一约束 **/
    DB_DUPLICATE_KEY("违反唯一约束"),

    /** 记录不存在 **/
    RECORD_NOT_EXIST("记录不存在"),

    /** 记录状态错误 **/
    RECORD_STATE_ERROR("记录状态错误"),

    /** 用户未登录 **/
    UNAUTHORIZED("用户未登录"),

    /** 没有权限 **/
    FORBIDDEN("没有权限"),

    /** 访问被拒绝 **/
    ACCESS_DENIED("访问被拒绝"),

    /** 操作已超时 **/
    OPERATE_TIMEOUT("操作已超时"),

    /** 暂不支持该操作 **/
    UNSUPPORTED_OPERATION("暂不支持该操作");

    /** 返回码描述 **/
    private final String message;

    /**
     * 构造函数
     *
     * @param message 返回码描述
     */
    private ResultCode(String message) {
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
