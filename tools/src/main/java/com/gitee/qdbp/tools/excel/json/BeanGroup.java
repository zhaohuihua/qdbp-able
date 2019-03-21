package com.gitee.qdbp.tools.excel.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.model.ColumnInfo;

/**
 * Bean分组
 *
 * @author zhaohuihua
 * @version 190317
 */
public class BeanGroup implements Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    /** 序号的KEY **/
    private static final String IDX_KEY = "$i$";

    /** 名称 **/
    private String name;
    /** 别名 **/
    private String alias;
    /** 列信息 **/
    private List<ColumnInfo> columns;
    /** 数据列表 **/
    private List<Map<String, Object>> datas;
    /** 值列表 **/
    private List<Map<String, Object>> values;

    public BeanGroup(String name) {
        this(name, null);
    }

    public BeanGroup(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    /** 获取名称 **/
    public String getName() {
        return name;
    }

    /** 设置名称 **/
    public void setName(String name) {
        this.name = name;
    }

    /** 获取别名 **/
    public String getAlias() {
        return alias;
    }

    /** 设置别名 **/
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /** 获取列信息 **/
    public List<ColumnInfo> getColumns() {
        return columns;
    }

    /** 设置列信息 **/
    public void setColumns(List<ColumnInfo> columns) {
        this.columns = columns;
    }

    /** 增加列信息 **/
    public void addColumn(ColumnInfo column) {
        if (this.columns == null) {
            this.columns = new ArrayList<>();
        }
        this.columns.add(column);
    }

    /** 增加列信息 **/
    public void addColumns(List<ColumnInfo> columns) {
        if (this.columns == null) {
            this.columns = new ArrayList<>();
        }
        this.columns.addAll(columns);
    }

    /** 获取第一条数据的指定字段值 **/
    public Object findFirstFieldValue(String fieldName) {
        Map<String, Object> last = findFistData();
        return last == null ? null : last.get(fieldName);
    }

    /** 获取最后一条数据的指定字段值 **/
    public Object findLastFieldValue(String fieldName) {
        Map<String, Object> last = findLastData();
        return last == null ? null : last.get(fieldName);
    }

    /** 获取第一条数据 **/
    public Map<String, Object> findFistData() {
        if (VerifyTools.isBlank(datas)) {
            return null;
        } else {
            return copyAndRemoveIdx(datas.get(0));
        }
    }

    /** 获取最后一条数据 **/
    public Map<String, Object> findLastData() {
        if (VerifyTools.isBlank(datas)) {
            return null;
        } else {
            return copyAndRemoveIdx(datas.get(datas.size() - 1));
        }
    }

    /** 获取指定数据的指定字段值 **/
    public Object findFieldValue(String idx, String fieldName) {
        Map<String, Object> last = findData(idx);
        return last == null ? null : last.get(fieldName);
    }

    /** 获取指定数据 **/
    public Map<String, Object> findData(String idx) {
        if (this.datas == null) {
            return null;
        }
        for (int i = 0; i < this.datas.size(); i++) {
            Map<String, Object> item = this.datas.get(i);
            if (item != null && idx.equals(item.get(IDX_KEY))) {
                return copyAndRemoveIdx(item);
            }
        }
        return null;
    }

    /** 获取数据列表 **/
    public List<Map<String, Object>> getDatas() {
        if (this.datas == null) {
            return null;
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> data : datas) {
            result.add(copyAndRemoveIdx(data));
        }
        return result;
    }

    private Map<String, Object> copyAndRemoveIdx(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        Map<String, Object> temp = new HashMap<>();
        temp.putAll(data);
        temp.remove(IDX_KEY);
        return temp;
    }

    /** 设置数据列表 **/
    public void setDatas(List<Map<String, Object>> datas) {
        this.datas = datas;
        setDefIdx(this.datas);
    }

    /** 增加数据列表项, 如果idx已存在则合并数据 **/
    public void addData(String idx, Map<String, Object> data) {
        if (this.datas == null) {
            this.datas = new ArrayList<>();
        }
        dataFillTo(idx, data, datas);
    }

    /** 获取值列表 **/
    public List<Object> getValues() {
        List<Object> list = new ArrayList<>();
        if (VerifyTools.isNotBlank(this.values)) {
            for (Map<String, Object> map : this.values) {
                list.add(map.get("value"));
            }
        }
        return list;
    }

    /** 设置值列表 **/
    public void setValues(List<Object> values) {
        List<Map<String, Object>> datas = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Map<String, Object> data = new HashMap<>();
            data.put(IDX_KEY, String.valueOf(i + 1));
            data.put("value", values.get(i));
            datas.add(data);
        }
        this.values = datas;
    }

    /** 增加值列表项 **/
    public void addValue(String idx, Object value) {
        if (this.values == null) {
            this.values = new ArrayList<>();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("value", value);
        dataFillTo(idx, data, this.values);
    }

    private static void setDefIdx(List<Map<String, Object>> datas) {
        for (int i = 0; i < datas.size(); i++) {
            Map<String, Object> item = datas.get(i);
            if (!item.containsKey(IDX_KEY)) {
                item.put(IDX_KEY, String.valueOf(i + 1));
            }
        }
    }

    private static void dataFillTo(String idx, Map<String, Object> data, List<Map<String, Object>> datas) {
        if (VerifyTools.isBlank(idx)) {
            idx = datas == null ? "1" : String.valueOf(datas.size() + 1);
        }
        data.put(IDX_KEY, idx);

        if (datas.isEmpty()) {
            datas.add(data);
        } else {
            boolean exist = false;
            for (Map<String, Object> item : datas) {
                if (idx.equals(item.get(IDX_KEY))) {
                    item.putAll(data);
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                datas.add(data);
            }
        }
    }

}
