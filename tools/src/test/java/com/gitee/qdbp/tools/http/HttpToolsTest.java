package com.gitee.qdbp.tools.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class HttpToolsTest {

    public static void main(String[] args) {
        String url = "http://cimqas.cttq.com/cim-clinic-gwy/operate/clinic/online/doc/cancel/record";
        String text =
                "{\"createTime\":\"2016-05-10\",\"sourceType\":\"pc\",\"ts\":34370,\"appCode\":\"A000\",\"accountType\":1,\"account\":\"13913001382\",\"digest\":\"25AE1ED2CDF19CD5A36CE58E1B2158CE\",\"imeiuuid\":\"359596063773059\",\"jsonData\":{\"causeContext\":\"诊室异常\",\"bussiType\":5,\"clinicDate\":\"2016-05-10 09:12:00\",\"clinicCid\":\"20160510085941749062992ee3a8ec60\"}}";
        JSONObject json = JSON.parseObject(text);
        try {
            System.out.println(HttpTools.json.post(url, json));
        } catch (HttpException e) {
            e.printStackTrace();
        }
    }

}
