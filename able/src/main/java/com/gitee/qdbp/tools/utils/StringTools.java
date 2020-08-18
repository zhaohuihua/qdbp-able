package com.gitee.qdbp.tools.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gitee.qdbp.able.beans.KeyString;

/**
 * 字符串工具
 *
 * @author zhaohuihua
 * @version 150913
 * @since 5.0.0
 */
public abstract class StringTools {

    /** 数字正则表达式 **/
    private static final Pattern DIGIT = Pattern.compile("([0-9]*)");

    /** 手机正则表达式 **/
    private static final Pattern PHONE = Pattern.compile("(1\\d{2})(\\d{4})(\\d{4})");

    /** 邮箱正则表达式 **/
    private static final Pattern EMAIL = Pattern.compile("([-\\.\\w]+)(@)([-\\.\\w]+\\.\\w+)");

    /** 英文字符 **/
    private static final Pattern ASCII = Pattern.compile("[\\x00-\\xff]+");

    /** 网址正则表达式 **/
    private static final Pattern URL = Pattern.compile("^https?://.*");

    /** 正则表达式转义字符 **/
    private static final Pattern REG_CHAR = Pattern.compile("([\\{\\}\\[\\]\\(\\)\\^\\$\\.\\*\\?\\-\\+\\|\\\\])");

    // \\uD800\\uDC00-\\uDBFF\\uDFFF\\uD800-\\uDFFF
    private static final Pattern UTF8MB4 = Pattern.compile("[\\uD800\\uDC00-\\uDBFF\\uDFFF]");

    private static final Pattern REGEXP_TRIM = Pattern.compile("^\\s+|\\s+$");
    private static final Pattern REGEXP_TRIM_LEFT = Pattern.compile("^\\s+");
    private static final Pattern REGEXP_TRIM_RIGHT = Pattern.compile("\\s+$");

    /** 是否存在表情字符 **/
    public static boolean existEmoji(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return UTF8MB4.matcher(string).find();
    }

    /** 清除表情字符 **/
    public static String clearEmoji(String string) {
        return UTF8MB4.matcher(string).replaceAll("?");
    }

    /**
     * 判断字符串是不是手机号码
     *
     * @author zhaohuihua
     * @param string 字符串
     * @return 是不是手机号码, 如果字符串等于null或空字符串, 返回false
     */
    public static boolean isPhone(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return PHONE.matcher(string).matches();
    }

    /**
     * 判断字符串是不是邮箱地址
     *
     * @author zhaohuihua
     * @param string 字符串
     * @return 是不是邮箱地址, 如果字符串等于null或空字符串, 返回false
     */
    public static boolean isEmail(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return EMAIL.matcher(string).matches();
    }

    /**
     * 判断字符串是不是网址
     *
     * @author zhaohuihua
     * @param string 字符串
     * @return 是不是网址, 如果字符串等于null或空字符串, 返回false
     */
    public static boolean isUrl(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return URL.matcher(string).matches();
    }

    /**
     * 判断字符串是不是数字
     *
     * @author zhaohuihua
     * @param string 字符串
     * @return 是不是数字, 如果字符串等于null或空字符串, 返回false
     */
    public static boolean isDigit(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return DIGIT.matcher(string).matches();
    }

    /**
     * 判断字符串是不是英文字符
     *
     * @author zhaohuihua
     * @param string 字符串
     * @return 是不是英文字符, 如果字符串等于null或空字符串, 返回false
     */
    public static boolean isAscii(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return ASCII.matcher(string).matches();
    }

    /**
     * 格式化, 实现参数替换<br>
     * 1. 占位符具有语义性<br>
     * 2. params可以直接由JSON转换而来<br>
     *
     * <pre>
     * String ptn = "尊敬的{nickName}:感谢注册!手机号码{phone}可作为登录账号使用.";
     * Map&lt;String, Object&gt; params = (JSONObject) JSON.toJSON(user);
     * String message = StringTools.format(ptn, params);
     * </pre>
     *
     * @author zhaohuihua
     * @param string 原字符串
     * @param params 参数键值对
     * @return 参数替换后的字符串
     */
    public static String format(String string, Map<String, Object> params) {
        if (string == null || params == null || params.isEmpty()) {
            return string;
        }
        for (Map.Entry<String, Object> item : params.entrySet()) {
            String temp = item.getKey().trim();
            String key;
            if (temp.startsWith("{") && temp.endsWith("}")) {
                key = temp;
            } else {
                key = "{" + temp + "}";
            }
            // 替换正则表达式的转义字符, KEY不需要支持正则表达式
            // 如果不替换, user.id这个点就会成为通配符
            key = REG_CHAR.matcher(key).replaceAll("\\\\$1");

            Object object = item.getValue();
            String value = object == null ? "" : object.toString();
            value = REG_CHAR.matcher(value).replaceAll("\\\\$1");
            string = string.replaceAll(key, value);
        }
        return string;
    }

    /**
     * 格式化, 实现参数替换
     *
     * <pre>
     * String ptn = "尊敬的{nickName}:感谢注册!手机号码{phone}可作为登录账号使用.";
     * String message = StringTools.format(ptn, "nickName", user.getNickName(), "phone", user.getPhone());
     * </pre>
     *
     * @author zhaohuihua
     * @param string 原字符串
     * @param params 参数键值对, 参数个数必须是2的倍数
     * @return 参数替换后的字符串
     */
    public static String format(String string, Object... params) {
        if (string == null || params == null || params.length == 0) {
            return string;
        }

        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("参数必须是键值对, 参数个数必须是2的倍数");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < params.length;) {
            map.put(params[i++].toString(), params[i++]);
        }
        return format(string, map);
    }

    /**
     * 拆分字符串, 以竖杠|为分隔符<br>
     * 每一个子字符串都已经trim()过了<br>
     * "aa|bb|cc" --&gt; [aa, bb, cc]<br>
     * "aa|bb||cc" --&gt; [aa, bb, , cc]
     *
     * @param string 原字符串
     * @return 拆分后的字符串数组
     */
    public static String[] split(String string) {
        return split(string, true, '|');
    }

    /**
     * 按指定字符拆分字符串<br>
     * 每一个子字符串都已经trim()过了<br>
     * "aa|bb|cc" --&gt; [aa, bb, cc]<br>
     * "aa|bb||cc" --&gt; [aa, bb, , cc]
     *
     * @param string 原字符串
     * @param chars 分隔符
     * @return 拆分后的字符串数组
     */
    public static String[] split(String string, char... chars) {
        return split(string, true, chars);
    }

    /**
     * 按指定字符拆分字符串<br>
     * "aa|bb|cc" --&gt; [aa, bb, cc]<br>
     * "aa|bb||cc" --&gt; [aa, bb, , cc]
     *
     * @param string 原字符串
     * @param trim 每一个子字符串是否执行trim()
     * @param chars 分隔符
     * @return 拆分后的字符串数组
     */
    public static String[] split(String string, boolean trim, char... chars) {
        if (string == null) {
            return null;
        }
        if (string.length() == 0) {
            return new String[0];
        }
        if (chars == null || chars.length == 0) {
            return new String[] { string };
        }
        List<String> list = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        char[] textChars = string.toCharArray();
        boolean lastIsSplitChar = false;
        for (int i = 0; i < textChars.length; i++) {
            char c = textChars[i];
            boolean isSplitChar = false;
            for (int j = 0; j < chars.length; j++) {
                if (c == chars[j]) {
                    isSplitChar = true;
                    break;
                }
            }
            if (!isSplitChar) {
                buffer.append(c);
                lastIsSplitChar = false;
            } else {
                if (trim) {
                    list.add(buffer.toString().trim());
                } else {
                    list.add(buffer.toString());
                }
                buffer.setLength(0);
                lastIsSplitChar = true;
            }
        }
        if (buffer.length() > 0) {
            if (trim) {
                list.add(buffer.toString().trim());
            } else {
                list.add(buffer.toString());
            }
            buffer.setLength(0);
        }
        if (lastIsSplitChar) {
            list.add("");
        }
        return list.toArray(new String[0]);
    }

    /**
     * 超过指定长度则省略中间字符<br>
     * 如果未超过指定长度则返回原字符, length小于20时省略最后部分而不是中间部分<br>
     * 如: 诺贝尔奖是以瑞典著名的 ... 基金创立的
     *
     * @author zhaohuihua
     * @param string 长文本
     * @param length 指定长度
     * @return 省略中间部分的字符
     */
    public static String ellipsis(String string, int length) {
        if (string == null || string.length() <= length || length == 0) {
            return string;
        }
        String flag = " ... ";
        if (length < 20) {
            return string.substring(0, length) + flag;
        }
        int suffix = (length - flag.length()) / 4;
        int prefix = length - flag.length() - suffix;
        int end = string.length() - suffix;
        return string.substring(0, prefix) + flag + string.substring(end);
    }

    /**
     * 连接字符串
     * 
     * @param parts 字符串片断
     * @return 完整字符串
	 * @since 5.0.0
     */
    public static String concat(String... parts) {
        if (parts == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, len = parts.length; i < len; i++) {
            String part = parts[i];
            if (part != null && part.length() > 0) {
                buffer.append(part);
            }
        }
        return buffer.toString();
    }

    /**
     * 连接字符串
     * 
     * @param c 分隔符
     * @param parts 字符串片断
     * @return 完整字符串
     */
    public static String concat(char c, String... parts) {
        return concat(c, null, parts, 0, parts.length);
    }

    /**
     * 连接字符串
     * 
     * @param c 分隔符
     * @param parts 字符串片断
     * @param start 起始位置
     * @param end 结束位置
     * @return 完整字符串
     */
    public static String concat(char c, String[] parts, int start, int end) {
        return concat(c, null, parts, start, end);
    }

    /**
     * 连接字符串
     * 
     * @param c 分隔符
     * @param prefix 前缀
     * @param parts 字符串片断
     * @param start 起始位置
     * @param end 结束位置
     * @return 完整字符串
     */
    public static String concat(char c, String prefix, String[] parts, int start, int end) {
        StringBuilder buffer = new StringBuilder();
        if (prefix != null && prefix.length() > 0) {
            buffer.append(prefix);
        }
        for (int i = Math.max(start, 0), len = Math.min(end, parts.length); i < len; i++) {
            String part = parts[i];
            if (part == null || part.length() == 0) {
                continue;
            }
            if (buffer.length() == 0) {
                buffer.append(part);
            } else if (!endsWithChar(buffer, c) && !startsWithChar(part, c)) {
                buffer.append(c).append(part);
            } else if (endsWithChar(buffer, c) && endsWithChar(part, c)) {
                buffer.append(part.substring(1));
            } else {
                buffer.append(part);
            }
        }
        return buffer.toString();
    }

    /** 是不是以指定字符开头 **/
    public static boolean startsWithChar(CharSequence string, char c) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return c == string.charAt(0);
    }

    /** 是不是以指定字符结尾 **/
    public static boolean endsWithChar(CharSequence string, char c) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return c == string.charAt(string.length() - 1);
    }

    /**
     * 指定字符串是不是以空白字符开头
     * 
     * @param string 指定字符串
     * @return 是不是以空白字符开头
	 * @since 5.0.0
     */
    public static boolean startsWithAsciiWhitespace(CharSequence string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return isAsciiWhitespace(string.charAt(0));
    }

    /**
     * 指定字符串是不是以空白字符结尾
     * 
     * @param string 指定字符串
     * @return 是不是以空白字符结尾
	 * @since 5.0.0
     */
    public static boolean endsWithAsciiWhitespace(CharSequence string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return isAsciiWhitespace(string.charAt(string.length() - 1));
    }

    /**
     * 是不是英文空白字符<br>
     * Character.isWhitespace(' '); // 中文空格会返回true
     * 
     * @param c 指定字符
     * @return 是不是空白字符
	 * @since 5.0.0
     */
    public static boolean isAsciiWhitespace(char c) {
        // \r=CARRIAGE RETURN,回到行首; \n=LINE FEED,换行; \t=HORIZONTAL TABULATION,水平制表位
        // \f=FORM FEED,换页, 这个不作处理, 相当于一个不可见字符
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    /** 删除左右两侧空白字符 **/
    public static String trim(String string) {
        return string == null ? null : REGEXP_TRIM.matcher(string).replaceAll("");
    }

    /** 删除左侧空白字符 **/
    public static String trimLeft(String string) {
        return string == null ? null : REGEXP_TRIM_LEFT.matcher(string).replaceAll("");
    }

    /** 删除右侧空白字符 **/
    public static String trimRight(String string) {
        return string == null ? null : REGEXP_TRIM_RIGHT.matcher(string).replaceAll("");
    }

    /**
     * 删除左右两侧的指定字符
     * 
     * @param string 原文本
     * @param chars 待删除的字符
     * @return 删除后的字符串
     * @deprecated 方法名含义不明确, 改为removeLeftRight(String string, char... chars)
     */
    @Deprecated
    public static String remove(String string, char... chars) {
        return removeLeftRight(string, chars);
    }

    /**
     * 删除左右两侧的指定字符
     * 
     * @param string 原文本
     * @param chars 待删除的字符
     * @return 删除后的字符串
     */
    public static String removeLeftRight(String string, char... chars) {
        return removeLeftRight(string, true, true, chars);
    }

    /**
     * 删除左侧的指定字符
     * 
     * @param string 原文本
     * @param chars 待删除的字符
     * @return 删除后的字符串
     */
    public static String removeLeft(String string, char... chars) {
        return removeLeftRight(string, true, false, chars);
    }

    /**
     * 删除右侧的指定字符
     * 
     * @param string 原文本
     * @param chars 待删除的字符
     * @return 删除后的字符串
     */
    public static String removeRight(String string, char... chars) {
        return removeLeftRight(string, false, true, chars);
    }

    private static String removeLeftRight(String string, boolean left, boolean right, char... chars) {
        char[] value = string.toCharArray();
        int len = value.length;
        int st = 0;
        char[] val = value;

        if (left) {
            while ((st < len) && inArray(val[st], chars)) {
                st++;
            }
        }
        if (right) {
            while ((st < len) && inArray(val[len - 1], chars)) {
                len--;
            }
        }
        return ((st > 0) || (len < value.length)) ? string.substring(st, len) : string;
    }

    /**
     * 删除左右两侧指定数量的字符
     * 
     * @param string 原文本
     * @param start 左侧删除的数量
     * @param end 右侧删除的数量
     * @return 删除后的字符串
     * @deprecated 方法名含义不明确, 改为removeLeftRight(String string, int start, int end)
     */
    @Deprecated
    public static String remove(String string, int start, int end) {
        return removeLeftRight(string, start, end);
    }

    /**
     * 删除左右两侧指定数量的字符
     * 
     * @param string 原文本
     * @param start 左侧删除的数量
     * @param end 右侧删除的数量
     * @return 删除后的字符串
     */
    public static String removeLeftRight(String string, int start, int end) {
        if (string == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        if (start > 0) {
            buffer.append(string.substring(0, start));
        }
        if (end < string.length()) {
            buffer.append(string.substring(string.length() - end));
        }
        return buffer.toString();
    }

    /** 删除前缀(删除到指定字符为止), removePrefixAt("userName$Equals", '$') = "Equals" **/
    public static String removePrefixAt(String string, char c) {
        if (string == null) {
            return null;
        }
        int index = string.lastIndexOf(c);
        return index < 0 ? string : string.substring(index + 1);
    }

    /** 删除后缀(从指定字符开始删除), removeSuffixAt("userName$Equals", '$') = "userName" **/
    public static String removeSuffixAt(String string, char c) {
        if (string == null) {
            return null;
        }
        int index = string.indexOf(c);
        return index < 0 ? string : string.substring(0, index);
    }

    /** 删除前缀, removePrefix("userNameEquals", "userName") = "Equals" **/
    public static String removePrefix(String string, String prefix) {
        if (string == null || prefix == null || !string.startsWith(prefix)) {
            return string;
        }
        if (string.length() < prefix.length()) {
            return "";
        }
        return string.substring(prefix.length());
    }

    /** 删除后缀, removeSuffix("userNameEquals", "Equals") = "userName" **/
    public static String removeSuffix(String string, String suffix) {
        if (string == null || suffix == null || !string.endsWith(suffix)) {
            return string;
        }
        int end = string.length() - suffix.length();
        if (end <= 0) {
            return "";
        }
        return string.substring(0, end);
    }

    /**
     * 删除指定的子字符串<br>
     * 如: removeSubStrings("11223344", "22", "33") 输出 1144<br>
     * 
     * @param string 源字符串
     * @param sub 待删除的字符串
     * @return 删除后的字符串
     */
    public static String removeSubStrings(String string, String... sub) {
        for (String s : sub) {
            string = removeSubString(string, s);
        }
        return string;
    }

    /**
     * 删除指定的子字符串<br>
     * 如: removeSubString("11223344", "22") 输出 113344<br>
     * 
     * @param string 源字符串
     * @param sub 待删除的字符串
     * @return 删除后的字符串
     */
    public static String removeSubString(String string, String sub) {
        StringBuilder buffer = new StringBuilder();
        int index = 0;
        int startIndex;
        while ((startIndex = string.indexOf(sub, index)) >= index) {
            if (index < startIndex) {
                buffer.append(string.substring(index, startIndex));
            }
            index = startIndex + sub.length();
        }
        if (index < string.length()) {
            buffer.append(string.substring(index));
        }
        return buffer.toString();
    }

    /**
     * 删除指定的字符<br>
     * 如: removeChars("11223344", '2','3') 输出 1144<br>
     * 
     * @param string 源字符串
     * @param chars 待删除的字符
     * @return 删除后的字符串
     */
    public static String removeChars(String string, char... chars) {
        StringBuilder buffer = new StringBuilder();
        char[] sources = string.toCharArray();
        for (int i = 0; i < sources.length; i++) {
            int index = -1;
            for (int j = 0; j < chars.length; j++) {
                if (sources[i] == chars[j]) {
                    index = j;
                    break;
                }
            }
            if (index < 0) {
                buffer.append(sources[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * 删除成对的符号及包含在中间的内容<br>
     * 如: removeInPairedSymbol("111<!--xxx-->222<!--xxx-->333", "<!--", "-->") 输出 111222333<br>
     * 如: removeInPairedSymbol("111/&#42;xxx&#42;/222/&#42;xxx&#42;/333", "/&#42;", "&#42;/") 输出 111222333<br>
     * 
     * @param string 源字符串
     * @param leftSymbol 左侧的符号
     * @param rightSymbol 右侧的符号
     * @return 删除后的字符串
     */
    public static String removeInPairedSymbol(String string, String leftSymbol, String rightSymbol) {
        StringBuilder buffer = new StringBuilder();
        int index = 0;
        int leftIndex;
        while ((leftIndex = string.indexOf(leftSymbol, index)) >= index) {
            if (index < leftIndex) {
                buffer.append(string.substring(index, leftIndex));
            }
            int rightIndex = string.indexOf(rightSymbol, leftIndex);
            if (rightIndex < 0) {
                return buffer.toString();
            }
            index = rightIndex + rightSymbol.length();
        }
        if (index < string.length()) {
            buffer.append(string.substring(index));
        }
        return buffer.toString();
    }

    /**
     * 获取成对符号之间的内容<br>
     * 如: getSubstringInPairedSymbol("111<!--xxx-->222<!--xxx-->333", "<!--", "-->") 输出 222<br>
     * 如: getSubstringInPairedSymbol("111/&#42;xxx&#42;/222/&#42;xxx&#42;/333", "/&#42;", "&#42;/") 输出 222<br>
     * 
     * @param string 源字符串
     * @param leftSymbol 左侧的符号
     * @param rightSymbol 右侧的符号
     * @return 子字符串, 未找到符号时返回null
     */
    public static String getSubstringInPairedSymbol(String string, String leftSymbol, String rightSymbol) {
        int startIndex = string.indexOf(leftSymbol);
        if (startIndex < 0) {
            return null;
        }
        startIndex += leftSymbol.length();
        int endIndex = string.indexOf(rightSymbol, startIndex);
        if (endIndex < 0) {
            return null;
        }
        return string.substring(startIndex, endIndex);
    }

    /**
     * 字符串替换(非正则)<br>
     * 例如: \t替换为空格, \r\n替换为\n<br>
     * StringTools.replace("abc\tdef\r\nxyz", "\t", " ", "\r\n", "\n");
     * 
     * @param string 源字符串
     * @param patterns 替换规则
     * @return 替换后的字符串
	 * @since 5.0.0
     */
    public static String replace(String string, String... patterns) {
        if (string == null || patterns == null || patterns.length == 0) {
            return string;
        }

        List<KeyString> list = parseReplaceKeyValue(patterns);
        if (list == null || list.isEmpty()) {
            return string;
        }

        StringBuilder buffer = new StringBuilder(string);
        doReplace(buffer, list);
        return buffer.toString();
    }

    /**
     * 字符串替换(非正则)<br>
     * 例如: \t替换为空格, \r\n替换为\n<br>
     * StringTools.replace("abc\tdef\r\nxyz", "\t", " ", "\r\n", "\n");
     * 
     * @param string 源字符串
     * @param patterns 替换规则
	 * @since 5.0.0
     */
    public static void replace(StringBuilder string, String... patterns) {
        if (string == null || patterns == null || patterns.length == 0) {
            return;
        }

        List<KeyString> list = parseReplaceKeyValue(patterns);
        if (list == null || list.isEmpty()) {
            return;
        }
        doReplace(string, list);
    }

    /**
     * 字符串替换(非正则)<br>
     * 例如: \t替换为空格, \r\n替换为\n<br>
     * StringTools.replace("abc\tdef\r\nxyz", "\t", " ", "\r\n", "\n");
     * 
     * @param string 源字符串
     * @param patterns 替换规则
	 * @since 5.0.0
     */
    public static void replace(StringBuffer string, String... patterns) {
        if (string == null || patterns == null || patterns.length == 0) {
            return;
        }
        List<KeyString> list = parseReplaceKeyValue(patterns);
        if (list == null || list.isEmpty()) {
            return;
        }
        doReplace(string, list);
    }

    /**
     * 字符串替换(非正则)
     * 
     * @param string 源字符串
     * @param patterns 替换规则
	 * @since 5.0.0
     */
    public static String replace(String string, List<KeyString> patterns) {
        if (string == null || string.length() == 0) {
            return string;
        }
        if (patterns == null || patterns.isEmpty()) {
            return string;
        }

        StringBuilder buffer = new StringBuilder(string);
        doReplace(buffer, patterns);
        return buffer.toString();
    }

    /**
     * 字符串替换(非正则)
     * 
     * @param string 源字符串
     * @param patterns 替换规则
	 * @since 5.0.0
     */
    public static void replace(StringBuilder string, List<KeyString> patterns) {
        if (string == null || string.length() == 0) {
            return;
        }
        if (patterns == null || patterns.isEmpty()) {
            return;
        }

        doReplace(string, patterns);
    }

    private static void doReplace(StringBuilder string, List<KeyString> patterns) {
        for (KeyString kv : patterns) {
            replace(string, kv.getKey(), kv.getValue());
        }
    }

    /**
     * 字符串替换(非正则)
     * 
     * @param string 源字符串
     * @param patterns 替换规则
	 * @since 5.0.0
     */
    public static void replace(StringBuffer string, List<KeyString> patterns) {
        if (string == null || string.length() == 0) {
            return;
        }
        if (patterns == null || patterns.isEmpty()) {
            return;
        }

        doReplace(string, patterns);
    }

    private static void doReplace(StringBuffer string, List<KeyString> patterns) {
        for (KeyString kv : patterns) {
            replace(string, kv.getKey(), kv.getValue());
        }
    }

    /**
     * 字符串替换(非正则)
     * 
     * @param string 源字符串
     * @param pattern 替换规则
     * @param replacement 替换目标
	 * @since 5.0.0
     */
    public static void replace(StringBuilder string, String pattern, String replacement) {
        if (string == null || string.length() == 0) {
            return;
        }
        if (pattern == null || pattern.isEmpty()) {
            return;
        }

        doReplace(string, pattern, replacement);
    }

    private static void doReplace(StringBuilder string, String pattern, String replacement) {
        int index = 0;
        while (true) {
            int nextIndex = string.indexOf(pattern, index);
            if (nextIndex < 0) {
                break;
            }
            if (replacement == null || replacement.isEmpty()) {
                string.delete(nextIndex, nextIndex + pattern.length());
                index = nextIndex;
            } else {
                string.replace(nextIndex, nextIndex + pattern.length(), replacement);
                index = nextIndex + replacement.length();
            }
        }
    }

    /**
     * 字符串替换(非正则)
     * 
     * @param string 源字符串
     * @param pattern 替换规则
     * @param replacement 替换目标
	 * @since 5.0.0
     */
    public static void replace(StringBuffer string, String pattern, String replacement) {
        if (string == null || string.length() == 0) {
            return;
        }
        if (pattern == null || pattern.isEmpty()) {
            return;
        }

        doReplace(string, pattern, replacement);
    }

    private static void doReplace(StringBuffer string, String pattern, String replacement) {
        if (pattern == null || pattern.isEmpty()) {
            return;
        }
        int index = 0;
        while (true) {
            int nextIndex = string.indexOf(pattern, index);
            if (nextIndex < 0) {
                break;
            }
            if (replacement == null || replacement.isEmpty()) {
                string.delete(nextIndex, pattern.length());
                index = nextIndex;
            } else {
                string.replace(nextIndex, nextIndex + pattern.length(), replacement);
                index = nextIndex + replacement.length();
            }
        }
    }

    private static List<KeyString> parseReplaceKeyValue(String... patterns) {
        if (patterns.length % 2 != 0) {
            throw new IllegalArgumentException("参数必须是键值对, 参数个数必须是2的倍数");
        }

        List<KeyString> list = new ArrayList<>();
        for (int i = 0; i < patterns.length;) {
            list.add(new KeyString(patterns[i++], patterns[i++]));
        }

        return list;
    }

    private static boolean inArray(char c, char[] array) {
        for (int i = 0, len = array.length; i < len; i++) {
            if (array[i] == c) {
                return true;
            }
        }
        return false;
    }

    /** 左侧补零 **/
    public static String pad(int number, int length) {
        return pad(String.valueOf(number), '0', true, length);
    }

    /** 左侧补零 **/
    public static String pad(long number, int length) {
        return pad(String.valueOf(number), '0', true, length);
    }

    /** 左侧补空格 **/
    public static String pad(String string, int length) {
        return pad(string, ' ', true, length);
    }

    /**
     * 左侧补字符<br>
     * pad("12345", '_', 10) 返回 _____12345<br>
     * 
     * @param string 原字符串
     * @param c 补充的字符
     * @param length 目标长度
     * @return 目标字符串
     */
    public static String pad(String string, char c, int length) {
        return pad(string, c, true, length);
    }

    /**
     * 左侧或右侧补字符<br>
     * pad("12345", '_', false, 10) 返回 12345_____<br>
     * 
     * @param string 原字符串
     * @param c 补充的字符
     * @param left 左侧补(true)还是右侧补(false)
     * @param length 目标长度
     * @return 目标字符串
     */
    public static String pad(String string, char c, boolean left, int length) {
        if (string == null || string.length() >= length) {
            return string;
        }

        char[] array = new char[length - string.length()];
        Arrays.fill(array, c);
        return left ? new String(array) + string : string + new String(array);
    }

    /**
     * 统计子字符串出现次数
     * 
     * @param string 源字符串
     * @param substring 子字符串
     * @return 次数
     */
    public static int countSubstring(String string, String substring) {
        int count = 0;
        for (int i = 0; (i = string.indexOf(substring, i)) >= 0; i += substring.length()) {
            count++;
        }
        return count;
    }

    /** 是不是肯定的 **/
    public static boolean isPositive(String value, boolean defValue) {
        if (value == null || value.length() == 0) {
            return defValue;
        } else {
            if (isExists(true, value, "Y", "yes", "true", "on", "1")) {
                return true;
            } else if (isExists(true, value, "N", "no", "false", "off", "0")) {
                return false;
            } else { // 配置了不能识别为boolean的字符串, 返回defValue
                return defValue;
            }
        }
    }

    /** 是不是否定的 **/
    public static boolean isNegative(String value, boolean defValue) {
        if (value == null || value.length() == 0) {
            return defValue;
        } else {
            if (isExists(true, value, "Y", "yes", "true", "on", "1")) {
                return false;
            } else if (isExists(true, value, "N", "no", "false", "off", "0")) {
                return true;
            } else { // 配置了不能识别为boolean的字符串, 返回defValue
                return defValue;
            }
        }
    }

    /** 判断字符串是否存在数组中 **/
    public static boolean isExists(String string, String... strings) {
        return isExists(false, string, strings);
    }

    /** 判断字符串是否存在数组中 **/
    public static boolean isNotExists(String string, String... strings) {
        return !isExists(false, string, strings);
    }

    /** 判断字符串是否存在数组中 **/
    public static boolean isExists(boolean ignoreCase, String string, String... strings) {
        if (strings == null || strings.length == 0) {
            return false;
        }
        for (String i : strings) {
            if (i == null && string == null) {
                return true;
            } else if (ignoreCase) {
                if (i != null && i.equalsIgnoreCase(string)) {
                    return true;
                } else if (string != null && string.equalsIgnoreCase(i)) {
                    return true;
                }
            } else {
                if (i != null && i.equals(string)) {
                    return true;
                } else if (string != null && string.equals(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 判断字符串是否存在数组中 **/
    public static boolean isNotExists(boolean ignoreCase, String string, String... strings) {
        return !isExists(ignoreCase, string, strings);
    }

    /**
     * 隐藏手机号码邮箱或名字
     *
     * @param string 手机号码邮箱或名字
     * @return 隐藏后的字符串, 如: 139****1382, zh****ua@126.com, <br>
     *         黄山-〇山, 昆仑山-〇〇山, 黄山毛峰-〇〇毛峰
     */
    public static String hidden(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }

        String ahide = "****";
        String uhide = "\u3007"; // 〇

        Matcher phone = PHONE.matcher(string);
        if (phone.matches()) {
            return phone.group(1) + ahide + phone.group(3);
        }

        Matcher email = EMAIL.matcher(string);
        if (email.matches()) {
            String prefix = email.group(1);
            String at = email.group(2);
            String suffix = email.group(3);
            return hiddenAscii(prefix, ahide) + at + suffix;
        }

        Matcher ascii = ASCII.matcher(string);
        if (ascii.matches()) {
            return hiddenAscii(string, ahide);
        } else {
            if (string.length() == 1) {
                return uhide + string;
            }
            if (string.length() == 2) {
                return uhide + string.substring(string.length() - 1);
            } else if (string.length() == 3) {
                return uhide + uhide + string.substring(string.length() - 1);
            } else if (string.length() == 4) {
                return uhide + uhide + string.substring(string.length() - 2);
            } else if (string.length() == 5) {
                return uhide + uhide + string.substring(string.length() - 3);
            } else {
                return uhide + uhide + string.substring(string.length() - 4);
            }
        }
    }

    private static String hiddenAscii(String string, String hide) {
        if (string.length() >= 4) {
            return string.substring(0, 2) + hide + string.substring(string.length() - 2);
        } else if (string.length() >= 2) {
            return string.substring(0, 1) + hide + string.substring(1);
        } else {
            return string + hide + string;
        }
    }

    /**
     * 如果全是大写字母, 则转换为小写字母
     * 
     * @param name 待转换的名称
     * @return 转换后的名称
     * @deprecated 改为{@linkplain NamingTools#toLowerCaseIfAllUpperCase(String)}
     */
    @Deprecated
    public static String toLowerCaseIfAllUpperCase(String name) {
        return NamingTools.toLowerCaseIfAllUpperCase(name);
    }

    /**
     * 转换为驼峰命名法格式<br>
     * 如: user_name = userName, iuser_service = iuserService, i_user_service = iUserService
     *
     * @author zhaohuihua
     * @param name 待转换的名称
     * @return 驼峰命名法名称
     * @deprecated 改为{@linkplain NamingTools#toCamelString(String)}
     */
    @Deprecated
    public static String toCamelNaming(String name) {
        return NamingTools.toCamelString(name);
    }

    /**
     * 转换为驼峰命名法格式<br>
     * 如startsWithUpperCase=true时:<br>
     * user_name = UserName, iuser_service = IuserService, i_user_service = IUserService
     *
     * @author zhaohuihua
     * @param name 待转换的名称
     * @param startsWithUpperCase 是否以大写字母开头
     * @return 驼峰命名法名称
     * @deprecated 改为{@linkplain NamingTools#toCamelString(String, boolean)}
     */
    @Deprecated
    public static String toCamelNaming(String name, boolean startsWithUpperCase) {
        return NamingTools.toCamelString(name, startsWithUpperCase);
    }

    /**
     * 转换为下划线命名法格式<br>
     * 如: userName = user_name, SiteURL = site_url, IUserService = iuser_service<br>
     * user$Name = user$name, user_Name = user_name, user name = user_name, md5String = md5_string
     *
     * @author zhaohuihua
     * @param name 待转换的名称
     * @return 下划线命名法名称
     * @deprecated 改为{@linkplain NamingTools#toUnderlineNaming(String)}
     */
    @Deprecated
    public static String toUnderlineNaming(String name) {
        return NamingTools.toUnderlineString(name);
    }

    /**
     * <p>
     * Capitalizes a String changing the first letter to title case as per {@link Character#toTitleCase(char)}. No other
     * letters are changed.
     * </p>
     *
     * <p>
     * For a word based algorithm, see {@link org.apache.commons.lang3.text.WordUtils#capitalize(String)}. A
     * {@code null} input String returns {@code null}.
     * </p>
     *
     * <pre>
     * StringTools.capitalize(null)  = null
     * StringTools.capitalize("")    = ""
     * StringTools.capitalize("cat") = "Cat"
     * StringTools.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param string the String to capitalize, may be null
     * @return the capitalized String, {@code null} if null String input
     * @see org.apache.commons.lang3.text.WordUtils#capitalize(String)
     * @since 2.0
     * @deprecated 改为{@linkplain NamingTools#capitalize(String)}
     */
    @Deprecated
    public static String capitalize(final String string) {
        return NamingTools.capitalize(string);
    }
}
