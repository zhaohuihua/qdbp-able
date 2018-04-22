package com.gitee.zhaohuihua.core.result;


/**
 * 操作结果
 *
 * @author zhaohuihua
 * @version 150915
 */
public interface IResultMessage {

    /**
     * 获取返回码
     *
     * @return 返回码
     */
    String getCode();

    /**
     * 获取返回消息
     *
     * @return 返回消息
     */
    String getMessage();
}
