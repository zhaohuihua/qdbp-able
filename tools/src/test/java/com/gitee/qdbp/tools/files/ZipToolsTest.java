package com.gitee.zhaohuihua.common.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.tools.files.ZipTools;
import com.gitee.qdbp.tools.files.ZipTools.UrlItem;

public class ZipToolsTest {

    public static void main(String[] args) throws IOException {
        List<UrlItem> urls = new ArrayList<>();
        urls.add(new UrlItem("image/百度.png", "http://www.baidu.com/img/bd_logo.png"));
        urls.add(new UrlItem("image/腾讯.png", "http://mat1.gtimg.com/www/images/qq2012/qqlogo.png"));

        ZipTools.compression(urls, "D:/测试.zip");
    }
}
