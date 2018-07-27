package com.gitee.qdbp.able.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具
 *
 * @author zhaohuihua
 * @version 150913
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
    private static final Pattern REG_CHAR = Pattern.compile("([\\{\\}\\[\\]\\(\\)\\^\\$\\.\\*\\?\\-\\+\\\\])");

    // \\uD800\\uDC00-\\uDBFF\\uDFFF\\uD800-\\uDFFF
    private static final Pattern UTF8MB4 = Pattern.compile("[\\uD800\\uDC00-\\uDBFF\\uDFFF]");

    private static final Pattern REGEXP_TRIM = Pattern.compile("^\\s+|\\s+$");
    private static final Pattern REGEXP_TRIM_LEFT = Pattern.compile("^\\s+");
    private static final Pattern REGEXP_TRIM_RIGHT = Pattern.compile("\\s+$");

    /** 是否存在表情字符 **/
    public static boolean existEmoji(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return UTF8MB4.matcher(str).find();
    }

    /** 清除表情字符 **/
    public static String clearEmoji(String str) {
        return UTF8MB4.matcher(str).replaceAll("?");
    }

    /**
     * 判断字符串是不是手机号码
     *
     * @author zhaohuihua
     * @param str 字符串
     * @return 是不是手机号码, 如果字符串等于null或空字符串, 返回false
     */
    public static boolean isPhone(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return PHONE.matcher(str).matches();
    }

    /**
     * 判断字符串是不是邮箱地址
     *
     * @author zhaohuihua
     * @param str 字符串
     * @return 是不是邮箱地址, 如果字符串等于null或空字符串, 返回false
     */
    public static boolean isEmail(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return EMAIL.matcher(str).matches();
    }

    /**
     * 判断字符串是不是网址
     *
     * @author zhaohuihua
     * @param str 字符串
     * @return 是不是网址, 如果字符串等于null或空字符串, 返回false
     */
    public static boolean isUrl(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return URL.matcher(str).matches();
    }

    /**
     * 判断字符串是不是数字
     *
     * @author zhaohuihua
     * @param str 字符串
     * @return 是不是数字, 如果字符串等于null或空字符串, 返回false
     */
    public static boolean isDigit(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return DIGIT.matcher(str).matches();
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

    public static String remove(String string, int start, int end) {
        if (string == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        if (start > 0) {
            buffer.append(string.substring(0, start));
        }
        if (end < string.length()) {
            buffer.append(string.substring(end));
        }
        return buffer.toString();
    }

    /**
     * 超过指定长度则省略中间字符<br>
     * 如果未超过指定长度则返回原字符, length小于20时省略最后部分而不是中间部分<br>
     * 如: 诺贝尔奖是以瑞典著名的 ... 基金创立的
     *
     * @author zhaohuihua
     * @param text 长文本
     * @param length 指定长度
     * @return 省略中间部分的字符
     */
    public static String ellipsis(String text, int length) {
        if (text == null || text.length() <= length || length == 0) {
            return text;
        }
        String flag = " ... ";
        if (length < 20) {
            return text.substring(0, length) + flag;
        }
        int suffix = (length - flag.length()) / 4;
        int prefix = length - flag.length() - suffix;
        int end = text.length() - suffix;
        return text.substring(0, prefix) + flag + text.substring(end);
    }

    private static Pattern LOWER_CASE = Pattern.compile("[a-z]");

    /** 如果全是大写字母, 则转换为小写字母 **/
    public static String toLowerCaseIfAllUpperCase(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        if (LOWER_CASE.matcher(string).find()) {
            return string; // 有小写字母, 不处理, 直接返回
        } else {
            return string.toLowerCase(); // 整个字符串都没有小写字母则转换为小写字母
        }
    }

    /**
     * 转换为驼峰命名法格式<br>
     * 如: user_name = userName, iuser_service = iuserService, i_user_service = iUserService
     *
     * @author zhaohuihua
     * @param name
     * @return
     */
    public static String toCamelNaming(String name) {
        return toCamelNaming(name, false);
    }

    /**
     * 转换为驼峰命名法格式<br>
     * 如startsWithUpperCase=true时:<br>
     * user_name = UserName, iuser_service = IuserService, i_user_service = IUserService
     *
     * @author zhaohuihua
     * @param name
     * @param startsWithUpperCase 是否以大写字母开头
     * @return
     */
    public static String toCamelNaming(String name, boolean startsWithUpperCase) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char[] chars = toLowerCaseIfAllUpperCase(name.trim()).toCharArray();

        StringBuilder buffer = new StringBuilder();
        boolean underline = startsWithUpperCase;
        for (char c : chars) {
            if (c == '_') {
                underline = true;
            } else {
                if (underline) {
                    buffer.append(Character.toUpperCase(c));
                } else {
                    buffer.append(c);
                }
                underline = false;
            }
        }
        return buffer.toString();
    }

    /**
     * 转换为下划线命名法格式<br>
     * 如: userName = user_name, SiteURL = site_url, IUserService = iuser_service<br>
     * user$Name = user$name, user_Name = user_name, user name = user_name
     *
     * @author zhaohuihua
     * @param name
     * @return
     */
    public static String toUnderlineNaming(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char[] chars = name.trim().toCharArray();

        boolean lastLowerCase = false;
        StringBuilder buffer = new StringBuilder();
        for (char c : chars) {
            if (Character.isWhitespace(c)) {
                if (lastLowerCase) {
                    buffer.append("_");
                }
                lastLowerCase = false;
            } else if (Character.isUpperCase(c)) {
                if (lastLowerCase) {
                    buffer.append("_");
                }
                buffer.append(Character.toLowerCase(c));
                lastLowerCase = false;
            } else {
                buffer.append(c);
                lastLowerCase = Character.isLowerCase(c);
            }
        }
        return buffer.toString();
    }

    public static String concat(char c, String... paths) {
        return concat(c, null, paths, 0, paths.length);
    }

    public static String concat(char c, String[] paths, int start, int end) {
        return concat(c, null, paths, start, end);
    }

    public static String concat(char c, String prefix, String[] paths, int start, int end) {

        StringBuilder buffer = new StringBuilder();
        if (VerifyTools.isNotBlank(prefix)) {
            buffer.append(prefix);
        }
        for (int i = Math.max(start, 0), len = Math.min(end, paths.length); i < len; i++) {
            String path = paths[i];
            if (VerifyTools.isBlank(path)) {
                continue;
            }
            if (buffer.length() == 0) {
                buffer.append(path);
            } else if (!endsWithChar(buffer, c) && !startsWithChar(path, c)) {
                buffer.append(c).append(path);
            } else if (endsWithChar(buffer, c) && endsWithChar(path, c)) {
                buffer.append(path.substring(1));
            } else {
                buffer.append(path);
            }
        }
        return buffer.toString();
    }

    public static boolean endsWithChar(CharSequence string, char c) {
        return string.length() > 0 && c == string.charAt(string.length() - 1);
    }

    public static boolean startsWithChar(CharSequence string, char c) {
        return string.length() > 0 && c == string.charAt(0);
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
     * @param str the String to capitalize, may be null
     * @return the capitalized String, {@code null} if null String input
     * @see org.apache.commons.lang3.text.WordUtils#capitalize(String)
     * @since 2.0
     */
    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return new StringBuilder(strLen).append(Character.toTitleCase(firstChar)).append(str.substring(1)).toString();
    }

    public static String trim(String text) {
        return text == null ? null : REGEXP_TRIM.matcher(text).replaceAll("");
    }

    public static String trimLeft(String text) {
        return text == null ? null : REGEXP_TRIM_LEFT.matcher(text).replaceAll("");
    }

    public static String trimRight(String text) {
        return text == null ? null : REGEXP_TRIM_RIGHT.matcher(text).replaceAll("");
    }

    public static String remove(String text, char... chars) {
        return remove(text, true, true, chars);
    }

    public static String removeLeft(String text, char... chars) {
        return remove(text, true, false, chars);
    }

    public static String removeRight(String text, char... chars) {
        return remove(text, false, true, chars);
    }

    private static String remove(String text, boolean left, boolean right, char... chars) {
        char[] value = text.toCharArray();
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
        return ((st > 0) || (len < value.length)) ? text.substring(st, len) : text;
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
     * @param string
     * @param c
     * @param length
     * @return
     */
    public static String pad(String string, char c, int length) {
        return pad(string, c, true, length);
    }

    /**
     * 左侧或右侧补字符<br>
     * pad("12345", '_', false, 10) 返回 12345_____<br>
     * 
     * @param string
     * @param c
     * @param left 左侧补(true)还是右侧补(false)
     * @param length
     * @return
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
        if (VerifyTools.isNotBlank(value) && isExists(true, value, "on", "yes", "true", "1")) {
            return true;
        } else {
            return defValue;
        }
    }

    /** 是不是否定的 **/
    public static boolean isNegative(String value, boolean defValue) {
        if (VerifyTools.isNotBlank(value) && isExists(true, value, "off", "no", "false", "0")) {
            return true;
        } else {
            return defValue;
        }
    }

    public static boolean isExists(String string, String... strings) {
        return isExists(false, string, strings);
    }

    public static boolean isNotExists(String string, String... strings) {
        return !isExists(false, string, strings);
    }

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

    public static boolean isNotExists(boolean ignoreCase, String string, String... strings) {
        return !isExists(ignoreCase, string, strings);
    }

    /**
     * 隐藏手机号码邮箱或名字
     *
     * @param text 手机号码邮箱或名字
     * @return 隐藏后的字符串, 如: 139****1382, zh****ua@126.com, <br>
     *         黄山-〇山, 昆仑山-〇〇山, 黄山毛峰-〇〇毛峰
     */
    public static String hidden(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }

        String ahide = "****";
        String uhide = "\u3007"; // 〇

        Matcher phone = PHONE.matcher(text);
        if (phone.matches()) {
            return phone.group(1) + ahide + phone.group(3);
        }

        Matcher email = EMAIL.matcher(text);
        if (email.matches()) {
            String prefix = email.group(1);
            String at = email.group(2);
            String suffix = email.group(3);
            return hiddenAscii(prefix, ahide) + at + suffix;
        }

        Matcher ascii = ASCII.matcher(text);
        if (ascii.matches()) {
            return hiddenAscii(text, ahide);
        } else {
            if (text.length() == 1) {
                return uhide + text;
            }
            if (text.length() == 2) {
                return uhide + text.substring(text.length() - 1);
            } else if (text.length() == 3) {
                return uhide + uhide + text.substring(text.length() - 1);
            } else if (text.length() == 4) {
                return uhide + uhide + text.substring(text.length() - 2);
            } else if (text.length() == 5) {
                return uhide + uhide + text.substring(text.length() - 3);
            } else {
                return uhide + uhide + text.substring(text.length() - 4);
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
}
