package com.gitee.qdbp.able.matches;

import java.io.File;

/**
 * 文件匹配接口
 *
 * @author zhaohuihua
 * @version 20200816
 * @since 5.1.0
 */
public interface FileMatcher {

    /** 匹配目标 **/
    enum Target {
        /** 文件名 **/
        FileName,
        /** 文件路径 **/
        FilePath
    }

    boolean matches(File source);
}
