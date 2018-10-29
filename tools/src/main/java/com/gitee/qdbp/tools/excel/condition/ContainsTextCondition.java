package com.gitee.qdbp.tools.excel.condition;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.excel.utils.ExcelTools;
import com.gitee.qdbp.tools.utils.ConvertTools;

public class ContainsTextCondition implements MatchesRowCondition {

    /** 日志对象 **/
    private static final Logger log = LoggerFactory.getLogger(IndexRangeCondition.class);

    // skip.row.when = 1:小计, 10:元
    private List<Item> conditions;

    public ContainsTextCondition() {
    }

    public ContainsTextCondition(Item... items) {
        this.conditions = ConvertTools.toList(items);
    }

    // 1:小计, 10:元
    public ContainsTextCondition(String string) {
        if (VerifyTools.isNotBlank(string)) {
            List<Item> conditions = new ArrayList<>();
            String[] items = StringTools.split(string, ',');
            for (String item : items) {
                int index = string.indexOf(':');
                if (index < 0) {
                    log.warn("ColumnValueConditionError: " + item);
                    continue;
                }
                String column = string.substring(0, index);
                String text = string.substring(index + 1);
                if (VerifyTools.isAnyBlank(column, text)) {
                    log.warn("ColumnValueConditionError: " + item);
                    continue;
                }
                try {
                    int c = ConvertTools.toInteger(column);
                    conditions.add(new Item(c, text));
                } catch (Exception e) {
                    log.warn("ColumnValueConditionError: " + item);
                    continue;
                }
            }
            this.conditions = conditions;
        }
    }

    /** {@inheritDoc} **/
    @Override
    public boolean isMatches(Row row) {
        if (VerifyTools.isBlank(this.conditions)) {
            return false;
        }
        try {
            for (Item item : this.conditions) {
                Cell cell = row.getCell(item.getColumn());
                Object value = cell == null ? null : ExcelTools.getCellValue(cell);
                if (!contains(value, item.getText())) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean contains(Object value, String text) {
        String string = value == null ? null : value.toString();
        if (VerifyTools.isBlank(string)) {
            return VerifyTools.isBlank(text) || text.equals("NULL");
        } else {
            return string.contains(text);
        }
    }

    public List<Item> getConditions() {
        return conditions;
    }

    public void setConditions(List<Item> conditions) {
        this.conditions = conditions;
    }

    public void addCondition(Item item) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }
        this.conditions.add(item);
    }

    public void addCondition(int column, String text) {
        if (this.conditions == null) {
            this.conditions = new ArrayList<>();
        }
        this.conditions.add(new Item(column, text));
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

}
