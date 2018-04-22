package com.gitee.zhaohuihua.core.model.field;

class IncludeFields extends FilterFields {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    public IncludeFields(AllFields all, String... names) {
        super(all);
        if (names != null) super.include(names);
    }
}
