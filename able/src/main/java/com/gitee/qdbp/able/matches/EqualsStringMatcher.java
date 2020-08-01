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
        this(pattern, Matches.Positive);
    }

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     * @param mode 匹配模式: Positive=肯定模式, 符合条件为匹配; Negative=否定模式, 不符合条件为匹配
     */
    public EqualsStringMatcher(String pattern, Matches mode) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        this.pattern = pattern;
        this.reverse = mode == Matches.Negative;
    }

    /**
     * 判断字符串是否符合匹配规则<br>
     * 与匹配模式有关: Positive=肯定模式, 符合条件为匹配; Negative=否定模式, 不符合条件为匹配
     * 
     * @param source 字符串
     * @return 是否匹配
     */
    @Override
    public boolean matches(String source) {
        return VerifyTools.equals(source, pattern) != reverse;
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
