package com.gitee.zhaohuihua.core.model.field;

import java.util.List;
import java.util.Objects;

public class AllFields extends BaseFields {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    private boolean readonly;

    public AllFields() {
        super();
        this.readonly = false;
    }

    public AllFields(List<FieldItem> fields) {
        super(fields);
    }

    public void readonly() {
        this.readonly = true;
    }

    public FilterFields include(String... names) {
        Objects.requireNonNull(names, "names");

        return new IncludeFields(this, names);
    }

    public FilterFields exclude(String... names) {
        Objects.requireNonNull(names, "names");

        return new ExcludeFields(this, names);
    }

    public FieldItem get(String name) {
        return super.get(name);
    }

    public void setItems(List<FieldItem> fields) {
        if (this.readonly) {
            throw new UnsupportedOperationException();
        }
        super.setItems(fields);
    }

    public void add(FieldItem... fields) {
        if (this.readonly) {
            throw new UnsupportedOperationException();
        }
        super.add(fields);
    }

    public void add(String name, String text) {
        if (this.readonly) {
            throw new UnsupportedOperationException();
        }
        super.add(name, text);
    }

    public void del(String... names) {
        if (this.readonly) {
            throw new UnsupportedOperationException();
        }
        super.del(names);
    }
}
