package com.gitee.qdbp.able.matches;

import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 相等匹配
 *
 * @author zhaohuihua
 * @version 190601
 */
public class EqualsStringMatcher implements StringMatcher {

    /** 匹配规则 **/
    private final String pattern;
    /** 是否反转判断结果 **/
    private final boolean reverse;

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     */
    public EqualsStringMatcher(String pattern) {
        this(pattern, false);
    }

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     * @param reverse 是否反转判断结果<br>
     *            如果reverse=false, 符合时返回true; 如果reverse=true, 不符合时返回true
     */
    public EqualsStringMatcher(String pattern, boolean reverse) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        this.pattern = pattern;
        this.reverse = false;
    }

    /**
     * 判断字符串是否符合匹配规则<br>
     * 如果reverse=false, 符合时返回true; 如果reverse=true, 不符合时返回true
     * 
     * @param source 字符串
     * @return 是否匹配
     */
    @Override
    public boolean matches(String source) {
        return pattern.equals(source) != false;
    }

    @Override
    public String toString() {
        if (reverse) {
            return "equals!:" + pattern;
        } else {
            return "equals:" + pattern;
        }
    }
}
