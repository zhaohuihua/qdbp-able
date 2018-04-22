package com.gitee.zhaohuihua.tools.http;

import java.util.List;
import java.util.Map;

import com.gitee.zhaohuihua.core.result.ResponseMessage;
import com.gitee.zhaohuihua.tools.utils.Config;

/**
 * 接口请求工具类<br>
 * 一般情况下, 基础参数/加解密逻辑/解析响应报文, 对于同一个第三方平台来说是相同的<br>
 * 这是HttpExecutor的基础条件, 如果不满足, 你还是不要用这个工具了, 自己用HttpTools自己实现吧<br>
 * HttpExecutor由三部分组成, HostUrlConfig, HttpTools, IHttpHandler<br>
 * HostUrlConfig负责配置项的读取和缓存<br>
 * HttpTools负责调用apache的HttpClient发送请求, 和调用IHttpHandler完成发送前的参数封装/接收后的报文解析<br>
 * IHttpHandler实现对于每个第三方平台独有业务逻辑的封装, 有fillBaseParams和parseResult两个访求:<br>
 * -- fillBaseParams负责组装一些平台级的基础参数, 或是计算摘要/加密等<br>
 * -- parseResult则负责对收到的报文进行解析, 将第三方平台的数据结构组装成本平台的数据结构<br>
 * <br>
 * 详见DEMO, XxxAuthHttpExecutor<br>
 *
 * @author zhaohuihua
 * @version 160224
 */
public class HttpExecutor {

    protected HttpTools tools;

    protected HostUrlConfig config;

    protected HttpExecutor(HostUrlConfig config, HttpTools tools) {
        this.config = config;
        this.tools = tools;
    }

    protected HttpExecutor(HostUrlConfig config, HttpTools tools, IHttpHandler handler) {
        this.config = config;
        this.tools = tools;
        this.tools.setHttpHandler(handler);
        if (handler instanceof Config.Aware) {
            ((Config.Aware) handler).setConfig(config);
        }
    }

    /**
     * 执行远程请求
     *
     * @param key 请求地址的KEY
     * @return 响应结果
     * @throws HttpException
     */
    public ResponseMessage execute(String key) throws HttpException {
        return tools.execute(config.getUrl(key));
    }

    /**
     * 执行远程请求
     *
     * @param key 请求地址的KEY
     * @param params 请求参数
     * @return 响应结果
     * @throws HttpException
     */
    public ResponseMessage execute(String key, Map<String, Object> params) throws HttpException {
        return tools.execute(config.getUrl(key), params);
    }

    /**
     * 执行远程请求并返回指定对象
     *
     * @param key 请求地址的KEY
     * @param type 结果类型
     * @return 响应结果对象
     * @throws HttpException
     */
    public <T> T query(String key, Class<T> type) throws HttpException {
        return tools.query(config.getUrl(key), type);
    }

    /**
     * 执行远程请求并返回指定对象
     *
     * @param key 请求地址的KEY
     * @param params 请求参数
     * @param type 结果类型
     * @return 响应结果对象
     * @throws HttpException
     */
    public <T> T query(String key, Map<String, Object> params, Class<T> type) throws HttpException {
        return tools.query(config.getUrl(key), params, type);
    }

    /**
     * 执行远程请求并返回对象列表
     *
     * @param key 请求地址的KEY
     * @param type 结果类型
     * @return 对象列表
     * @throws HttpException
     */
    public <T> List<T> list(String key, Class<T> type) throws HttpException {
        return tools.list(config.getUrl(key), type);
    }

    /**
     * 执行远程请求并返回对象列表
     *
     * @param key 请求地址的KEY
     * @param params 请求参数
     * @param type 结果类型
     * @return 对象列表
     * @throws HttpException
     */
    public <T> List<T> list(String key, Map<String, Object> params, Class<T> type) throws HttpException {
        return tools.list(config.getUrl(key), params, type);
    }
}
