package com.gitee.zhaohuihua.tools.excel.parse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gitee.zhaohuihua.core.utils.StringTools;
import com.gitee.zhaohuihua.core.utils.VerifyTools;

/**
 * 名称列表配置类<br>
 * 配置规则: * 表示全部<br>
 * 配置规则: 开发|测试<br>
 * 配置规则: !说明|描述 表示排除<br>
 *
 * @author zhaohuihua
 * @version 160805
 */
public class NameListConfig implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 以!开头的是排除法配置 **/
    private static final Pattern EXCLUDE = Pattern.compile("^\\s*!\\s*");

    /** 是不是全部允许 **/
    private boolean all = false;

    /** 是不是排除法 **/
    private boolean exclude = false;

    /** 名称列表 **/
    private List<String> names;

    /** 默认全部允许 **/
    public NameListConfig() {
        this.all = true;
    }

    public NameListConfig(boolean exclude, String... name) {
        this.exclude = exclude;
        this.names = new ArrayList<>();
        for (String i : name) {
            this.names.add(i);
        }
    }

    /** 解析文本规则 **/
    public NameListConfig(String text) {
        parse(text);
    }

    public boolean isEnable(String name) {
        if (all) {
            return true;
        } else if (names == null) {
            return false;
        } else if (exclude) {
            return names.indexOf(name.trim()) < 0;
        } else {
            return names.indexOf(name.trim()) >= 0;
        }
    }

    private void parse(String text) {

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
        List<String> names = new ArrayList<>();
        for (String digit : digits) {
            names.add(digit.trim());
        }
        this.names = names;
    }

}
