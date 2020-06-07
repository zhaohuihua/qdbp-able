package com.gitee.qdbp.able.matches;

import com.gitee.qdbp.able.matches.WrapStringMatcher.LogicType;
import com.gitee.qdbp.tools.utils.StringTools;

public class WrapStringMatcherTest {

    public static void main(String[] args) {
        // @formatter:off
        String[] sources = new String[] {
            "/home/files/202005/aaa.docx",
            "/home/files/202005/bbb.html",
            "/home/files/202005/aaa.docx.bak",
            "/home/files/temp/202005/aaa.docx",
            "/home/files/temp/202005/bbb.html",
            "/home/files/temp/202005/aaa.docx.bak"
        };
        // @formatter:on
        test1(sources);
        test2(sources);
        test3(sources);
    }

    private static void test1(String[] sources) {
        // 不在temp文件夹下的docx文件
        String rule1 = "contains!:/temp/";
        String rule2 = "ant:/**/*.docx";
        StringMatcher matcher = new WrapStringMatcher(LogicType.AND, rule1, rule2);
        doMatches(matcher, sources);
    }

    private static void test2(String[] sources) {
        // temp文件夹下的bak文件
        String rule1 = "contains:/temp/";
        String rule2 = "ant:/**/*.bak";
        StringMatcher matcher = new WrapStringMatcher(LogicType.AND, rule1, rule2);
        doMatches(matcher, sources);
    }

    private static void test3(String[] sources) {
        // temp文件或bak文件
        String rule1 = "contains:/temp/";
        String rule2 = "ant:/**/*.bak";
        StringMatcher matcher = new WrapStringMatcher(LogicType.OR, rule1, rule2);
        doMatches(matcher, sources);
    }

    private static void doMatches(StringMatcher matcher, String... strings) {
        System.out.println(matcher.toString());
        for (String string : strings) {
            boolean matches = matcher.matches(string);
            System.out.println(StringTools.pad(String.valueOf(matches), ' ', false, 8) + string);
        }
    }
}
