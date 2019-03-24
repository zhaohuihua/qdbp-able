package com.gitee.qdbp.tools.excel.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gitee.qdbp.able.utils.ConvertTools;
import com.gitee.qdbp.able.utils.DateTools;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.exception.ResultSetMismatchException;
import com.gitee.qdbp.tools.excel.model.ColumnInfo;
import com.gitee.qdbp.tools.utils.JsonTools;

/**
 * Bean分组
 *
 * @author zhaohuihua
 * @version 190317
 */
public class BeanGroup implements Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    private static final String IDX_KEY = "$I$";

    /** 名称 **/
    private String name;
    /** 别名 **/
    private String alias;
    /** 列信息 **/
    private List<ColumnInfo> columns;
    /** 数据列表 **/
    private List<DataEntry> datas;
    /** 值列表 **/
    private List<DataEntry> values;

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
            return datas.get(0).clone();
        }
    }

    /** 获取最后一条数据 **/
    public Map<String, Object> findLastData() {
        if (VerifyTools.isBlank(datas)) {
            return null;
        } else {
            return datas.get(datas.size() - 1).clone();
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
            DataEntry item = this.datas.get(i);
            if (item != null && idx.equals(item.getIndex())) {
                return item.clone();
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
        for (DataEntry data : datas) {
            result.add(data.clone());
        }
        return result;
    }

    /** 设置数据列表 **/
    public void setDatas(List<Map<String, Object>> datas) {
        this.datas = setDefIdx(datas);
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
        List<DataEntry> datas = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            String idx = String.valueOf(i + 1);
            Map<String, Object> data = new HashMap<>();
            data.put("value", values.get(i));
            datas.add(new DataEntry(idx, data));
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

    private static List<DataEntry> setDefIdx(List<Map<String, Object>> datas) {
        List<DataEntry> entries = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            Map<String, Object> data = datas.get(i);
            String idx = String.valueOf(i + 1);
            if (data.containsKey(IDX_KEY)) {
                Object def = data.remove(IDX_KEY);
                if (def != null) {
                    idx = def.toString();
                }
            }
            entries.add(new DataEntry(idx, data));
        }
        return entries;
    }

    /**
     * 如果存在指定序号的data就合并, 如果不存在就新增
     * 
     * @param idx 指定序号
     * @param data 数据
     * @param datas 数据集
     */
    private static void dataFillTo(String idx, Map<String, Object> data, List<DataEntry> datas) {
        if (VerifyTools.isBlank(idx)) {
            idx = datas == null ? "1" : String.valueOf(datas.size() + 1);
        }
        DataEntry entry = new DataEntry(idx, data);

        if (datas.isEmpty()) {
            datas.add(entry);
        } else {
            boolean exist = false;
            for (DataEntry item : datas) {
                if (idx.equals(item.getIndex())) {
                    item.putAll(data);
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                datas.add(entry);
            }
        }
    }

    private Map<String, String> newCompareDatasResources() {
        Map<String, String> resources = new HashMap<>();
        resources.put("summary.single", "The item don't match.");
        resources.put("summary.multiple", "The are {total} items in total, {mismatch} of which don't match.");
        resources.put("size.mismatch", "Size don't match, expect={expect}, actual={actual}.");
        resources.put("bean.mismatch", "[{index}]:");
        resources.put("field.mismatch", "[{field}], expect={expect}, actual={actual}");
        return resources;
    }

    /**
     * 核对数据集, 入参是源数据集, 自己是期望数据集
     * 
     * @param sourceDatas 源数据集
     * @throws ResultSetMismatchException 不匹配
     */
    public <T> void compareDatasOf(List<T> sourceDatas) throws ResultSetMismatchException {
        this.compareDatasOf(sourceDatas, newCompareDatasResources());
    }

    /**
     * 核对数据集, 入参是源数据集, 自己是期望数据集
     * 
     * @param sourceDatas 源数据集
     * @param resources 提示消息
     * @throws ResultSetMismatchException 不匹配
     */
    public <T> void compareDatasOf(List<T> sourceDatas, Map<String, String> resources)
            throws ResultSetMismatchException {
        Map<String, String> realres = newCompareDatasResources();
        if (VerifyTools.isNotBlank(resources)) {
            realres.putAll(resources);
        }

        List<DataEntry> expectDatas = this.datas;
        // 比较size
        int actualSize = sourceDatas == null ? 0 : sourceDatas.size();
        int expectSize = expectDatas == null ? 0 : expectDatas.size();
        if (actualSize != expectSize) {
            String summary = sizeMismatchSummary(realres, expectSize, actualSize);
            throw new ResultSetMismatchException(summary + newlineResultMessage(expectDatas, sourceDatas));
        }

        // 逐一比较bean
        List<String> errors = new ArrayList<>();
        List<Map<String, Object>> actualDatas = new ArrayList<>();
        for (int i = 0; i < expectSize; i++) {
            Map<String, Object> actualData = JsonTools.beanToMap(sourceDatas.get(i));
            actualDatas.add(actualData);
            DataEntry expectData = expectDatas.get(i);
            String idx = expectData.getIndex();
            List<String> s = new ArrayList<>();
            // 逐一比较fieldValue
            for (Map.Entry<String, Object> entry : expectData.entrySet()) {
                String fieldName = entry.getKey();
                if (VerifyTools.isBlank(fieldName)) {
                    continue; // 配置值没有字段名, 无法比较
                }
                Object expectValue = entry.getValue();
                Object actualValue = actualData.get(fieldName);
                if (!compareValue(expectValue, actualValue)) { // 比较字段值
                    s.add(fieldMismatchMessage(realres, fieldName, expectValue, actualValue));
                }
            }
            if (!s.isEmpty()) {
                errors.add(beanMismatchMessage(realres, idx, s));
            }
        }
        if (!errors.isEmpty()) {
            String summary = finalMismatchSummary(realres, expectSize, errors);
            throw new ResultSetMismatchException(summary + newlineResultMessage(expectDatas, actualDatas));
        }
    }

    // 1. 一个值为空另一个不为空为不匹配
    // 2. 相同类型的直接比较
    // 3. 不同类型的转换为字符串比较
    private static boolean compareValue(Object expectValue, Object actualValue) {
        if (VerifyTools.isAllBlank(expectValue, actualValue)) {
            return true; // 都为空, 判定为匹配
        }

        // 一个值为空另一个不为空为不匹配
        if (VerifyTools.isBlank(actualValue) && VerifyTools.isNotBlank(actualValue)) {
            return false;
        } else if (VerifyTools.isNotBlank(actualValue) && VerifyTools.isBlank(actualValue)) {
            return false;
        }

        if (expectValue.getClass() == actualValue.getClass()) {
            // 相同类型的直接比较
            if (VerifyTools.notEquals(expectValue, actualValue)) {
                return false;
            }
        } else { // 不同类型的转换为字符串比较
            String expectString = JsonTools.toLogString(expectValue);
            String actualString = JsonTools.toLogString(actualValue);
            if (VerifyTools.notEquals(expectString, actualString)) {
                return false;
            }
        }
        return true;
    }

    private static String fieldValueToString(Object value) {
        if (VerifyTools.isBlank(value)) {
            return "null";
        } else if (value instanceof CharSequence) {
            return value.toString();
        } else if (value instanceof Date) {
            return DateTools.toNormativeString((Date) value);
        } else {
            return JsonTools.toLogString(value);
        }
    }

    /** 查找字段名 **/
    private String findFieldName(String fieldName) {
        String realFieldName = fieldName;
        for (ColumnInfo column : this.columns) {
            if (fieldName.equals(column.getField())) {
                if (VerifyTools.isNotBlank(column.getTitle())) {
                    realFieldName = column.getTitle();
                }
                break;
            }
        }
        return realFieldName;
    }

    private String finalMismatchSummary(Map<String, String> resources, int total, List<String> errors) {
        //  summary.single = The item don't match.
        //  summary.multiple = The are {total} items in total, {mismatch} of which don't match.
        String pattern = resources.get(total > 1 ? "summary.multiple" : "summary.single");
        Map<String, Object> params = new HashMap<>();
        params.put("total", total);
        params.put("mismatch", errors.size());
        String intro = StringTools.format(pattern, params);
        String target = '[' + VerifyTools.nvl(this.getName(), "UnknowName") + ']';
        String details = ConvertTools.joinToString(errors, "\n\t");
        return StringTools.concat(' ', target, intro) + "\n\t" + details;
    }

    private String sizeMismatchSummary(Map<String, String> resources, int expect, int actual) {
        // size.mismatch = Size don't match, expect={expect}, actual={actual}.
        String pattern = resources.get("size.mismatch");
        Map<String, Object> params = new HashMap<>();
        params.put("expect", expect);
        params.put("actual", actual);
        String intro = StringTools.format(pattern, params);
        String target = '[' + VerifyTools.nvl(this.getName(), "UnknowName") + ']';
        return StringTools.concat(' ', target, intro);
    }

    private String beanMismatchMessage(Map<String, String> resources, String idx, List<String> fieldErrors) {
        String pattern = resources.get("bean.mismatch");
        Map<String, Object> params = new HashMap<>();
        params.put("index", idx);
        String intro = StringTools.format(pattern, params);
        String details = ConvertTools.joinToString(fieldErrors, "; ");
        return StringTools.concat(' ', intro, details);
    }

    private String fieldMismatchMessage(Map<String, String> resources, String field, Object expect, Object actual) {
        String pattern = resources.get("field.mismatch");
        Map<String, Object> params = new HashMap<>();
        params.put("field", findFieldName(field));
        params.put("expect", fieldValueToString(expect));
        params.put("actual", fieldValueToString(actual));
        return StringTools.format(pattern, params);
    }

    private String newlineResultMessage(List<?> expectDatas, List<?> actualDatas) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("\n\texpectResult: ").append(JsonTools.toJsonString(expectDatas));
        buffer.append("\n\tactualResult: ").append(JsonTools.toJsonString(actualDatas));
        return buffer.toString();
    }

    private static class DataEntry extends HashMap<String, Object> {

        /** serialVersionUID **/
        private static final long serialVersionUID = 1L;
        /** 序号 **/
        private String index;

        public DataEntry() {
        }

        public DataEntry(String index, Map<String, Object> data) {
            this.index = index;
            this.putAll(data);
        }

        /** 获取序号 **/
        public String getIndex() {
            return index;
        }

        /** 设置序号 **/
        public void setIndex(String index) {
            this.index = index;
        }

        public DataEntry clone() {
            DataEntry copied = new DataEntry();
            copied.setIndex(this.getIndex());
            if (!this.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> content = (Map<String, Object>) super.clone();
                copied.putAll(content);
            }
            return copied;
        }
    }
}
