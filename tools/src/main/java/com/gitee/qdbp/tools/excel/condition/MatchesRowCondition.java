package com.gitee.qdbp.tools.excel.condition;

import org.apache.poi.ss.usermodel.Row;

public interface MatchesRowCondition {

    boolean isMatches(Row row);

}
