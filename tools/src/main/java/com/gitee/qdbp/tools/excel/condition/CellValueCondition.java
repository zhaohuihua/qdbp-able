package com.gitee.qdbp.tools.excel.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.utils.ExcelTools;
import com.gitee.qdbp.tools.utils.ConvertTools;

public abstract class CellValueCondition implements MatchesRowCondition {

    /** 日志对象 **/
    private static final Logger log = LoggerFactory.getLogger(IndexRangeCondition.class);

    // skip.row.when = { A:"小计", H:"元" }
    private List<Item> items;

    public CellValueCondition() {
    }

    public CellValueCondition(Item... items) {
        this.items = ConvertTools.toList(items);
    }

    public CellValueCondition(List<Item> items) {
        this.items = items;
    }

    /** 解析JSON字符串: { A:"NULL" }, { B:"小计", H:"元" }, { B:"总计", H:"元" } **/
    public static List<List<Item>> parse(String jsonString) {
        if (VerifyTools.isBlank(jsonString)) {
            return null;
        }
        if (!jsonString.startsWith("[")) {
            jsonString = "[" + jsonString + "]";
        }
        // 转换为JSON数组
        JSONArray array;
        try {
            array = JSON.parseArray(jsonString);
        } catch (Exception e) {
            log.warn("ContainsTextConditionError, json string format error: " + jsonString, e);
            return null;
        }
        // 逐一解析
        List<List<Item>> conditions = new ArrayList<>();
        for (Object i : array) {
            if (!(i instanceof JSONObject)) {
                continue;
            }
            JSONObject json = (JSONObject) i;
            if (json.isEmpty()) {
                continue;
            }
            List<Item> items = new ArrayList<>();
            List<String> keys = new ArrayList<>();
            Collections.sort(keys);
            for (String key : keys) {
                int index;
                if (StringTools.isDigit(key)) { // 数字
                    index = ConvertTools.toInteger(key);
                } else { // A,B,AA,AB之类的列名
                    index = ExcelTools.columnNameToIndex(key);
                }
                Object value = json.get(key);
                String text = value == null ? null : value.toString();
                items.add(new Item(index, text));
            }
            if (!items.isEmpty()) {
                conditions.add(items);
            }
        }

        return conditions;

    }

    /** {@inheritDoc} **/
    @Override
    public boolean isMatches(Row row) {
        if (VerifyTools.isBlank(this.items)) {
            return true;
        }
        try {
            for (Item item : this.items) {
                Cell cell = row.getCell(item.getColumn() - 1);
                Object value = cell == null ? null : ExcelTools.getCellValue(cell);
                if (!isMatches(value, item.getText())) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected abstract boolean isMatches(Object value, String text);

    public List<Item> getConditions() {
        return items;
    }

    public void setConditions(List<Item> conditions) {
        this.items = conditions;
    }

    public void addCondition(Item item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    public void addCondition(int column, String text) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(new Item(column, text));
    }

    public static class Item {

        private int column;
        private String text;

        public Item(int column, String text) {
            this.column = column;
            this.text = text;
        }

        public int getColumn() {
            return column;
        }

        public void setColumn(int column) {
            this.column = column;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public String toString() {
        if (items == null) {
            return "null";
        }
        List<String> buffer = new ArrayList<>();
        for (Item item : items) {
            if (item == null) {
                buffer.add("null");
            } else {
                String columnName = ExcelTools.columnIndexToName(item.getColumn());
                buffer.add(columnName + ":" + item.getText());
            }
        }
        return this.getClass().getSimpleName() + "[" + ConvertTools.joinToString(buffer) + "]";
    }
}
