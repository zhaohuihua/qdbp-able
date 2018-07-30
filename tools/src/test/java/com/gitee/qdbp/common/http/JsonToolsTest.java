package com.gitee.zhaohuihua.common.http;

import java.util.HashMap;
import java.util.Map;
import com.gitee.qdbp.tools.http.HttpException;
import com.gitee.qdbp.tools.http.HttpTools;

public class JsonToolsTest {

    public static void main(String[] args) {
        String url = "http://hdev01.cttq.com/cim-user-gwy/user/doctorRegisterAudit";
        Map<String, Object> data = new HashMap<>();
        data.put("accountCid", "201601251326023230619fe78f6220eb");
        data.put("registerApplyCid", "2016012920303240607176aec6733a32");
        data.put("status", "1");
        Map<String, Object> params = new HashMap<>();
        params.put("jsonData", data);
        params.put("account", "15500000006");
        params.put("accountType", 1);
        params.put("appCode", "A000");
        params.put("digest", "6ea2604fa0204421b2579aac82d43214");
        params.put("imeiuuid", "359596063773059");
        params.put("sourceType", "android");
        params.put("ts", 945700);
        params.put("createTime", "2016-1-20");
        try {
            System.out.println(HttpTools.json.post(url, params));
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

}
