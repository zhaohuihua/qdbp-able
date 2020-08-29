package com.gitee.qdbp.able.matches;

import com.gitee.qdbp.able.matches.StringMatcher.Matches;

/**
 * Ant模式的文件匹配类
 *
 * @author zhaohuihua
 * @version 20200816
 * @since 5.1.0
 */
public class AntFileMatcher extends BaseFileMatcher {

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     * @param target 匹配目标, 是根据文件名还是文件路径进行比对
     */
    public AntFileMatcher(String pattern, Target target) {
        this(pattern, target, Matches.Positive);
    }

    /**
     * 构造函数
     * 
     * @param pattern 匹配规则
     * @param target 匹配目标, 是根据文件名还是文件路径进行比对
     * @param mode 匹配模式: Positive=肯定模式, 符合条件为匹配; Negative=否定模式, 不符合条件为匹配
     */
    public AntFileMatcher(String pattern, Target target, Matches mode) {
        super(new AntStringMatcher(pattern, true, mode), target);
    }
}
