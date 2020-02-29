package com.gitee.qdbp.able.instance;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import com.gitee.qdbp.able.matches.StringMatcher;
import com.gitee.qdbp.able.matches.WrapStringMatcher;

/**
 * 根据StringMatcher筛选文件的过滤器
 *
 * @author zhaohuihua
 * @version 20200229
 */
public class WithMatcherFileFilter implements FileFilter, Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    /** 文件夹匹配规则 **/
    private final StringMatcher folderMatcher;
    /** 文件名匹配规则 **/
    private final StringMatcher fileMatcher;

    /**
     * 构造函数<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * 其余的解析为EqualsStringMatcher<br>
     * 
     * @param fileMatcher 文件名匹配规则
     */
    public WithMatcherFileFilter(String fileMatcher) {
        this(null, WrapStringMatcher.parseMatcher(fileMatcher));
    }

    /**
     * 构造函数<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * 其余的解析为EqualsStringMatcher<br>
     * 
     * @param folderMatcher 文件夹匹配规则
     * @param fileMatcher 文件名匹配规则
     */
    public WithMatcherFileFilter(String folderMatcher, String fileMatcher) {
        this(WrapStringMatcher.parseMatcher(folderMatcher), WrapStringMatcher.parseMatcher(fileMatcher));
    }

    /**
     * 构造函数
     * 
     * @param fileMatcher 文件名匹配规则
     */
    public WithMatcherFileFilter(StringMatcher fileMatcher) {
        this(null, fileMatcher);
    }

    /**
     * 构造函数
     * 
     * @param folderMatcher 文件夹匹配规则
     * @param fileMatcher 文件名匹配规则
     */
    public WithMatcherFileFilter(StringMatcher folderMatcher, StringMatcher fileMatcher) {
        this.folderMatcher = folderMatcher;
        this.fileMatcher = fileMatcher;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return folderMatcher == null ? false : folderMatcher.matches(file.getAbsolutePath());
        } else {
            return fileMatcher == null ? false : fileMatcher.matches(file.getAbsolutePath());
        }
    }

    @Override
    public String toString() {
        if (folderMatcher == null && fileMatcher == null) {
            return "folderMatcher:null, fileMatcher:null";
        }
        StringBuilder buffer = new StringBuilder();
        if (folderMatcher != null) {
            buffer.append("folder").append('[').append(folderMatcher.toString()).append(']');
        }
        if (fileMatcher != null) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append("file").append('[').append(fileMatcher.toString()).append(']');
        }
        return buffer.toString();
    }

}
