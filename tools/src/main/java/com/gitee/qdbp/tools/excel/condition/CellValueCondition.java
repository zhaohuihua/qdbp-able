package com.gitee.qdbp.tools.excel.condition;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import com.gitee.qdbp.tools.excel.utils.ExcelTools;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 单元格值的判断条件
 *
 * @author zhaohuihua
 * @version 181104
 */
public abstract class CellValueCondition implements MatchesRowCondition {

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
