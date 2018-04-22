package com.gitee.zhaohuihua.common.files;

import com.gitee.zhaohuihua.tools.files.PathTools;

public class RelativizeTest {

    public static void main(String[] args) throws Exception {
        testFile();
        testHttp();
    }

    private static void testFile() {
        String root = "D:/domain/biz/";
        System.out.println("测试FILE: " + root);
        System.out.println(PathTools.relativize(root, "D:/domain/biz/index.html"));
        System.out.println(PathTools.relativize(root, "D:/domain/biz/html/homepage.html"));
        System.out.println(PathTools.relativize(root, "D:/domain/assets/libs/mui/mui.js"));
        System.out.println(PathTools.relativize(root, "D:/static/assets/libs/mui/mui.js"));
        System.out.println("跨盘符");
        String relative = PathTools.relativize(root, "E:/static/assets/libs/mui/mui.js");
        System.out.println(relative);
        System.out.println(PathTools.concat(root, relative));
        System.out.println(PathTools.concat(true, root, relative));
    }

    private static void testHttp() {
        String root = "http://xxx.com/domain/biz/";
        System.out.println("测试HTTP: " + root);
        System.out.println(PathTools.relativize(root, "http://xxx.com/domain/biz/index.html"));
        System.out.println(PathTools.relativize(root, "http://xxx.com/domain/biz/html/homepage.html"));
        System.out.println(PathTools.relativize(root, "http://xxx.com/domain/assets/libs/mui/mui.js"));
        System.out.println(PathTools.relativize(root, "http://xxx.com/static/assets/libs/mui/mui.js"));
        System.out.println("跨站点");
        String relative = PathTools.relativize(root, "http://yyy.com/static/assets/libs/mui/mui.js");
        System.out.println(relative);
        System.out.println(PathTools.concat(root, relative));
        System.out.println(PathTools.concat(true, root, relative));
    }
}
