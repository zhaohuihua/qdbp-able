package com.gitee.zhaohuihua.tools.http;

import java.util.Map;

import com.gitee.zhaohuihua.core.result.ResponseMessage;

/**
 * 参数处理和结果解析
 *
 * @author zhaohuihua
 * @version 160907
 */
public interface IHttpHandler {

    /**
     * 填充基础参数, 如填充配置信息在的公共参数/计算摘要等操作
     * 
     * @param params 业务参数
     * @return 业务参数+基础参数
     */
    Map<String, Object> fillBaseParams(HttpUrl hurl, Map<String, Object> params);

    /**
     * 解析结果
     * 
     * @param hurl
     * @param string
     * @return
     * @throws RemoteServiceException
     * @throws Exception
     */
    ResponseMessage parseResult(HttpUrl hurl, String string) throws RemoteServiceException, Exception;
}
