package com.gitee.qdbp.tools.excel.json;

import com.gitee.qdbp.tools.excel.XMetadata;

// field:users, excel:user.xlsx[MainSheet], id:1, header:1
public class ToJsonOptions extends XMetadata {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    private String fieldName;
    private String excelFile;
    private String idColumn;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(String excelFile) {
        this.excelFile = excelFile;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }
}
