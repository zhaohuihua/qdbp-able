package com.gitee.qdbp.able.instance;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.gitee.qdbp.tools.files.PathTools;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.StringTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 根据文件扩展名筛选文件的过滤器
 *
 * @author zhaohuihua
 * @version 20200229
 */
public class WithExtensionFileFilter implements FileFilter, Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    /** 是否递归查询子文件夹 **/
    private final boolean recursive;
    /** 包含模式还是排除模式: true=包含, false=排除 **/
    private final boolean include;
    /** 文件扩展名映射表 **/
    private final Map<String, ?> extensions;

    /**
     * 构造函数
     * 
     * @param extensions 文件扩展名列表
     */
    public WithExtensionFileFilter(String... extensions) {
        this(false, true, extensions);
    }

    /**
     * 构造函数
     * 
     * @param recursive 是否递归查询子文件夹
     * @param extensions 文件扩展名列表
     */
    public WithExtensionFileFilter(boolean recursive, String... extensions) {
        this(recursive, true, extensions);
    }

    /**
     * 构造函数
     * 
     * @param recursive 是否递归查询子文件夹
     * @param include 包含模式还是排除模式: true=包含, false=排除
     * @param extensions 文件扩展名列表
     */
    public WithExtensionFileFilter(boolean recursive, boolean include, String... extensions) {
        VerifyTools.requireNotBlank(extensions, "extensions");
        this.recursive = recursive;
        this.include = include;
        this.extensions = new HashMap<>();
        for (String extension : extensions) {
            this.extensions.put(StringTools.removeLeft(extension.toLowerCase(), '.'), null);
        }
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return recursive;
        } else {
            String extension = PathTools.getExtension(file.getName(), false);
            if (extension == null) {
                return false;
            } else {
                return include == this.extensions.containsKey(extension.toLowerCase());
            }
        }
    }

    @Override
    public String toString() {
        return (include ? "include:" : "exclude:") + ConvertTools.joinToString(extensions.keySet());
    }

}
