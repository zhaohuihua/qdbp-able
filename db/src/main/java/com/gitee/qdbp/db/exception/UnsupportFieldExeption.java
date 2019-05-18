package com.gitee.qdbp.db.exception;

import java.util.List;
import com.gitee.qdbp.able.utils.ConvertTools;

/**
 * 不支持字段的异常类
 *
 * @author zhaohuihua
 * @version 190211
 */
public class UnsupportFieldExeption extends RuntimeException {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    private String message;
    private List<String> fields;

    public UnsupportFieldExeption(String message, List<String> fields) {
        super(message);
        this.message = message;
        this.fields = fields;
    }

    public UnsupportFieldExeption(List<String> fields) {
        super();
        this.fields = fields;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public List<String> getFields() {
        return this.fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String toString() {
        if (message == null && fields == null) {
            return "";
        } else if (message == null) {
            return ConvertTools.joinToString(fields);
        } else if (fields == null) {
            return message;
        } else {
            return message + ": " + ConvertTools.joinToString(fields);
        }
    }
}
