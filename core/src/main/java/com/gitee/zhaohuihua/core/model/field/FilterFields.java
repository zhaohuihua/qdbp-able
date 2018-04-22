package com.gitee.zhaohuihua.core.model.field;

import java.util.List;
import java.util.Objects;

public abstract class FilterFields extends BaseFields {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    protected AllFields all;

    protected FilterFields(AllFields all) {
        super();
        this.all = all;
    }

    protected FilterFields(AllFields all, List<FieldItem> fields) {
        super(fields);
        this.all = all;
    }

    public FilterFields include(String... names) {
        Objects.requireNonNull(names, "names");
        for (String name : names) {
            FieldItem item = this.all.get(name);
            if (item == null) {
                throw new IllegalArgumentException("Field '" + name + "' not exists.");
            } else {
                super.add(item);
            }
        }
        return this;
    }

    public FilterFields exclude(String... names) {
        super.del(names);
        return this;
    }
}
