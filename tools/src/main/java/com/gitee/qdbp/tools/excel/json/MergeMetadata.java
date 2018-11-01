package com.gitee.qdbp.tools.excel.json;

import com.gitee.qdbp.tools.excel.XMetadata;

/**
 * 基础合并参数
 *
 * @author zhaohuihua
 * @version 181101
 */
public class MergeMetadata extends XMetadata {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /** Excel文件路径 **/
    private String fileName;
    /** ID字段名 **/
    private String idField;

    /** Excel文件路径 **/
    public String getFileName() {
        return fileName;
    }

    /** Excel文件路径 **/
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** ID字段名 **/
    public String getIdField() {
        return idField;
    }

    /** ID字段名 **/
    public void setIdField(String idField) {
        this.idField = idField;
    }

    /**
     * 将当前对象转换为子类对象
     *
     * @param clazz 目标类型
     * @return 目标对象
     */
    public <T extends XMetadata> T to(Class<T> clazz) {
        T instance = super.to(clazz);

        if (instance instanceof MergeMetadata) {
            MergeMetadata real = (MergeMetadata) instance;
            real.setFileName(this.getFileName()); // Excel文件路径
            real.setIdField(this.getIdField()); // ID字段名
        }
        return instance;
    }
}
