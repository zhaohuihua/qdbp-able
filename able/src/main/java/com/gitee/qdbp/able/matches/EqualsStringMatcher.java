package com.gitee.qdbp.able.matches;

/**
 * 相等匹配
 *
 * @author zhaohuihua
 * @version 190601
 */
public class EqualsStringMatcher implements StringMatcher {

    private String pattern;

    public EqualsStringMatcher(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern argument cannot be null.");
        }
        this.pattern = pattern;
    }

    public boolean matches(String source) {
        return pattern.equals(source);
    }

    @Override
    public String toString() {
        return "equals:" + pattern;
    }
}
