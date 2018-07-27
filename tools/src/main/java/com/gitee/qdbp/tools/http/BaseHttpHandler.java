package com.gitee.qdbp.tools.http;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.HeaderGroup;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.qdbp.able.result.ResponseMessage;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.able.utils.VerifyTools;

public class BaseHttpHandler implements IHttpHandler {

    private HeaderGroup headers;

    /** 追加header参数 **/
    protected void addHeader(String name, String value) {
        if (VerifyTools.isAnyBlank(name, value)) {
            return;
        }
        if (this.headers == null) {
            this.headers = new HeaderGroup();
        }
        this.headers.addHeader(new BasicHeader(name, value));
    }

    /** 删除header参数 **/
    protected void removeHeader(String name) {
        if (VerifyTools.isBlank(name)) {
            return;
        }
        if (this.headers == null) {
            return;
        }
        for (HeaderIterator i = this.headers.iterator(); i.hasNext();) {
            Header header = i.nextHeader();
            if (name.equalsIgnoreCase(header.getName())) {
                i.remove();
            }
        }
    }

    /** 遍历header参数 **/
    protected HeaderIterator headerIterator() {
        if (this.headers == null) {
            return new HeaderGroup().iterator();
        } else {
            return this.headers.iterator();
        }
    }

    /** 获取全部header参数 **/
    public Header[] getAllHeaders() {
        return this.headers == null ? null : this.headers.getAllHeaders();
    }

    /**
     * 填充基础参数, 如填充配置信息在的公共参数/计算摘要等操作
     * 
     * @param hurl 请求地址
     * @param params 业务参数
     * @return 业务参数+基础参数
     */
    public <T> Map<String, Object> fillBaseParams(HttpUrl hurl, Map<String, T> params) {
        Map<String, Object> map = new HashMap<>();
        if (VerifyTools.isNotBlank(params)) {
            map.putAll(params);
        }
        return map;
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
        result.setBody(json.get("body"));
        result.setExtra(json.getJSONObject("extra"));

        if (ResultCode.SUCCESS.name().equals(result.getCode())) {
            return result;
        } else {
            throw new RemoteServiceException(result.getCode(), result.getMessage());
        }
    }
}
