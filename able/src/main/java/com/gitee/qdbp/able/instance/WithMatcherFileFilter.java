package com.gitee.qdbp.able.instance;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import com.gitee.qdbp.able.matches.StringMatcher;
import com.gitee.qdbp.able.matches.WrapStringMatcher;
import com.gitee.qdbp.tools.files.PathTools;

/**
 * 根据StringMatcher筛选文件的过滤器
 *
 * @author zhaohuihua
 * @version 20200229
 */
public class WithMatcherFileFilter implements FileFilter, Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    /** 匹配规则 **/
    private StringMatcher matcher;
    /** 匹配文件路径还是文件名 **/
    private boolean usePath = true;

    /** 默认构造函数 **/
    public WithMatcherFileFilter() {
    }

    /**
     * 构造函数<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * 其余的也解析为ContainsStringMatcher<br>
     * 
     * @param matcher 文件名匹配规则
     */
    public WithMatcherFileFilter(String matcher) {
        this(WrapStringMatcher.parseMatcher(matcher, false));
    }

    /**
     * 构造函数
     * 
     * @param matcher 文件名匹配规则
     */
    public WithMatcherFileFilter(StringMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean accept(File file) {
        boolean isFolder = file.isDirectory();
        if (isFolder) {
            return false;
        } else if (matcher == null) {
            // 未设置文件匹配规则就等于遍历所有文件, 因此返回true
            return true;
        }
        String path;
        if (!usePath) {
            path = file.getName();
        } else {
            // 路径转换为/分隔符, 方便windows/linux统一处理
            path = PathTools.formatPath(file.getAbsolutePath());
            // 如果是文件夹, 固定以/结尾
            if (isFolder && !path.endsWith("/")) {
                path += "/";
            }
            if (!path.startsWith("/") && path.charAt(1) == ':') {
                // windows文件, 去掉盘符, 如D:/home/files, 只取/home/files
                // 如果保留盘符, ant规则不好处理
                path = path.substring(2);
            }
        }
        return matcher.matches(path);
    }

    /** 文件名匹配规则 **/
    public StringMatcher getMatcher() {
        return matcher;
    }

    /** 文件名匹配规则 **/
    public void setMatcher(StringMatcher matcher) {
        this.matcher = matcher;
    }

    /** 匹配文件路径还是文件名 **/
    public boolean isUsePath() {
        return usePath;
    }

    /** 匹配文件路径还是文件名 **/
    public void setUsePath(boolean usePath) {
        this.usePath = usePath;
    }

    @Override
    public String toString() {
        if (matcher == null) {
            return "matcher=null";
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append("matcher").append('=').append(matcher.toString());
        buffer.append(',').append(' ');
        buffer.append("usePath").append('=').append(usePath);
        return buffer.toString();
    }

}
