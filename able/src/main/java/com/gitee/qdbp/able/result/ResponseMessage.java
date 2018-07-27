package com.gitee.qdbp.able.result;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.gitee.qdbp.able.model.paging.PageList;
import com.gitee.qdbp.able.model.paging.PartList;

/**
 * 响应消息
 *
 * @author zhaohuihua
 * @version 150211
 */
public class ResponseMessage implements IResultMessage, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 5354279138750945658L;

    /** 返回码 **/
    private String code;

    /** 返回消息 **/
    private String message;

    /** 返回结果 **/
    private Object body;

    /** 附加信息 **/
    private Map<String, Object> extra;

    /** 构造函数, 默认消息为成功 **/
    public ResponseMessage() {
        this.code = ResultCode.SUCCESS.getCode();
        this.message = ResultCode.SUCCESS.getMessage();
    }

    /**
     * 构造函数, 默认消息为成功
     *
     * @param body 消息体
     */
    public ResponseMessage(Object body) {
        this.code = ResultCode.SUCCESS.getCode();
        this.message = ResultCode.SUCCESS.getMessage();
        this.setBody(body);
    }

    /**
     * 构造函数
     *
     * @param resultMessage 异常类型
     */
    public ResponseMessage(IResultMessage resultMessage) {
        this.code = resultMessage.getCode();
        this.message = resultMessage.getMessage();
    }

    /** 获取返回码 **/
    @Override
    public String getCode() {
        return code;
    }

    /** 设置返回码 **/
    public void setCode(String code) {
        this.code = code;
    }

    /** 获取返回消息 **/
    @Override
    public String getMessage() {
        return message;
    }

    /** 设置返回消息 **/
    public void setMessage(String message) {
        this.message = message;
    }

    /** 获取返回内容 **/
    public Object getBody() {
        return body;
    }

    /** 设置返回内容 **/
    public void setBody(Object body) {
        if (body instanceof PageList) {
            PageList<?> temp = (PageList<?>) body;
            this.body = temp.getList();
            this.addExtra("total", temp.getTotal());
        } else if (body instanceof PartList) {
            this.body = body;
            this.addExtra("total", ((PartList<?>) body).getTotal());
        } else {
            this.body = body;
        }
    }

    /** 获取附加信息 **/
    public Map<String, Object> getExtra() {
        return extra;
    }

    /** 设置附加信息 **/
    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    /** 增加附加信息 **/
    public void addExtra(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.put(key, value);
    }

    /** 增加附加信息 **/
    public void addExtra(Map<String, ?> extra) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.putAll(extra);
    }

    @Override
    public String toString() {
        return code + "[" + message + "]";
    }

}
