package com.gitee.qdbp.able.jdbc.fields;

import java.util.List;

/**
 * Distinct字段子集
 *
 * @author zhaohuihua
 * @version 20200605
 */
public class DistinctFields extends IncludeFields {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    public DistinctFields(String... fields) {
        super(fields);
    }

    public DistinctFields(List<String> fields) {
        super(fields);
    }
}
