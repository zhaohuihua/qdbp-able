package com.gitee.qdbp.able.model.field;

/**
 * 导入型字段子集
 *
 * @author zhaohuihua
 * @version 180503
 */
class IncludeFields extends FilterFields {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    public IncludeFields(AllFields all, String... names) {
        super(all);
        if (names != null) super.include(names);
    }
}
