package com.gitee.qdbp.able.model.field;

/**
 * 排除型字段子集
 *
 * @author zhaohuihua
 * @version 180503
 */
class ExcludeFields extends FilterFields {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    public ExcludeFields(AllFields all, String... names) {
        super(all, all.fields);
        if (names != null) super.exclude(names);
    }
}
