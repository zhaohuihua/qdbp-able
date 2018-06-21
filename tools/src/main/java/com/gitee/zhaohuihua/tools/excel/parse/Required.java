package com.gitee.zhaohuihua.tools.excel.parse;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gitee.zhaohuihua.core.utils.VerifyTools;

/**
 * 必填字段配置解析
 *
 * @author zhaohuihua
 * @version 160805
 */
public class Required implements Serializable {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 星号开头或(*)结尾的字段为必填字段: [* 姓名] or [姓名 (*)] **/
    private static final Pattern REQUIRED = Pattern.compile("(^\\s*\\*\\s*|\\s*\\(\\*\\)\\s*$)");

    /** 字段名 **/
    private final String name;

    /** 是不是必填字段 **/
    private final boolean required;

    public Required(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    /** 获取字段名 **/
    public String getName() {
        return name;
    }

    /** 是不是必填字段 **/
    public boolean isRequired() {
        return required;
    }

    public static Required of(String text) {
        if (VerifyTools.isBlank(text)) {
            return null;
        }

        String string = text.trim();
        // 星号开头或(*)结尾的字段为必填字段
        Matcher matcher = REQUIRED.matcher(string);
        if (matcher.find()) {
            return new Required(matcher.replaceAll(""), true);
        } else {
            return new Required(string, false);
        }
    }

}
