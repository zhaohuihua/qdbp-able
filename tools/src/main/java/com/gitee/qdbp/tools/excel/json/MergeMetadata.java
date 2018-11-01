package com.gitee.qdbp.tools.excel.json;

/**
 * 合并选项接口
 *
 * @author zhaohuihua
 * @version 181101
 */
public interface MergeMetadata {

    /** Excel文件路径 **/
    String getFileName();

    /** ID所在的列号 **/
    Integer getIdColumn();
}
