package com.gitee.qdbp.able.matches;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.able.matches.StringMatcher.Matches;
import com.gitee.qdbp.tools.utils.StringTools;

/**
 * FileMatcher多规则的包装类
 *
 * @author zhaohuihua
 * @version 20200816
 * @since 5.1.0
 */
public class WrapFileMatcher implements FileMatcher {

    /** 多个匹配规则使用and还是or关联 **/
    public static enum LogicType {
        AND, OR
    }

    private List<FileMatcher> matchers;
    /** 多个匹配规则使用and还是or关联 **/
    private LogicType logicType;

    public WrapFileMatcher(String... patterns) {
        this.logicType = LogicType.AND;
        this.addMatchers(patterns);
    }

    public WrapFileMatcher(LogicType logicType, String... patterns) {
        this.logicType = logicType;
        this.addMatchers(patterns);
    }

    public WrapFileMatcher(FileMatcher... matchers) {
        this.logicType = LogicType.AND;
        this.addMatcher(matchers);
    }

    public WrapFileMatcher(LogicType logicType, FileMatcher... matchers) {
        this.logicType = logicType;
        this.addMatcher(matchers);
    }

    @Override
    public boolean matches(File file) {
        if (this.matchers == null) {
            // matchers=null和matchers是空数组效果一致
            return this.logicType == LogicType.AND ? true : false;
        }

        for (FileMatcher matcher : this.matchers) {
            boolean matches = matcher.matches(file);
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
        for (FileMatcher matcher : this.matchers) {
            if (buffer.length() > 0) {
                buffer.append(' ').append(logicType).append(' ');
            }
            buffer.append(matcher.toString());
        }
        return buffer.toString();
    }

    /** 获取匹配规则 **/
    public List<FileMatcher> getMatchers() {
        return matchers;
    }

    /** 设置匹配规则 **/
    public void setMatchers(List<FileMatcher> matchers) {
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
    public void addMatcher(FileMatcher... matchers) {
        if (this.matchers == null) {
            this.matchers = new ArrayList<>();
        }
        if (matchers != null && matchers.length > 0) {
            for (FileMatcher matcher : matchers) {
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
     * 其余的解析为EqualsStringMatcher<br>
     * 
     * @param pattern 匹配规则
     * @return StringMatcher
     */
    public static FileMatcher parseMatcher(String pattern) {
        return parseMatcher(pattern, true);
    }

    /**
     * 解析FileMatcher规则<br>
     * <br>
     * 例如: name:ant:*.txt, 表示用fileName匹配ant规则*.txt<br>
     * 例如: path:contains!:bak, 表示filePath不含有bak的<br>
     * <br>
     * 第1段, name或path表示匹配目标, 是根据文件名还是文件路径进行比对<br>
     * 第2段, 匹配规则:<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * 其余的, 严格模式下解析为EqualsStringMatcher, 否则解析为ContainsStringMatcher<br>
     * 
     * @param pattern 匹配规则
     * @param strict 是否使用严格格式
     * @return StringMatcher
     */
    public static FileMatcher parseMatcher(String pattern, boolean strict) {
        Target target = Target.FileName;
        if (pattern.startsWith("name:")) {
            target = Target.FileName;
            pattern = StringTools.removePrefix(pattern, "name:");
        } else if (pattern.startsWith("file:")) {
            target = Target.FileName;
            pattern = StringTools.removePrefix(pattern, "file:");
        } else if (pattern.startsWith("path:")) {
            target = Target.FilePath;
            pattern = StringTools.removePrefix(pattern, "path:");
        }
        if (pattern.startsWith("regexp:")) {
            String value = StringTools.removePrefix(pattern, "regexp:");
            return new BaseFileMatcher(new RegexpStringMatcher(value, Matches.Positive), target);
        } else if (pattern.startsWith("regexp!:")) {
            String value = StringTools.removePrefix(pattern, "regexp!:");
            return new BaseFileMatcher(new RegexpStringMatcher(value, Matches.Negative), target);
        } else if (pattern.startsWith("ant:")) {
            String value = StringTools.removePrefix(pattern, "ant:");
            return new BaseFileMatcher(new AntStringMatcher(value, true, Matches.Positive), target);
        } else if (pattern.startsWith("ant!:")) {
            String value = StringTools.removePrefix(pattern, "ant!:");
            return new BaseFileMatcher(new AntStringMatcher(value, true, Matches.Negative), target);
        } else if (pattern.startsWith("equals:")) {
            String value = StringTools.removePrefix(pattern, "equals:");
            return new BaseFileMatcher(new EqualsStringMatcher(value, Matches.Positive), target);
        } else if (pattern.startsWith("equals!:")) {
            String value = StringTools.removePrefix(pattern, "equals!:");
            return new BaseFileMatcher(new EqualsStringMatcher(value, Matches.Negative), target);
        } else if (pattern.startsWith("contains:")) {
            String value = StringTools.removePrefix(pattern, "contains:");
            return new BaseFileMatcher(new ContainsStringMatcher(value, Matches.Positive), target);
        } else if (pattern.startsWith("contains!:")) {
            String value = StringTools.removePrefix(pattern, "contains!:");
            return new BaseFileMatcher(new ContainsStringMatcher(value, Matches.Negative), target);
        } else {
            if (strict) {
                return new BaseFileMatcher(new EqualsStringMatcher(pattern, Matches.Positive), target);
            } else {
                return new BaseFileMatcher(new ContainsStringMatcher(pattern, Matches.Positive), target);
            }
        }
    }

}
