package com.gitee.qdbp.tools.excel.condition;

import org.apache.poi.ss.usermodel.Row;

/**
 * 行匹配的判断条件
 *
 * @author zhaohuihua
 * @version 181104
 */
public interface MatchesRowCondition {

    boolean isMatches(Row row);

}
