package com.gitee.qdbp.tools.excel.condition;

import java.util.List;
import com.gitee.qdbp.able.utils.VerifyTools;

/**
 * 单元格值包含指定文本的判断条件
 *
 * @author zhaohuihua
 * @version 181104
 */
public class CellValueContainsTextCondition extends CellValueCondition {

    public CellValueContainsTextCondition() {
    }

    public CellValueContainsTextCondition(Item... items) {
        super(items);
    }

    public CellValueContainsTextCondition(List<Item> items) {
        super(items);
    }

    protected boolean isMatches(Object value, String text) {
        String string = value == null ? null : value.toString();
        if (VerifyTools.isBlank(string)) {
            return VerifyTools.isBlank(text) || text.equals("NULL");
        } else {
            return string.contains(text);
        }
    }

}
