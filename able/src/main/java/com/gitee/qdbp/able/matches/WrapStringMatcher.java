package com.gitee.qdbp.able.matches;

import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.tools.utils.StringTools;

/**
 * StringMatcher多规则的包装类
 *
 * @author zhaohuihua
 * @version 20200229
 */
public class WrapStringMatcher implements StringMatcher {

    /** 多个匹配规则使用and还是or关联 **/
    public static enum LogicType {
        AND, OR
    }

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
                this.matchers.add(parseMatcher(pattern));
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
     * 其余的也解析为ContainsStringMatcher<br>
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
        } else if (pattern.startsWith("equals:")) {
            String value = StringTools.removePrefix(pattern, "equals:");
            return new EqualsStringMatcher(value, false);
        } else if (pattern.startsWith("equals!:")) {
            String value = StringTools.removePrefix(pattern, "equals!:");
            return new EqualsStringMatcher(value, true);
        } else if (pattern.startsWith("contains:")) {
            String value = StringTools.removePrefix(pattern, "contains:");
            return new ContainsStringMatcher(value, false);
        } else if (pattern.startsWith("contains!:")) {
            String value = StringTools.removePrefix(pattern, "contains!:");
            return new ContainsStringMatcher(value, true);
        } else {
            return new ContainsStringMatcher(pattern, false);
        }
    }

}
