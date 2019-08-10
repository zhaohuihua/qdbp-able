package com.gitee.qdbp.tools.utils;

import com.gitee.qdbp.able.beans.DepthMap;

public class DepthMapTest {

    public static void main(String[] args) {

        DepthMap dpm = new DepthMap();
        dpm.put("author", "xxx"); // 该设置无效, 会被后面的覆盖掉
        dpm.put("author.code", 99);
        dpm.put("author.code", 100);
        dpm.put("author.name", "zhaohuihua");

        dpm.put("code.folder.service", "service");
        dpm.put("code.folder.page", "views");
        dpm.put("code.folder", "java"); // 该设置无效, 会被忽略掉

        System.out.println("author = " + JsonTools.toLogString(dpm.get("author"))); // Map({code=100, name=zhaohuihua})
        System.out.println("author.code = " + dpm.get("author.code")); // 100
        System.out.println("author.name = " + dpm.get("author.name")); // zhaohuihua

        System.out.println("code.folder = " + JsonTools.toLogString(dpm.get("code.folder"))); // Map({service=service, page=views})
        System.out.println("code.folder.service = " + dpm.get("code.folder.service")); // service
        System.out.println("code.folder.page = " + dpm.get("code.folder.page")); // views

    }
}
