package com.gitee.qdbp.able.matches;

import java.util.regex.Pattern;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 正则表达式匹配<br>
 * Copy from org.apache.shiro.util.RegExPatternMatcher.<br>
 * {@code PatternMatcher} implementation that uses standard {@link java.util.regex} objects.
 *
 * @see Pattern
 * @since 1.0
 */
public class RegexpStringMatcher implements StringMatcher {

    /** 匹配规则 **/
    private final Pattern pattern;
    /** 是否反转判断结果 **/
    private final boolean reverse;

    /**
     * 构造函数
     * 
     * @param 匹配规则
     */
    public RegexpStringMatcher(String pattern) {
        this(pattern, false);
    }

    /**
     * 构造函数
     * 
     * @param 匹配规则
     */
    public RegexpStringMatcher(Pattern pattern) {
        this(pattern, false);
    }

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     * @param reverse 是否反转判断结果<br>
     *            如果reverse=false, 符合时返回true; 如果reverse=true, 不符合时返回true
     */
    public RegexpStringMatcher(String pattern, boolean reverse) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        this.pattern = Pattern.compile(pattern);
        this.reverse = reverse;
    }

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     * @param reverse 是否反转判断结果<br>
     *            如果reverse=false, 符合时返回true; 如果reverse=true, 不符合时返回true
     */
    public RegexpStringMatcher(Pattern pattern, boolean reverse) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        this.pattern = pattern;
        this.reverse = reverse;
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
        return pattern.matcher(source).find() != reverse;
    }

    @Override
    public String toString() {
        if (reverse) {
            return "regexp!:" + pattern;
        } else {
            return "regexp:" + pattern;
        }
    }
}
