package com.gitee.qdbp.able.matches;

import com.gitee.qdbp.tools.utils.StringTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * StringMatcher实现类的包装类
 *
 * @author zhaohuihua
 * @version 20200229
 */
public class WrapStringMatcher implements StringMatcher {

    private final StringMatcher matcher;

    public WrapStringMatcher(String pattern) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        this.matcher = parseMatcher(pattern);
    }

    @Override
    public boolean matches(String source) {
        return matcher.matches(source);
    }

    @Override
    public String toString() {
        return this.matcher.toString();
    }

    /**
     * 解析StringMatcher规则<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * 其余的也解析为EqualsStringMatcher<br>
     * 
     * @param pattern 匹配规则
     * @return StringMatcher
     */
    public static StringMatcher parseMatcher(String pattern) {
        if (pattern.startsWith("regexp:")) {
            String value = StringTools.removePrefix(pattern, "regexp:");
            return new RegexpStringMatcher(value, false);
        } else if (pattern.startsWith("regexp!:")) {
            String value = StringTools.removePrefix(pattern, "regexp!:");
            return new RegexpStringMatcher(value, true);
        } else if (pattern.startsWith("ant:")) {
            String value = StringTools.removePrefix(pattern, "ant:");
            return new AntStringMatcher(value, true, false);
        } else if (pattern.startsWith("ant!:")) {
            String value = StringTools.removePrefix(pattern, "ant!:");
            return new AntStringMatcher(value, true, true);
        } else if (pattern.startsWith("ant:^")) {
            String value = StringTools.removePrefix(pattern, "ant:^");
            return new AntStringMatcher(value, false, false);
        } else if (pattern.startsWith("ant!:^")) {
            String value = StringTools.removePrefix(pattern, "ant!:");
            return new AntStringMatcher(value, false, true);
        } else if (pattern.startsWith("equals:")) {
            String value = StringTools.removePrefix(pattern, "equals:");
            return new AntStringMatcher(value, false);
        } else if (pattern.startsWith("equals!:")) {
            String value = StringTools.removePrefix(pattern, "equals!:");
            return new AntStringMatcher(value, true);
        } else {
            return new EqualsStringMatcher(pattern, false);
        }
    }

}
