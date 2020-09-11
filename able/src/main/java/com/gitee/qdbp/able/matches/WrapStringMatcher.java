package com.gitee.qdbp.able.matches;

import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.StringTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * StringMatcher多规则的包装类
 *
 * @author zhaohuihua
 * @version 20200229
 */
public class WrapStringMatcher implements StringMatcher {

    private List<StringMatcher> matchers;
    /** 多个匹配规则使用and还是or关联 **/
    private LogicType logicType;

    public WrapStringMatcher(String... patterns) {
        this.logicType = LogicType.AND;
        this.addMatchers(patterns);
    }

    public WrapStringMatcher(LogicType logicType, String... patterns) {
        this.logicType = logicType;
        this.addMatchers(patterns);
    }

    public WrapStringMatcher(StringMatcher... matchers) {
        this.logicType = LogicType.AND;
        this.addMatcher(matchers);
    }

    public WrapStringMatcher(LogicType logicType, StringMatcher... matchers) {
        this.logicType = logicType;
        this.addMatcher(matchers);
    }

    @Override
    public boolean matches(String source) {
        if (this.matchers == null) {
            // matchers=null和matchers是空数组效果一致
            return this.logicType == LogicType.AND ? true : false;
        }

        for (StringMatcher matcher : this.matchers) {
            boolean matches = matcher.matches(source);
            if (this.logicType == LogicType.AND) {
                if (!matches) {
                    // AND说明必须所有的全部匹配才返回true, 那么遇到不匹配的就直接返回false
                    return false;
                }
            } else {
                if (matches) {
                    // OR说明只要有一个匹配就直接返回false
                    return true;
                }
            }
        }
        // 走到这里, 如果是AND, 说明全部匹配了; 如果是OR, 说明全部未匹配
        return this.logicType == LogicType.AND ? true : false;
    }

    @Override
    public String toString() {
        if (this.matchers == null || this.matchers.isEmpty()) {
            return "NULL";
        }
        StringBuilder buffer = new StringBuilder();
        String logicType = this.logicType == null ? "OR" : this.logicType.name();
        for (StringMatcher matcher : this.matchers) {
            if (buffer.length() > 0) {
                buffer.append(' ').append(logicType).append(' ');
            }
            buffer.append(matcher.toString());
        }
        return buffer.toString();
    }

    /** 获取匹配规则 **/
    public List<StringMatcher> getMatchers() {
        return matchers;
    }

    /** 设置匹配规则 **/
    public void setMatchers(List<StringMatcher> matchers) {
        this.matchers = matchers;
    }

    /** 增加匹配规则 **/
    public void addMatchers(String... patterns) {
        if (this.matchers == null) {
            this.matchers = new ArrayList<>();
        }
        if (patterns != null && patterns.length > 0) {
            for (String pattern : patterns) {
                this.matchers.add(parseMatcher(pattern, true));
            }
        }
    }

    /** 增加匹配规则 **/
    public void addMatcher(StringMatcher... matchers) {
        if (this.matchers == null) {
            this.matchers = new ArrayList<>();
        }
        if (matchers != null && matchers.length > 0) {
            for (StringMatcher matcher : matchers) {
                this.matchers.add(matcher);
            }
        }
    }

    /** 多个匹配规则使用and还是or关联 **/
    public LogicType getLogicType() {
        return logicType;
    }

    /** 多个匹配规则使用and还是or关联 **/
    public void setLogicType(LogicType logicType) {
        this.logicType = logicType;
    }

    /**
     * 解析StringMatcher规则<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * starts:开头的解析为StartsStringMatcher<br>
     * ends:开头的解析为EndsStringMatcher<br>
     * 其余的解析为EqualsStringMatcher<br>
     * 
     * @param pattern 匹配规则
     * @return StringMatcher
     */
    public static StringMatcher parseMatcher(String pattern) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        return parseMatcher(pattern, "equals");
    }

    /**
     * 解析StringMatcher规则<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * starts:开头的解析为StartsStringMatcher<br>
     * ends:开头的解析为EndsStringMatcher<br>
     * 其余的, 严格模式下解析为EqualsStringMatcher, 否则解析为ContainsStringMatcher<br>
     * 
     * @param pattern 匹配规则
     * @param strict 是否使用严格格式
     * @return StringMatcher
     */
    public static StringMatcher parseMatcher(String pattern, boolean strict) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        return parseMatcher(pattern, strict ? "equals" : "contains");
    }

    /**
     * 解析StringMatcher规则<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * starts:开头的解析为StartsStringMatcher<br>
     * ends:开头的解析为EndsStringMatcher<br>
     * 其余的, 使用defaultMode指定的匹配方式<br>
     * 
     * @param pattern 匹配规则
     * @param defaultMode 默认匹配方式
     * @return StringMatcher
     * @since 5.1.1
     */
    public static StringMatcher parseMatcher(String pattern, String defaultMode) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        if (pattern.startsWith("regexp:")) {
            String value = StringTools.removePrefix(pattern, "regexp:");
            return new RegexpStringMatcher(value, Matches.Positive);
        } else if (pattern.startsWith("regexp!:")) {
            String value = StringTools.removePrefix(pattern, "regexp!:");
            return new RegexpStringMatcher(value, Matches.Negative);
        } else if (pattern.startsWith("ant:")) {
            String value = StringTools.removePrefix(pattern, "ant:");
            return new AntStringMatcher(value, true, Matches.Positive);
        } else if (pattern.startsWith("ant!:")) {
            String value = StringTools.removePrefix(pattern, "ant!:");
            return new AntStringMatcher(value, true, Matches.Negative);
        } else if (pattern.startsWith("equals:")) {
            String value = StringTools.removePrefix(pattern, "equals:");
            return new EqualsStringMatcher(value, Matches.Positive);
        } else if (pattern.startsWith("equals!:")) {
            String value = StringTools.removePrefix(pattern, "equals!:");
            return new EqualsStringMatcher(value, Matches.Negative);
        } else if (pattern.startsWith("contains:")) {
            String value = StringTools.removePrefix(pattern, "contains:");
            return new ContainsStringMatcher(value, Matches.Positive);
        } else if (pattern.startsWith("contains!:")) {
            String value = StringTools.removePrefix(pattern, "contains!:");
            return new ContainsStringMatcher(value, Matches.Negative);
        } else if (pattern.startsWith("starts:")) {
            String value = StringTools.removePrefix(pattern, "starts:");
            return new StartsStringMatcher(value, Matches.Positive);
        } else if (pattern.startsWith("starts!:")) {
            String value = StringTools.removePrefix(pattern, "starts!:");
            return new StartsStringMatcher(value, Matches.Negative);
        } else if (pattern.startsWith("ends:")) {
            String value = StringTools.removePrefix(pattern, "ends:");
            return new EndsStringMatcher(value, Matches.Positive);
        } else if (pattern.startsWith("ends!:")) {
            String value = StringTools.removePrefix(pattern, "contains!:");
            return new EndsStringMatcher(value, Matches.Negative);
        } else {
            if ("ant".equals(defaultMode)) {
                return new AntStringMatcher(pattern, true, Matches.Positive);
            } else if ("regexp".equals(defaultMode)) {
                return new RegexpStringMatcher(pattern, Matches.Positive);
            } else if ("equals".equals(defaultMode)) {
                return new EqualsStringMatcher(pattern, Matches.Positive);
            } else if ("contains".equals(defaultMode)) {
                return new ContainsStringMatcher(pattern, Matches.Positive);
            } else if ("starts".equals(defaultMode)) {
                return new StartsStringMatcher(pattern, Matches.Positive);
            } else if ("ends".equals(defaultMode)) {
                return new EndsStringMatcher(pattern, Matches.Positive);
            } else {
                return new EqualsStringMatcher(pattern, Matches.Positive);
            }
        }
    }

    /**
     * 解析StringMatcher规则列表, 以逗号或换行符分隔<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * starts:开头的解析为StartsStringMatcher<br>
     * ends:开头的解析为EndsStringMatcher<br>
     * 其余的解析为EqualsStringMatcher<br>
     * 
     * @param patterns 匹配规则列表
     * @param logicType 多个匹配规则使用and还是or关联
     * @return StringMatcher
     * @see WrapStringMatcher#parseMatcher(String, String)
     * @since 5.1.1
     */
    public static StringMatcher parseMatchers(String patterns, LogicType logicType) {
        VerifyTools.requireNotBlank(patterns, "patterns");
        return parseMatchers(patterns, logicType, "equals", ',', '\n');
    }

    /**
     * 解析StringMatcher规则列表<br>
     * 如: parseMatchers(pattern, Logic.OR, "ant", ',', '\n'); // 默认以ant规则匹配<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * starts:开头的解析为StartsStringMatcher<br>
     * ends:开头的解析为EndsStringMatcher<br>
     * 其余的, 使用defaultMode指定的匹配方式<br>
     * 
     * @param patterns 匹配规则列表
     * @param defaultMode 默认匹配方式
     * @param chars 分隔符
     * @return StringMatcher
     * @see WrapStringMatcher#parseMatcher(String, String)
     * @since 5.1.1
     */
    public static StringMatcher parseMatchers(String patterns, LogicType logicType, String defaultMode, char... chars) {
        VerifyTools.requireNotBlank(patterns, "patterns");
        if (chars == null || chars.length == 0) {
            chars = new char[] { ',', '\n' };
        }
        String[] array = StringTools.split(patterns, chars);
        List<StringMatcher> matchers = new ArrayList<>();
        for (String pattern : array) {
            if (VerifyTools.isNotBlank(pattern)) {
                matchers.add(parseMatcher(pattern, defaultMode));
            }
        }
        if (matchers.size() == 0) {
            return null;
        } else if (matchers.size() == 1) {
            return matchers.get(0);
        } else {
            return new WrapStringMatcher(logicType, ConvertTools.toArray(matchers, StringMatcher.class));
        }
    }

}
