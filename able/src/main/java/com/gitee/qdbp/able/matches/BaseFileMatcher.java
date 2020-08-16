package com.gitee.qdbp.able.matches;

import java.io.File;
import com.gitee.qdbp.tools.files.PathTools;

/**
 * 正则表达式文件匹配类
 *
 * @author zhaohuihua
 * @version 20200816
 */
public class BaseFileMatcher implements FileMatcher {

    /** 匹配规则 **/
    private final StringMatcher matcher;
    /** 匹配目标 **/
    private final Target target;

    /**
     * 构造函数
     * 
     * @param matcher 匹配规则
     * @param target 匹配目标, 是根据文件名还是文件路径进行比对
     */
    public BaseFileMatcher(StringMatcher matcher, Target target) {
        this.matcher = matcher;
        this.target = target;
    }

    /**
     * 判断文件是否符合匹配规则<br>
     * 与匹配目标有关: FileName=与文件名比较; FilePath=与文件路径比较<br>
     * 与匹配模式有关: Positive=肯定模式, 符合条件为匹配; Negative=否定模式, 不符合条件为匹配
     * 
     * @param file 文件
     * @return 是否匹配
     */
    @Override
    public boolean matches(File file) {
        String source = formatFilePath(file);
        return this.matcher.matches(source);
    }

    protected String formatFilePath(File file) {
        if (this.target != Target.FilePath) {
            return file.getName();
        }
        // 路径转换为/分隔符, 方便windows/linux统一处理
        String filePath = PathTools.formatPath(file.getAbsolutePath());
        // 如果是文件夹, 固定以/结尾
        if (file.isDirectory() && !filePath.endsWith("/")) {
            filePath += "/";
        }
        if (!filePath.startsWith("/") && filePath.charAt(1) == ':') {
            // windows文件, 去掉盘符, 如D:/home/files, 只取/home/files
            // 如果保留盘符, ant规则不好处理
            filePath = filePath.substring(2);
        }
        return filePath;
    }

    /** 匹配目标, 是根据文件名还是文件路径进行比对 **/
    public Target getTarget() {
        return this.target;
    }

    /** 匹配规则 **/
    public StringMatcher getMatcher() {
        return this.matcher;
    }

    @Override
    public String toString() {
        return (this.target == Target.FilePath ? "path:" : "name:") + this.matcher.toString();
    }
}
