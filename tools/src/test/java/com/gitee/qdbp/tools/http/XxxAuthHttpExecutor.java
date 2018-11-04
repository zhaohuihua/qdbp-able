package com.gitee.qdbp.tools.http;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResponseMessage;
import com.gitee.qdbp.able.result.ResultCode;
import com.gitee.qdbp.tools.codec.HexTools;
import com.gitee.qdbp.tools.files.PathTools;
import com.gitee.qdbp.tools.http.HttpTools.HttpJsonImpl;
import com.gitee.qdbp.tools.utils.RandomTools;

/**
 * 这是怎么开发HttpExecutor的DEMO, 以xxx平台为例<br>
 * <pre>
# xxx.auth.properties
# interface host url
xxx.auth.host = http://www.xxx.com/auth/
# interface common return code
xxx.auth.code.sys.success = 0
xxx.auth.code.biz.success = 000000
# interface common params
cim.interface.account  = 10000000000
cim.interface.password = aabbcc112233

# interface list
xxx.auth.user.list     = POST user-center/user/list.json
xxx.auth.user.baseinfo = POST user-center/user/baseinfo.json
xxx.auth.user.register = POST user-center/user/register.json
xxx.auth.user.balance  = POST user-center/user/balance.json
 * </pre>
 * 
 * @author zhaohuihua
 * @version 170715
 */
public class XxxAuthHttpExecutor extends HttpExecutor {

    /** 配置文件地址 **/
    private static final URL PATH = PathTools.findResource("settings/xxx.auth.properties", XxxAuthHttpExecutor.class);

    public static final XxxAuthHttpExecutor me = new XxxAuthHttpExecutor();

    public XxxAuthHttpExecutor() {
        // new HttpFormImpl() -- form 方式提交请求参数
        // new HttpJsonImpl() -- json 方式提交请求参数
        // super(new HostUrlConfig(PATH, "xxx.auth.host"), new HttpFormImpl(), new XxxAuthHandler());
        super(new HostUrlConfig(PATH, "xxx.auth.host"), new HttpJsonImpl(), new XxxAuthHandler());
    }

    // 如果host url prefix是通过数据库配置的, 则可以调这个构造函数, 但最好建一个单例缓存
    // public XxxAuthHttpExecutor(String hostUrlPrefix) {
    //     super(new HostUrlConfig(PATH, hostUrlPrefix), new HttpJsonImpl(), new XxxAuthHandler());
    // }

    // 如果针对每个接口存在不一样的请求参数或响应报文配置, 可将hurl强转为KeyedHttpUrl
    // String key = ((KeyedHttpUrl) hurl).getKey();
    // config.getString(key + ".any.subffix"); // 针对每个接口作不同的配置

    private static class XxxAuthHandler extends ConfigHttpHandler {

        // return { account:string, tm:long, ts:int, digest:string, body:data  }
        @Override
        public <P> Map<String, Object> fillBaseParams(HttpUrl hurl, Map<String, P> data) {

            Map<String, Object> map = new HashMap<>();

            // 组装基础参数
            String account = config.getString("xxx.auth.account"); // 本平台在xxx平台申请的账号
            String password = config.getString("xxx.auth.password"); // 对应的密码
            long ts = Long.valueOf(RandomTools.generateNumber(5)); // xxx平台要求用一个5位随机数加密
            long time = System.currentTimeMillis();

            map.put("account", account);
            map.put("tm", time);
            map.put("ts", ts);
            if (data != null) {
                map.put("body", data);
            }

            try { // 计算摘要
                String info = account + password + ts + time;
                byte[] digests = MessageDigest.getInstance("MD5").digest(info.getBytes());
                map.put("digest", HexTools.toString(digests));
            } catch (NoSuchAlgorithmException e) { // 这个异常不会出现, 除非你用的JDK不支持MD5加密
                throw new IllegalStateException("NoSuchAlgorithm: MD5", e);
            }

            return map;
        }

        @Override
        public ResponseMessage parseResult(HttpUrl hurl, String string) throws RemoteServiceException, Exception {
            // xxx平台的响应报文是一个两层结构
            // { code:int, json:{ resultCode:string, resultMessage:string, resultData:json } }
            // code是系统异常, 0=成功; returnCode是业务异常, 000000=成功
            JSONObject json = JSON.parseObject(string);
            String sysCode = json.getString("code");
            if (!sysCode.equals(config.getString("xxx.auth.code.sys.success"))) {
                // xxx.auth.code.sys.success = 0, 不等于0的都是失败
                // 这一类异常不应该抛上去给用户看到了, 直接返回接口调用失败, 日志HttpTools会统一记录的
                ResultCode resultCode = ResultCode.REMOTE_SERVICE_FAIL;
                throw new RemoteServiceException(resultCode.getCode(), resultCode.getMessage());
            } else {
                String jsonString = json.getString("json");
                JSONObject jsonObject = JSON.parseObject(jsonString);
                String bizCode = jsonObject.getString("resultCode");
                if (!bizCode.equals(config.getString("xxx.auth.code.biz.success"))) {
                    // xxx.auth.code.biz.success = 000000, 不等于000000的都是失败
                    throw new RemoteServiceException(sysCode, jsonObject.getString("resultMessage"));
                } else {
                    ResponseMessage result = new ResponseMessage();
                    result.setBody(jsonObject.getString("resultData"));
                    return result;
                }
            }

        }

    }
}

class UserInfo {
    // id, name, phone, email, ...
}

class UserQueryParams {
    // query condition, ...
}

/**
 * 这是怎么使用HttpExecutor的DEMO
 *
 * @author zhaohuihua
 * @version 170715
 */
class UserAuthService {

    public UserInfo queryUserInfo(UserQueryParams contidion) throws ServiceException {
        try {
            JSONObject params = (JSONObject) JSON.toJSON(contidion);
            return XxxAuthHttpExecutor.me.query("xxx.auth.user.baseinfo", params, UserInfo.class);
        } catch (RemoteServiceException e) {
            throw new ServiceException(e, e); // ServiceException(IResultMessage, Throwable)
        } catch (Exception e) {
            throw new ServiceException(ResultCode.REMOTE_SERVICE_ERROR);
        }
    }

    public List<UserInfo> listUserInfo(UserQueryParams contidion) throws ServiceException {
        try {
            JSONObject params = (JSONObject) JSON.toJSON(contidion);
            return XxxAuthHttpExecutor.me.list("xxx.auth.user.list", params, UserInfo.class);
        } catch (RemoteServiceException e) {
            throw new ServiceException(e, e); // ServiceException(IResultMessage, Throwable)
        } catch (Exception e) {
            throw new ServiceException(ResultCode.REMOTE_SERVICE_ERROR);
        }
    }

    /** 注册并返回用户ID **/
    public String register(UserInfo userInfo) throws ServiceException {
        try {
            JSONObject params = (JSONObject) JSON.toJSON(userInfo);
            // 注册接口返回的resultData = { userId:string }
            ResponseMessage resp = XxxAuthHttpExecutor.me.execute("xxx.auth.user.register", params);
            JSONObject result = JSON.parseObject((String) resp.getBody());
            return result.getString("userId");
        } catch (RemoteServiceException e) {
            throw new ServiceException(e, e); // ServiceException(IResultMessage, Throwable)
        } catch (Exception e) {
            throw new ServiceException(ResultCode.REMOTE_SERVICE_ERROR);
        }
    }

    /** 查询用户钱包余额 **/
    public Double balance(String userId) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            // 余额接口返回的resultData = { balance:double }
            ResponseMessage resp = XxxAuthHttpExecutor.me.execute("xxx.auth.user.balance", params);
            JSONObject result = JSON.parseObject((String) resp.getBody());
            return result.getDouble("balance");
        } catch (RemoteServiceException e) {
            throw new ServiceException(e, e); // ServiceException(IResultMessage, Throwable)
        } catch (Exception e) {
            throw new ServiceException(ResultCode.REMOTE_SERVICE_ERROR);
        }
    }
}
