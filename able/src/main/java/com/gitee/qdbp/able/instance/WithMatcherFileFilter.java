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
    /** 文件夹匹配规则 **/
    private StringMatcher folderMatcher;
    /** 文件名匹配规则 **/
    private StringMatcher fileMatcher;
    /** 是否递归查询子文件夹(只在folderMatcher为空时有效) **/
    private boolean recursive = false;
    /** 匹配文件路径还是文件名 **/
    private boolean usePath = true;

    /** 默认构造函数 **/
    public WithMatcherFileFilter() {
        this.recursive = false;
    }

    /**
     * 构造函数<br>
     * regexp:开头的解析为RegexpStringMatcher<br>
     * ant:开头的解析为AntStringMatcher<br>
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * 其余的也解析为ContainsStringMatcher<br>
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
     * equals:开头的解析为EqualsStringMatcher<br>
     * contains:开头的解析为ContainsStringMatcher<br>
     * 其余的也解析为ContainsStringMatcher<br>
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
        boolean isDirectory = file.isDirectory();
        if (isDirectory && folderMatcher == null) {
            return recursive;
        }
        String path;
        if (!usePath) {
            path = file.getName();
        } else {
            // 路径转换为/分隔符, 方便windows/linux统一处理
            path = PathTools.formatPath(file.getAbsolutePath());
            // 如果是文件夹, 固定以/结尾
            if (isDirectory && !path.endsWith("/")) {
                path += "/";
            }
            if (!path.startsWith("/") && path.charAt(1) == ':') {
                // windows文件, 去掉盘符, 如D:/home/files, 只取/home/files
                // 如果保留盘符, ant规则不好处理
                path = path.substring(2);
            }
        }
        if (isDirectory) {
            return folderMatcher.matches(path);
        } else {
            // 未设置文件匹配规则就等于遍历所有文件, 因此返回true
            return fileMatcher == null ? true : fileMatcher.matches(path);
        }
    }

    /** 文件夹匹配规则 **/
    public StringMatcher getFolderMatcher() {
        return folderMatcher;
    }

    /** 文件夹匹配规则 **/
    public void setFolderMatcher(StringMatcher folderMatcher) {
        this.folderMatcher = folderMatcher;
    }

    /** 文件名匹配规则 **/
    public StringMatcher getFileMatcher() {
        return fileMatcher;
    }

    /** 文件名匹配规则 **/
    public void setFileMatcher(StringMatcher fileMatcher) {
        this.fileMatcher = fileMatcher;
    }

    /** 是否递归查询子文件夹(只在folderMatcher为空时有效) **/
    public boolean isRecursive() {
        return recursive;
    }

    /** 是否递归查询子文件夹(只在folderMatcher为空时有效) **/
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
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
