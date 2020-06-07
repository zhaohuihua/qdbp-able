package com.gitee.qdbp.able.matches;

import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 是否存在指定字符串的匹配规则
 *
 * @author zhaohuihua
 * @version 20200607
 */
public class ContainsStringMatcher implements StringMatcher {

    /** 匹配规则 **/
    private final String pattern;
    /** 是否反转判断结果 **/
    private final boolean reverse;

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     */
    public ContainsStringMatcher(String pattern) {
        this(pattern, false);
    }

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     * @param reverse 是否反转判断结果<br>
     *            如果reverse=false, 符合时返回true; 如果reverse=true, 不符合时返回true
     */
    public ContainsStringMatcher(String pattern, boolean reverse) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        this.pattern = pattern;
        this.reverse = reverse;
    }

    /**
     * 判断字符串中是否存在此规则指定的子字符串<br>
     * 如果reverse=false, 符合时返回true; 如果reverse=true, 不符合时返回true
     * 
     * @param source 字符串
     * @return 是否匹配
     */
    @Override
    public boolean matches(String source) {
        return source.contains(pattern) != reverse;
    }

    @Override
    public String toString() {
        if (reverse) {
            return "contains!:" + pattern;
        } else {
            return "contains:" + pattern;
        }
    }
}
