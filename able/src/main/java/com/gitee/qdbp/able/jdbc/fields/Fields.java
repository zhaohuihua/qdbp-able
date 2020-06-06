package com.gitee.qdbp.able.jdbc.fields;

import java.util.List;

/**
 * 字段容器
 *
 * @author zhaohuihua
 * @version 20180503
 */
public interface Fields {

    Fields ALL = new AllFields();

    List<String> getItems();
}
