package com.gitee.qdbp.tools.excel.json;

import com.gitee.qdbp.tools.excel.XMetadata;

/**
 * 基础合并参数
 *
 * @author zhaohuihua
 * @version 181101
 */
public class MergeBase extends XMetadata implements MergeMetadata {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** Excel文件路径 **/
    private String fileName;
    /** ID所在的列号 **/
    private Integer idColumn;

    /** Excel文件路径 **/
    public String getFileName() {
        return fileName;
    }

    /** Excel文件路径 **/
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** ID所在的列号 **/
    public Integer getIdColumn() {
        return idColumn;
    }

    /** ID所在的列号 **/
    public void setIdColumn(Integer idColumn) {
        this.idColumn = idColumn;
    }

    /**
     * 将当前对象转换为子类对象
     *
     * @param clazz 目标类型
     * @return 目标对象
     */
    public <T extends XMetadata> T to(Class<T> clazz) {
        T instance = super.to(clazz);

        if (instance instanceof MergeBase) {
            MergeBase real = (MergeBase) instance;
            real.setFileName(this.getFileName()); // Excel文件路径
            real.setIdColumn(this.getIdColumn()); // ID所在的列号
        }
        return instance;
    }
}
