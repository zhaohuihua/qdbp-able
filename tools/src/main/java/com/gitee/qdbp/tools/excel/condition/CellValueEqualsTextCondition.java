package com.gitee.qdbp.tools.excel.condition;

import java.util.List;
import com.gitee.qdbp.able.utils.VerifyTools;

public class CellValueEqualsTextCondition extends CellValueCondition {

    public CellValueEqualsTextCondition() {
    }

    public CellValueEqualsTextCondition(Item... items) {
        super(items);
    }

    public CellValueEqualsTextCondition(List<Item> items) {
        super(items);
    }

    protected boolean isMatches(Object value, String text) {
        String string = value == null ? null : value.toString();
        if (VerifyTools.isBlank(string)) {
            return VerifyTools.isBlank(text) || text.equals("NULL");
        } else {
            return string.equals(text);
        }
    }

}
