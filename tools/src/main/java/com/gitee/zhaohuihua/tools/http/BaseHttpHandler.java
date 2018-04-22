package com.gitee.zhaohuihua.tools.http;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.zhaohuihua.core.result.ResponseMessage;
import com.gitee.zhaohuihua.core.result.ResultCode;

public class BaseHttpHandler implements IHttpHandler {

    /**
     * 填充基础参数, 如填充配置信息在的公共参数/计算摘要等操作
     * 
     * @param hurl 请求地址
     * @param params 业务参数
     * @return 业务参数+基础参数
     */
    public Map<String, Object> fillBaseParams(HttpUrl hurl, Map<String, Object> params) {
        return params;
    }

    /**
     * 解析结果
     * 
     * @param hurl 请求地址
     * @param string 返回报文
     * @return
     * @throws RemoteServiceException
     * @throws Exception
     */
    public ResponseMessage parseResult(HttpUrl hurl, String string) throws RemoteServiceException, Exception {
        JSONObject json = JSON.parseObject(string);
        ResponseMessage result = new ResponseMessage();
        result.setCode(json.getString("code"));
        result.setMessage(json.getString("message"));
        result.setBody(json.getString("body"));
        result.setExtra(json.getJSONObject("extra"));

        if (ResultCode.SUCCESS.name().equals(result.getCode())) {
            return result;
        } else {
            throw new RemoteServiceException(result.getCode(), result.getMessage());
        }
    }
}
