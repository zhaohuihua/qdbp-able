package com.gitee.qdbp.tools.utils;

import java.util.Map;
import com.alibaba.fastjson.JSON;

public class ReflectToolsTest {

    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
    }

    private static void test1() {
        Object object = JSON.parse("{\"domain\":{\"text\":\"baidu\",\"url\":\"http://baidu.com\"}}");
        String text = ReflectTools.getDepthValue(object, "domain.text");
        System.out.println("domain.text = " + text);
    }

    private static void test2() {
        Object object = JSON.parse(
            "[{\"domain\":{\"text\":\"baidu\",\"url\":\"http://baidu.com\"}},{\"domain\":{\"text\":\"bing\",\"url\":\"http://cn.bing.com\"}}]");
        String text = ReflectTools.getDepthValue(object, "[1].domain.text");
        System.out.println("[1].domain.text = " + text);
    }

    private static void test3() {
        Object object = JSON.parse(
            "{\"data\":[{\"domain\":{\"text\":\"baidu\",\"url\":\"http://baidu.com\"}},{\"domain\":{\"text\":\"bing\",\"url\":\"http://cn.bing.com\"}}]}");
        String text = ReflectTools.getDepthValue(object, "data[1].domain.text");
        System.out.println("data[1].domain.text = " + text);
    }

    private static void test4() {
        Object object = JSON.parse(
            "{\"data\":[{\"domain\":{\"text\":\"baidu\",\"url\":\"http://baidu.com\"}},{\"domain\":{\"text\":\"bing\",\"url\":\"http://cn.bing.com\"}}]}");
        Map<String, Object> domain = ReflectTools.getDepthValue(object, "data[1].domain");
        System.out.println("data[1].domain = " + JsonTools.toLogString(domain));
    }
}
