package com.gitee.qdbp.tools.excel.parse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.qdbp.able.utils.StringTools;
import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.tools.utils.ConvertTools;

/**
 * 序号范围配置类<br>
 * 配置规则: 1|2|5-8|12<br>
 *
 * @author zhaohuihua
 * @version 160805
 */
public class IndexRangeCondition implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 日志对象 **/
    private static final Logger log = LoggerFactory.getLogger(IndexRangeCondition.class);

    /** 开始序号至结束序号 **/
    private static final Pattern BETWEEN = Pattern.compile("^\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*$");

    /** Index列表 **/
    private List<Integer> indexs;

    private Integer min;

    private Integer max;

    public IndexRangeCondition(int... index) {
        this.indexs = new ArrayList<>();
        for (int i : index) {
            addIndex(i);
        }
    }

    /** 解析文本规则 **/
    public IndexRangeCondition(String text) {
        this(text, 0);
    }

    /** 解析文本规则 **/
    public IndexRangeCondition(String text, int startBy) {
        parse(text, startBy);
    }

    public boolean isEnable(int index) {
        if (indexs == null || indexs.isEmpty()) {
            return false;
        } else {
            return indexs.indexOf(index) >= 0;
        }
    }
    
    public Integer getMin() {
        return min;
    }
    
    public Integer getMax() {
        return max;
    }

    private void parse(String text, int startBy) {

        if (VerifyTools.isBlank(text) || VerifyTools.isBlank(text.trim())) {
            return;
        }

        String[] digits = StringTools.split(text);
        this.indexs = new ArrayList<>();
        for (String digit : digits) {
            digit = digit.trim();
            if (StringTools.isDigit(digit)) {
                addIndex(ConvertTools.toInteger(digit) - startBy);
            } else {
                Matcher bm = BETWEEN.matcher(digit);
                if (bm.matches()) {
                    int start = ConvertTools.toInteger(bm.group(1)) - startBy;
                    int end = ConvertTools.toInteger(bm.group(2)) - startBy;
                    if (start > end) {
                        int temp = start;
                        start = end;
                        end = temp;
                    }
                    for (int i = start; i <= end; i++) {
                        addIndex(i);
                    }
                } else {
                    log.warn("ExcelIndexError: " + digit);
                }
            }
        }
    }

    private void addIndex(int index) {
        this.indexs.add(index);
        if (min == null || min > index) {
            min = index;
        }
        if (max == null || max < index) {
            max = index;
        }
    }
}
