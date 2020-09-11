package com.gitee.qdbp.able.matches;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.gitee.qdbp.able.matches.StringMatcher.LogicType;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.StringTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * FileMatcher多规则的包装类
 *
 * @author zhaohuihua
 * @version 20200816
 * @since 5.1.0
 */
public class WrapFileMatcher implements FileMatcher {

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
     * 解析FileMatcher规则<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * starts:开头的解析为StartsStringMatcher<br>
     * ends:开头的解析为EndsStringMatcher<br>
     * 其余的解析为EqualsStringMatcher<br>
     * 
     * @param pattern 匹配规则
     * @return FileMatcher
     */
    public static FileMatcher parseMatcher(String pattern) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        return parseMatcher(pattern, "name", "equals");
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
     * starts:开头的解析为StartsStringMatcher<br>
     * ends:开头的解析为EndsStringMatcher<br>
     * 其余的, 严格模式下解析为EqualsStringMatcher, 否则解析为ContainsStringMatcher<br>
     * 
     * @param pattern 匹配规则
     * @param strict 是否使用严格格式
     * @return FileMatcher
     */
    public static FileMatcher parseMatcher(String pattern, boolean strict) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        return parseMatcher(pattern, "name", strict ? "equals" : "contains");
    }

    /**
     * 解析FileMatcher规则<br>
     * 如: parseMatcher(pattern, "name", "ant"); // 默认按文件名以ant规则匹配<br>
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
     * starts:开头的解析为StartsStringMatcher<br>
     * ends:开头的解析为EndsStringMatcher<br>
     * 其余的, 使用defaultMode指定的匹配方式<br>
     * 
     * @param pattern 匹配规则
     * @param defaultTarget 默认匹配目标
     * @param defaultMode 默认匹配方式
     * @return FileMatcher
     * @see WrapStringMatcher#parseMatcher(String, String)
     * @since 5.1.1
     */
    public static FileMatcher parseMatcher(String pattern, String defaultTarget, String defaultMode) {
        VerifyTools.requireNotBlank(pattern, "pattern");
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
        } else {
            target = "path".equals(defaultTarget) ? Target.FilePath : Target.FileName;
        }

        StringMatcher stringMatcher = WrapStringMatcher.parseMatcher(pattern, defaultMode);
        return new BaseFileMatcher(stringMatcher, target);
    }

    /**
     * 解析FileMatcher规则列表, 以逗号或换行符分隔<br>
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
     * @return FileMatcher
     * @see WrapStringMatcher#parseMatcher(String, String)
     * @since 5.1.1
     */
    public static FileMatcher parseMatchers(String patterns, LogicType logicType) {
        VerifyTools.requireNotBlank(patterns, "patterns");
        return parseMatchers(patterns, logicType, "name", "equals", ',', '\n');
    }

    /**
     * 解析FileMatcher规则列表<br>
     * 如: parseMatcher(pattern, Logic.OR, "name", "ant", ',', '\n'); // 默认按文件名以ant规则匹配<br>
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
     * starts:开头的解析为StartsStringMatcher<br>
     * ends:开头的解析为EndsStringMatcher<br>
     * 其余的, 使用defaultMode指定的匹配方式<br>
     * 
     * @param patterns 匹配规则列表
     * @param defaultTarget 默认匹配目标
     * @param defaultMode 默认匹配方式
     * @param chars 分隔符
     * @return FileMatcher
     * @see WrapStringMatcher#parseMatcher(String, String)
     * @since 5.1.1
     */
    public static FileMatcher parseMatchers(String patterns, LogicType logicType, String defaultTarget,
            String defaultMode, char... chars) {
        VerifyTools.requireNotBlank(patterns, "patterns");
        if (chars == null || chars.length == 0) {
            chars = new char[] { ',', '\n' };
        }
        String[] array = StringTools.split(patterns, chars);
        List<FileMatcher> matchers = new ArrayList<>();
        for (String pattern : array) {
            if (VerifyTools.isNotBlank(pattern)) {
                matchers.add(parseMatcher(pattern, defaultTarget, defaultMode));
            }
        }
        if (matchers.size() == 0) {
            return null;
        } else if (matchers.size() == 1) {
            return matchers.get(0);
        } else {
            return new WrapFileMatcher(logicType, ConvertTools.toArray(matchers, FileMatcher.class));
        }
    }
}
