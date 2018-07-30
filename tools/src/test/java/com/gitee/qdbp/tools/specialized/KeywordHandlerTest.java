package com.gitee.qdbp.tools.specialized;

import com.gitee.qdbp.tools.specialized.KeywordHandler;

public class KeywordHandlerTest {

    public static void main(String[] args) {
        KeywordHandler keywords = KeywordHandler.newInstance();
        keywords.addSegment("小米 手机");
        keywords.addPlain("MIX-2S");
        keywords.addText("全面屏游戏手机 陶瓷白 全网通");
        System.out.println(keywords.toString());
    }
}
