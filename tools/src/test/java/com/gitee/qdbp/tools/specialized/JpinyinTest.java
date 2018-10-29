package com.gitee.qdbp.tools.specialized;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class JpinyinTest {
    
    // 多音字中的常用读音顺序优先
    // 血
    // 露
    // 佪
    // 捋
    // 犴
    // 扎
    // 吭
    // 骠
    // 荠
    // 帑
    // 靓
    // 栅
    // 翟
    // 幢

    // 读音错误
    // 一声不吭
    // 尖沙咀
    // 骠悍

    public static void main(String[] args) {
        System.out.println(PinyinHelper.convertToPinyinString("一声不吭", " ", PinyinFormat.WITH_TONE_MARK));
        System.out.println(PinyinHelper.convertToPinyinString("尖沙咀", " ", PinyinFormat.WITH_TONE_MARK));
        System.out.println(PinyinHelper.convertToPinyinString("骠悍", " ", PinyinFormat.WITH_TONE_MARK));
        System.out.println(PinyinHelper.convertToPinyinString("绸缪", " ", PinyinFormat.WITH_TONE_MARK));
    }
}
