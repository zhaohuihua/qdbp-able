package com.gitee.zhaohuihua.core.model.field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 基础字段容器
 *
 * @author zhaohuihua
 * @version 180503
 */
abstract class BaseFields implements Fields, Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    protected List<FieldItem> fields;

    protected BaseFields() {
        this.fields = new ArrayList<>();
    }

    protected BaseFields(List<FieldItem> fields) {
        this.fields = fields;
        for (FieldItem field : fields) {
            Objects.requireNonNull(field.getName(), "field.name");
        }
    }

    @Override
    public List<String> getNames() {
        List<String> fields = new ArrayList<>();
        for (FieldItem field : this.fields) {
            fields.add(field.getName());
        }
        return fields;
    }

    public List<FieldItem> getItems() {
        return Collections.unmodifiableList(this.fields);
    }

    public void setItems(List<FieldItem> fields) {
        this.fields.clear();
        this.fields.addAll(fields);
    }

    protected FieldItem get(String name) {
        Objects.requireNonNull(name, "name");

        Iterator<FieldItem> itr = this.fields.iterator();
        while (itr.hasNext()) {
            FieldItem item = itr.next();
            if (name.equals(item.getName())) {
                return item;
            }
        }
        return null;
    }

    protected void add(FieldItem... fields) {
        Objects.requireNonNull(fields, "fields");
        for (FieldItem field : fields) {
            Objects.requireNonNull(field.getName(), "field.name");
            this.fields.add(field);
        }
    }

    protected void add(String name, String text) {
        Objects.requireNonNull(name, "name");
        this.fields.add(new FieldItem(name, text));
    }

    protected void del(String... names) {
        Objects.requireNonNull(names, "names");

        Iterator<FieldItem> itr = this.fields.iterator();
        while (itr.hasNext()) {
            for (String name : names) {
                FieldItem item = itr.next();
                if (name.equals(item.getName())) {
                    itr.remove();
                    break;
                }
            }
        }
    }
}
