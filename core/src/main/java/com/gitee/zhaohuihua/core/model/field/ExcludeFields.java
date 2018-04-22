package com.gitee.zhaohuihua.core.model.field;

class ExcludeFields extends FilterFields {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    public ExcludeFields(AllFields all, String... names) {
        super(all, all.fields);
        if (names != null) super.exclude(names);
    }
}
