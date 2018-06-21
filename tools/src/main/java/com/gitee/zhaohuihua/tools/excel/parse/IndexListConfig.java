package com.gitee.zhaohuihua.tools.excel.parse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gitee.zhaohuihua.core.utils.StringTools;
import com.gitee.zhaohuihua.core.utils.VerifyTools;
import com.gitee.zhaohuihua.tools.utils.ConvertTools;

/**
 * 序号列表配置类<br>
 * 配置规则: * 表示全部<br>
 * 配置规则: 1|2|5-8|12<br>
 * 配置规则: !1 表示排除第1个<br>
 * 配置规则: !1|3|5 表示排除第1/3/5个<br>
 *
 * @author zhaohuihua
 * @version 160805
 */
public class IndexListConfig implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 日志对象 **/
    private static final Logger log = LoggerFactory.getLogger(IndexListConfig.class);

    /** 以!开头的是排除法配置 **/
    private static final Pattern EXCLUDE = Pattern.compile("^\\s*!\\s*");

    /** 开始序号至结束序号 **/
    private static final Pattern BETWEEN = Pattern.compile("^\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*$");

    /** 是不是全部允许 **/
    private boolean all = false;

    /** 是不是排除法 **/
    private boolean exclude = false;

    /** Index列表 **/
    private List<Integer> indexs;

    /** 默认全部允许 **/
    public IndexListConfig() {
        this.all = true;
    }

    public IndexListConfig(boolean exclude, int... index) {
        this.exclude = exclude;
        this.indexs = new ArrayList<>();
        for (int i : index) {
            this.indexs.add(i);
        }
    }

    public IndexListConfig(int... index) {
        this(false, index);
    }

    /** 解析文本规则 **/
    public IndexListConfig(String text) {
        this(text, 0);
    }

    /** 解析文本规则 **/
    public IndexListConfig(String text, int startBy) {
        parse(text, startBy);
    }

    public boolean isEnable(int index) {
        if (all) {
            return true;
        } else if (indexs == null) {
            return false;
        } else if (exclude) {
            return indexs.indexOf(index) < 0;
        } else {
            return indexs.indexOf(index) >= 0;
        }
    }

    private void parse(String text, int startBy) {

        if (VerifyTools.isBlank(text) || VerifyTools.isBlank(text.trim())) {
            return;
        }

        if ("*".equals(text.trim())) {
            this.all = true;
            return;
        }

        Matcher em = EXCLUDE.matcher(text);
        if (em.find()) {
            exclude = true;
            text = em.replaceFirst("").trim();
        }
        String[] digits = StringTools.split(text);
        List<Integer> indexs = new ArrayList<>();
        for (String digit : digits) {
            digit = digit.trim();
            if (StringTools.isDigit(digit)) {
                indexs.add(ConvertTools.toInteger(digit) - startBy);
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
                        indexs.add(i);
                    }
                } else {
                    log.warn("ExcelIndex配置错误: " + digit);
                }
            }
        }
        this.indexs = indexs;
    }

}
