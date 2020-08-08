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
        this(pattern, Matches.Positive);
    }

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     * @param reverse 是否反转判断结果<br>
     *            如果reverse=false, 符合时返回true; 如果reverse=true, 不符合时返回true
     * @deprecated 改为 {@link #ContainsStringMatcher(String, Matches)}<br>
     *            因为reverse写在构造函数中恰好与习惯思维相反<br>
     *            new ContainsStringMatcher(pattern, false)容易理解为期望不包含, 实际上是期望包含(不反转判断结果)
     */
    @Deprecated
    public ContainsStringMatcher(String pattern, boolean reverse) {
        this(pattern, reverse ? Matches.Negative : Matches.Positive);
    }

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     * @param mode 匹配模式: Positive=肯定模式, 符合条件为匹配; Negative=否定模式, 不符合条件为匹配
     */
    public ContainsStringMatcher(String pattern, Matches mode) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        this.pattern = pattern;
        this.reverse = mode == Matches.Negative;
    }

    /**
     * 判断字符串中是否存在此规则指定的子字符串<br>
     * 与匹配模式有关: Positive=肯定模式, 符合条件为匹配; Negative=否定模式, 不符合条件为匹配
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
