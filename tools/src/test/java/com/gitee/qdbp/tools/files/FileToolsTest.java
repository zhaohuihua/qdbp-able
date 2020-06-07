package com.gitee.qdbp.tools.files;

import java.io.File;
import java.util.List;
import com.gitee.qdbp.able.matches.StringMatcher;
import com.gitee.qdbp.able.matches.WrapStringMatcher;
import com.gitee.qdbp.tools.utils.ConvertTools;

public class FileToolsTest {

    public static void main(String[] args) {
        String rootFolder = "D:/home/filecenter/tagging/";
        List<File> files = FileTools.collect(rootFolder, "*.docx");
        System.out.println(ConvertTools.joinToString(files, "\n"));

        System.out.println("------------------");

        // 存在CCB_HTML, 不存在simplify, 以.html结尾的文件
        StringMatcher matcher2 = new WrapStringMatcher("contains:/CCB_HTML", "contains!:simplify", "ant:/**/*.html");
        List<File> files2 = FileTools.treelist(rootFolder, true, matcher2);
        System.out.println(ConvertTools.joinToString(files2, "\n"));
    }
}
