package com.gitee.qdbp.able.matches;

import java.util.regex.Pattern;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 正则表达式替换
 *
 * @author zhaohuihua
 * @version 20200217
 */
public class RegexpStringReplacer implements StringReplacer {

    /** 匹配规则 **/
    private final Pattern pattern;
    /** 替换内容 **/
    private final String replacement;

    /**
     * 构造函数
     * 
     * @param pattern 正则表达式
     * @param replacement 替换内容
     */
    public RegexpStringReplacer(String pattern, String replacement) {
        VerifyTools.requireNotBlank(pattern, "pattern");
        VerifyTools.requireNonNull(replacement, "replacement");
        this.pattern = Pattern.compile(pattern);
        this.replacement = replacement;
    }

    /**
     * 构造函数
     * 
     * @param pattern 正则表达式
     * @param replacement 替换内容
     */
    public RegexpStringReplacer(Pattern pattern, String replacement) {
        VerifyTools.requireNonNull(pattern, "pattern");
        VerifyTools.requireNonNull(replacement, "replacement");
        this.pattern = pattern;
        this.replacement = replacement;
    }

    /**
     * 替换字符串
     * 
     * @param string 待替换的源字符串
     * @return 替换后的字符串
     */
    @Override
    public String replace(String string) {
        return pattern.matcher(string).replaceAll(replacement);
    }

    @Override
    public String toString() {
        return "regexp:" + pattern + " --> " + replacement;
    }

}
