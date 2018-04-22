package com.gitee.zhaohuihua.common.http;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.zhaohuihua.core.result.ResponseMessage;
import com.gitee.zhaohuihua.core.result.ResultCode;
import com.gitee.zhaohuihua.tools.codec.DigestTools;
import com.gitee.zhaohuihua.tools.codec.HexTools;
import com.gitee.zhaohuihua.tools.files.PathTools;
import com.gitee.zhaohuihua.tools.http.ConfigHttpHandler;
import com.gitee.zhaohuihua.tools.http.HostUrlConfig;
import com.gitee.zhaohuihua.tools.http.HttpException;
import com.gitee.zhaohuihua.tools.http.HttpExecutor;
import com.gitee.zhaohuihua.tools.http.HttpUrl;
import com.gitee.zhaohuihua.tools.http.RemoteServiceException;
import com.gitee.zhaohuihua.tools.http.HostUrlConfig.KeyedHttpUrl;
import com.gitee.zhaohuihua.tools.http.HttpTools.JsonTools;
import com.gitee.zhaohuihua.tools.utils.RandomTools;
import com.gitee.zhaohuihua.tools.utils.StringTools;

public class HttpExecutorTest extends HttpExecutor {

    private static final URL PATH = PathTools.findResource("cttq.cfg", HttpExecutorTest.class);

    public HttpExecutorTest() {
        super(new HostUrlConfig(PATH, "cttq.cim.host"), new JsonTools(), new CttqHttpHandler());
    }

    public static void main(String[] args) {

        HttpExecutorTest test = new HttpExecutorTest();

        Map<String, Object> data = new HashMap<>();
        data.put("account", "15295530709");
        data.put("passwd", DigestTools.md5("123456").toLowerCase());
        data.put("accountType", 2);
        try {
            ResponseMessage rsp = test.execute("cim.login.url", data);
            System.out.println(StringTools.toJsonString(rsp));
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

    private static class CttqHttpHandler extends ConfigHttpHandler {

        @Override
        public Map<String, Object> fillBaseParams(HttpUrl hurl, Map<String, Object> data) {
            Map<String, Object> map = new HashMap<>();

            String account = config.getString("cim.interface.account");
            String appcode = config.getString("cim.interface.app.code");
            String imeiuuid = config.getString("cim.interface.imeiuuid");
            String createTimePattern = config.getString("cim.interface.create.time");
            String versionSecretKey = config.getString("cim.interface.version.secret.key");

            String createTime = new SimpleDateFormat(createTimePattern).format(new Date());
            long ts = Long.valueOf(RandomTools.generateNumber(5));
            String info = account + ts + imeiuuid + appcode + versionSecretKey;

            map.put("appCode", appcode);
            map.put("imeiuuid", imeiuuid);
            map.put("account", account);
            map.put("accountType", config.getInteger("cim.interface.account.type"));
            map.put("sourceType", config.getString("cim.interface.source.type"));
            map.put("createTime", createTime);
            map.put("ts", ts);
            if (data != null) {
                map.put("jsonData", data);
            }

            try {
                byte[] digests = MessageDigest.getInstance("MD5").digest(info.getBytes());
                map.put("digest", HexTools.toString(digests));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            return map;
        }

        @Override
        public ResponseMessage parseResult(HttpUrl hurl, String string) throws RemoteServiceException, Exception {
            // { resultCode:int, resultJson:{ msgCode:string, message:string, data:json } }
            JSONObject json = JSON.parseObject(string);
            ResponseMessage result = new ResponseMessage();
            String resultCode = json.getString("resultCode");
            if (!resultCode.equals(config.getString("cim.interface.success"))) {
                // 这一类异常不应该抛上去给用户看到了, 直接返回接口调用失败, 日志HttpTools会统一记录的
                ResultCode code = ResultCode.REMOTE_SERVICE_FAIL;
                throw new RemoteServiceException(code.getCode(), code.getMessage());
            } else {
                String resultString = json.getString("resultJson");
                JSONObject resultJson = JSON.parseObject(resultString);
                result.setCode(resultJson.getString("msgCode"));
                result.setMessage(resultJson.getString("message"));
                result.setBody(resultJson.getString("data"));

                String key = ((KeyedHttpUrl) hurl).getKey();
                if (result.getCode().equals(config.getString(key + ".success"))) {
                    return result;
                } else {
                    throw new RemoteServiceException(result.getCode(), result.getMessage());
                }
            }
        }

    }
}
