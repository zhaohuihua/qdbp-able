package com.gitee.qdbp.tools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期工具
 *
 * @author zhaohuihua
 * @version 150913
 */
public abstract class DateTools {

    /** 一秒有多少毫秒 **/
    public static final long RATE_SECOND = 1000;
    /** 一分钟有多少毫秒 **/
    public static final long RATE_MINUTE = 60 * RATE_SECOND;
    /** 一小时有多少毫秒 **/
    public static final long RATE_HOUR = 60 * RATE_MINUTE;
    /** 一天有多少毫秒 **/
    public static final long RATE_DAY = 24 * RATE_HOUR;

    /** 标准格式 **/
    public static final String PATTERN_GENERAL_NORMATIVE = "yyyy-MM-dd HH:mm:ss.SSS";
    /** 紧凑格式(纯数字没有分隔符) **/
    public static final String PATTERN_COMPACT_NORMATIVE = "yyyyMMddHHmmssSSS";
    /** 只有日期部分 **/
    public static final String PATTERN_GENERAL_DATE = "yyyy-MM-dd";
    /** 只有日期部分(纯数字没有分隔符) **/
    public static final String PATTERN_COMPACT_DATE = "yyyyMMdd";
    /** 只有时间部分 **/
    public static final String PATTERN_GENERAL_TIME = "HH:mm:ss";
    /** 只有时间部分(纯数字没有分隔符) **/
    public static final String PATTERN_COMPACT_TIME = "HHmmss";
    /** 日期+时间 **/
    public static final String PATTERN_GENERAL_DATETIME = "yyyy-MM-dd HH:mm:ss";
    /** 日期+时间(纯数字没有分隔符) **/
    public static final String PATTERN_COMPACT_DATETIME = "yyyyMMddHHmmss";
    /** 标准格式+ISO时区(FastJson支持这种格式) **/
    // 2010-10-20T15:25:35.450+08:00
    // yyyy-MM-dd'T'HH:mm:ss.SSSXXX
    public static final String PATTERN_GENERAL_NORMATIVE_T_ISO_XXX = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    /** 标准格式+ISO时区 **/
    // 2010-10-20T15:25:35.450+0800
    // yyyy-MM-dd'T'HH:mm:ss.SSSXX
    public static final String PATTERN_GENERAL_NORMATIVE_T_ISO_XX = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";
    /** 标准格式+ISO时区 **/
    // 2010-10-20T15:25:35.450+08
    // yyyy-MM-dd'T'HH:mm:ss.SSSX
    public static final String PATTERN_GENERAL_NORMATIVE_T_ISO_X = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    /** 标准格式+GMT时区 **/
    // 2010-10-20 15:25:35.450 +0800
    // yyyy-MM-dd HH:mm:ss.SSS Z
    public static final String PATTERN_GENERAL_NORMATIVE_GMT_ZONE = "yyyy-MM-dd HH:mm:ss.SSS Z";
    /** 紧凑格式+GMT时区(纯数字没有分隔符)(FastJson支持这种格式) **/
    // 20101020152535450+08:00
    // yyyyMMddHHmmssSSSZ
    public static final String PATTERN_COMPACT_NORMATIVE_GMT_ZONE = "yyyyMMddHHmmssSSSZ";

    // 解析日期时支持的格式
    private static DateParsers PARSERS = new DateParsers(PATTERN_GENERAL_NORMATIVE, PATTERN_COMPACT_NORMATIVE,
            PATTERN_GENERAL_DATE, PATTERN_COMPACT_DATE, PATTERN_GENERAL_TIME, PATTERN_COMPACT_TIME,
            PATTERN_GENERAL_DATETIME, PATTERN_COMPACT_DATETIME,
            // ISO时区
            PATTERN_GENERAL_NORMATIVE_T_ISO_XXX, PATTERN_GENERAL_NORMATIVE_T_ISO_XX, PATTERN_GENERAL_NORMATIVE_T_ISO_X,
            // GMT时区
            PATTERN_GENERAL_NORMATIVE_GMT_ZONE, PATTERN_COMPACT_NORMATIVE_GMT_ZONE);

    /**
     * 日期解析类
     * 
     * @author zhaohuihua
     * @version 170330
     */
    private static class DateParser {

        /** 正则表达式转义字符 **/
        private static final Pattern REG_CHAR = Pattern.compile("([\\{\\}\\[\\]\\(\\)\\^\\$\\.\\*\\?\\-\\+\\\\])");
        /** 数字字符(替换为[0-9]) **/
        private static final Pattern NUMBER_CHAR = Pattern.compile("[yMdHmsS]");
        /** 常量字符(去掉两边的单引号) **/
        private static final Pattern CONST_CHAR = Pattern.compile("'([^']+)'");
        /** 单引号字符(两个单引号替换为一个) **/
        private static final Pattern SINGLE_QUOTE_CHAR = Pattern.compile("''");

        private int length;
        private Pattern regexp;
        private String pattern;

        public DateParser(String pattern) {
            this.length = pattern.length();
            this.pattern = pattern;
            String regexp = REG_CHAR.matcher(pattern).replaceAll("\\\\$1");
            regexp = NUMBER_CHAR.matcher(regexp).replaceAll("\\\\d");

            // 时区字符只支持末尾的字符, 因为不好识别'Z'之类的常量字符
            if (regexp.endsWith("X")) { // ISO时区字符
                this.length = 0;
                // X=+/-hh, XX=+/-hhmm, 3个X=+/-hh:mm
                if (regexp.endsWith("XXX")) {
                    regexp = StringTools.removeRight(regexp, 'X') + "[+-]\\d\\d:\\d\\d";
                } else if (regexp.endsWith("XX")) {
                    regexp = StringTools.removeRight(regexp, 'X') + "[+-]\\d{4}";
                } else if (regexp.endsWith("X")) {
                    regexp = StringTools.removeRight(regexp, 'X') + "[+-]\\d{2}";
                }
            } else if (regexp.endsWith("Z")) { // GMT时区字符
                this.length = 0;
                // GMT或GMT+/-hh:mm或+/-hhmm
                regexp = StringTools.removeRight(regexp, 'Z') + "(?:\\w+|\\w+[+-]\\d\\d:\\d\\d|[+-]\\d{4})";
            }
            { // 常量字符
                Matcher matcher = CONST_CHAR.matcher(regexp);
                if (matcher.find()) {
                    this.length = 0;
                    regexp = matcher.replaceAll("$1");
                }
            }
            { // 单引号字符
                Matcher matcher = SINGLE_QUOTE_CHAR.matcher(regexp);
                if (matcher.find()) {
                    this.length = 0;
                    regexp = matcher.replaceAll("'");
                }
            }
            this.regexp = Pattern.compile(regexp);
        }

        public boolean supported(String date) {
            return (length <= 0 || length == date.length()) && this.regexp.matcher(date).matches();
        }

        public Date parse(String date) {
            try {
                return new SimpleDateFormat(this.pattern).parse(date);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Date format is not supported [" + date + "].", e);
            }
        }
    }

    /**
     * 循环尝试解析日期
     * 
     * @author zhaohuihua
     * @version 170330
     */
    private static class DateParsers {

        private List<DateParser> parsers = new ArrayList<>();

        public DateParsers(String... patterns) {
            for (String s : patterns) {
                this.parsers.add(new DateParser(s));
            }
        }

        public Date parse(String date) {
            for (DateParser dp : parsers) {
                if (dp.supported(date)) {
                    return dp.parse(date);
                }
            }
            throw new IllegalArgumentException("Date format is not supported [" + date + "].");
        }
    }

    private static Map<Character, Void> CHINESE_DATE_UNITS = ConvertTools.toCharMaps("年月日");
    private static Map<Character, Void> CHINESE_TIME_UNITS = ConvertTools.toCharMaps("时分秒毫秒");
    private static Pattern CHINESE_PART = Pattern.compile("(\\d+)(年|月|日|时|分|秒|毫秒)");

    /**
     * 智能解析日期<br>
     * parse("2000-10-20 15:25:35.450");<br>
     * parse("2000-10-20 15:25:35");<br>
     * parse("2000-10-20");<br>
     * parse("2018/8/20 15:25:35");<br>
     * parse("8/20/2018 15:25:35");<br>
     * parse("20001020152535450");<br>
     * parse("20001020152535");<br>
     * parse("20001020");<br>
     * parse("15:25:35");<br>
     * parse("152535");<br>
     * 解析年月日格式说明<br>
     * 年月日(中式): 2018/8/8, 2018/8/20, 2018/12/12<br>
     * 月日年(美式): 8/8/2018, 8/20/2018, 12/12/2018<br>
     * 日月年(英式): 20/8/2018(20大于12的作为日); 7/8/2018(无法识别月日顺序按月日年处理)<br>
     * 注意: 大于31的识别为年份; 大于99的为公元年份; 小于等于99的, 30及以上为19XX年, 小于等于29的为20XX年<br>
     * 三个数字都不大于31时, 按年月日顺序处理: 6/7/8=2006/7/8; 29/7/8=2029/7/8; 30/7/8=1930/7/8<br>
     * 大于2位的识别为公元年份: 6/7/8=2006/7/8; 0006/7/8=公元6年7月8日<br>
     * 
     * @param string 日期字符串
     * @return 日期
     */
    public static Date parse(String string) {
        if (string == null || string.trim().length() == 0) {
            return null;
        }
        String datetime = string.trim();
        int spaceIndex = -1;
        int hbarIndex = -1;
        int slashIndex = -1;
        int colonIndex = -1;
        int dotIndex = -1;
        int otherCharIndex = -1;
        boolean existChineseDateUnits = false;
        boolean existChineseTimeUnits = false;
        char[] chars = datetime.toCharArray();
        for (char i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == ' ') {
                if (spaceIndex < 0) {
                    spaceIndex = i;
                }
            } else if (c == '-') {
                if (hbarIndex < 0) {
                    hbarIndex = i;
                }
            } else if (c == '/') {
                if (slashIndex < 0) {
                    slashIndex = i;
                }
            } else if (c == ':') {
                if (colonIndex < 0) {
                    colonIndex = i;
                }
            } else if (c == '.') {
                if (dotIndex < 0) {
                    dotIndex = i;
                }
            } else if (c < '0' || c > '9') {
                if (otherCharIndex < 0) {
                    otherCharIndex = i;
                }
            }
            if (CHINESE_DATE_UNITS.containsKey(c)) {
                existChineseDateUnits = true;
            } else if (CHINESE_TIME_UNITS.containsKey(c)) {
                existChineseTimeUnits = true;
            }
        }

        if (existChineseDateUnits || existChineseTimeUnits) {
            // 2018年7月8日10时20分30秒400毫秒
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Matcher matcher = CHINESE_PART.matcher(datetime);
            int index = 0;
            Map<String, Void> units = new HashMap<>();
            while (matcher.find()) {
                String number = matcher.group(1);
                String unit = matcher.group(2);
                if (index < matcher.start()) {
                    String pending = datetime.substring(index, matcher.start());
                    if (pending.trim().length() > 0) {
                        throw new IllegalArgumentException("Date format is not supported [" + datetime + "].");
                    }
                }
                if (units.containsKey(unit)) {
                    // 单位重复了, 如: 2018年7月8月
                    throw new IllegalArgumentException("Date format is not supported [" + datetime + "].");
                }
                units.put(unit, null);
                int n = Integer.parseInt(number);
                if ("年".equals(unit)) {
                    calendar.set(Calendar.YEAR, n);
                } else if ("月".equals(unit)) {
                    calendar.set(Calendar.MONTH, n - 1);
                } else if ("日".equals(unit)) {
                    calendar.set(Calendar.DAY_OF_MONTH, n);
                } else if ("时".equals(unit)) {
                    calendar.set(Calendar.HOUR_OF_DAY, n);
                } else if ("分".equals(unit)) {
                    calendar.set(Calendar.MINUTE, n);
                } else if ("秒".equals(unit)) {
                    calendar.set(Calendar.SECOND, n);
                } else if ("毫秒".equals(unit)) {
                    calendar.set(Calendar.MILLISECOND, n);
                } else {
                    throw new IllegalArgumentException("Date format is not supported [" + datetime + "].");
                }
                index = matcher.end();
            }
            if (index >= datetime.length()) {
                return calendar.getTime();
            }
            String pending = datetime.substring(index);
            if (!units.containsKey("毫秒") && StringTools.isDigit(pending)) {
                calendar.set(Calendar.MILLISECOND, Math.min(Integer.parseInt(pending), 999));
                return calendar.getTime();
            } else if (!existChineseTimeUnits) {
                // 2018年7月8日 10:20:30.400
                return parseTime(calendar.getTime(), pending);
            }
        }

        if (otherCharIndex >= 0) {
            return PARSERS.parse(datetime);
        } else if (spaceIndex > 0) {
            String[] array = StringTools.split(datetime, ' ');
            if (array.length != 2) {
                return PARSERS.parse(datetime);
            }
            if (colonIndex >= 0 && colonIndex < spaceIndex || dotIndex >= 0 && dotIndex < spaceIndex) {
                // 日期部分有冒号或点
                return PARSERS.parse(datetime);
            } else if (hbarIndex >= 0 && hbarIndex > spaceIndex || slashIndex >= 0 && slashIndex > spaceIndex) {
                // 时间部分有横框或斜杠
                return PARSERS.parse(datetime);
            }
            Date date;
            if ((slashIndex < 0 || slashIndex > spaceIndex) && (hbarIndex > 0 && hbarIndex < spaceIndex)) {
                // 横杠分隔的日期: 没有斜杠,有横杠且横杠的位置在日期部分
                date = parseYyyyMMdd(array[0], '-');
            } else if ((hbarIndex < 0 || hbarIndex > spaceIndex) && (slashIndex > 0 && slashIndex < spaceIndex)) {
                // 斜杠分隔的日期: 没有横杠,有斜杠且斜杠的位置在日期部分
                date = parseYyyyMMdd(array[0], '/');
            } else {
                return PARSERS.parse(datetime);
            }
            if (colonIndex > spaceIndex) {
                // 时间部分有冒号
                return parseTime(date, array[1]);
            } else {
                return PARSERS.parse(datetime);
            }
        } else if (hbarIndex < 0 && slashIndex < 0 && colonIndex > 0) {
            // 时间: 没有横杠和斜杠,有冒号
            return parseTime(toStartTime(new Date()), datetime);
        } else if (colonIndex < 0 && dotIndex < 0 && slashIndex < 0 && hbarIndex > 0) {
            // 横杠分隔的日期: 没有冒号和点,没有斜杠,有横杠
            return parseYyyyMMdd(datetime, '-');
        } else if (colonIndex < 0 && dotIndex < 0 && hbarIndex < 0 && slashIndex > 0) {
            // 横杠分隔的日期: 没有冒号和点,没有横杠,有斜杠
            return parseYyyyMMdd(datetime, '/');
        } else {
            return PARSERS.parse(datetime);
        }
    }

    /**
     * 解析年月日<br>
     * 年月日(中式): 2018/8/8, 2018/8/20, 2018/12/12<br>
     * 月日年(美式): 8/8/2018, 8/20/2018, 12/12/2018<br>
     * 日月年(英式): 20/8/2018(20大于12的作为日); 7/8/2018(无法识别月日顺序按月日年处理)<br>
     * 注意: 大于31的识别为年份; 大于99的为公元年份; 小于等于99的, 30及以上为19XX年, 小于等于29的为20XX年<br>
     * 三个数字都不大于31时, 按年月日顺序处理: 6/7/8=2006/7/8; 29/7/8=2029/7/8; 30/7/8=1930/7/8<br>
     * 大于2位的识别为公元年份: 6/7/8=2006/7/8; 0006/7/8=公元6年7月8日<br>
     * 
     * @param date 日期字符串
     * @param separator 分隔符
     * @return 解析后的日期
     */
    private static Date parseYyyyMMdd(String date, char separator) {
        String[] array = StringTools.split(date, separator);
        if (array.length != 3) { // 不是三个数字
            throw new IllegalArgumentException("Date format is not supported [" + date + "].");
        }
        int longCount = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].length() > 2) {
                longCount++;
            }
        }
        if (longCount > 1) { // 不只一个三位以上数字, 例如: 2018/8/100
            throw new IllegalArgumentException("Date format is not supported [" + date + "].");
        }
        int first = parseInt(array[0], "Date", date);
        int second = parseInt(array[1], "Date", date);
        int third = parseInt(array[2], "Date", date);
        Integer year = null;
        Integer month = null;
        Integer day = null;
        if (array[0].length() > 2 || first > 31) { // 年/月/日, 例如: 2018/7/8; 0006/7/8(公元6年)
            year = array[0].length() > 2 ? first : parseYear(first);
            month = second;
            day = third;
        } else if (array[1].length() > 2 || second > 31) { // 年份出现在中间, 例如: 7/2018/8
            throw new IllegalArgumentException("Date format is not supported [" + date + "].");
        } else if (array[2].length() > 2 || third > 31) { // 月/日/年;日/月/年
            year = array[2].length() > 2 ? third : parseYear(third);
            if (first > 12) { // 日/月/年, 例如: 20/8/2018
                month = second;
                day = first;
            } else { // 月/日/年, 例如: 8/20/2018; 7/8/2018(无法识别月日顺序,按月日年处理)
                month = first;
                day = second;
            }
        } else { // 三个数字都没有大于31, 按年/月/日处理
            // 6/7/8(在excel中文版输入6/7/8会显示为2006年7月8日)
            year = parseYear(first);
            month = second;
            day = third;
        }
        if (year > 9999 || month > 12 || day > 31) { // 2018/20/20, 2018/20/8, 2018/8/32, 20/20/2018
            throw new IllegalArgumentException("Date format is not supported [" + date + "].");
        }
        return DateTools.of(year, month - 1, day);
    }

    private static Date parseTime(Date date, String string) {
        long millis = parseTime(string);
        return DateTools.addMillisecond(date, (int) millis);
    }

    /**
     * 解析时间<br>
     * 毫秒分隔符支持点或逗号, 10:20:30.999或10:20:30,999(FastJson支持逗号)<br>
     * 带毫秒或不带毫秒, 10:20:30, 10:5:8.360<br>
     * 毫秒如果超过三位只截止前三位, 10:20:30.999999=10:20:30.999
     * 
     * @param string 时间字符串
     * @return 时间毫秒数
     */
    private static long parseTime(String string) {
        if (string == null || string.trim().length() == 0) {
            return 0;
        }
        String[] parts = StringTools.split(string.trim(), '.', ',');
        if (parts.length != 1 && parts.length != 2) {
            throw new IllegalArgumentException("Time format is not supported [" + string + "].");
        }
        String time = parts[0];
        if (VerifyTools.isBlank(time)) {
            throw new IllegalArgumentException("Time format is not supported [" + string + "].");
        }
        // 解析毫秒数
        int millis = 0;
        if (parts.length == 2) {
            String millisString = parts[1];
            if (VerifyTools.isAnyBlank(millisString)) {
                throw new IllegalArgumentException("Time format is not supported [" + string + "].");
            }
            // 毫秒如果超过三位只截止前三位, 10:20:30.999999=10:20:30.999
            if (millisString.length() > 3) {
                millisString = millisString.substring(0, 3);
            }
            millis = parseInt(millisString, "Time", string);
        }
        // 解析时分秒
        String[] array = StringTools.split(time, ':');
        if (array.length != 3) { // 不是三个数字
            throw new IllegalArgumentException("Time format is not supported [" + time + "].");
        }
        for (int i = 0; i < array.length; i++) {
            if (array[i].length() > 2) {
                throw new IllegalArgumentException("Time format is not supported [" + time + "].");
            }
        }
        int hours = parseInt(array[0], "Time", time);
        int minutes = parseInt(array[1], "Time", time);
        int seconds = parseInt(array[2], "Time", time);
        if (hours > 23 || minutes > 59 || seconds > 59) {
            throw new IllegalArgumentException("Time format is not supported [" + time + "].");
        }
        return hours * RATE_HOUR + minutes * RATE_MINUTE + seconds * RATE_SECOND + millis;
    }

    /** 解析年份: 大于99的为公元年份; 小于等于99的, 30及以上为19XX年, 小于等于29的为20XX年 **/
    // 在excel中文版输入6/7/8会显示为2006年7月8日
    // 在excel中文版输入29/7/8会显示为2029年7月8日
    // 在excel中文版输入30/7/8会显示为1930年7月8日
    private static int parseYear(int number) {
        if (number > 99 || number < 0) {
            return number;
        } else if (number < 30) {
            return 2000 + number;
        } else {
            return 1900 + number;
        }
    }

    private static int parseInt(String value, String type, String original) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            String fmt = "%s format is not supported [%s], number format error [%s].";
            throw new IllegalArgumentException(String.format(fmt, type, original, value));
        }
    }

    /** 按指定格式解析日期 **/
    public static Date parse(String date, String pattern) {
        if (pattern == null) {
            throw new NullPointerException();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Date format is not supported.", e);
        }
    }

    /** 手动设置日期的各个部分(月份从0开始) **/
    public static Date of(int year, int month, int day) {
        return of(year, month, day, 0, 0, 0, 0);
    }

    /** 手动设置日期的各个部分(月份从0开始) **/
    public static Date of(int year, int month, int day, int hour, int minute, int second) {
        return of(year, month, day, hour, minute, second, 0);
    }

    /** 手动设置日期的各个部分(月份从0开始) **/
    public static Date of(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    /**
     * 将日期格式化为字符串
     *
     * @param date 待处理的日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String format(Date date, String pattern) {
        if (pattern == null) {
            throw new NullPointerException();
        }
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 转换为标准的字符串, 如 2012-08-08 20:00:00.000
     *
     * @param date 待处理的日期
     * @return 日期字符串
     */
    public static String toNormativeString(Date date) {
        return toNormativeString(date, false);
    }

    /**
     * 转换为标准的字符串, 如 2012-08-08 20:00:00.000
     *
     * @param date 待处理的日期
     * @param compact 紧凑格式, 纯数字没有分隔符
     * @return 日期字符串
     */
    public static String toNormativeString(Date date, boolean compact) {
        if (date == null) {
            return null;
        }
        String fmt = compact ? PATTERN_COMPACT_NORMATIVE : PATTERN_GENERAL_NORMATIVE;
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    /**
     * 转换为字符串<br>
     * 如果有时间部分就返回 2012-08-08 20:00:00; 如果没有时间部分返回 2012-08-08<br>
     *
     * @param date 待处理的日期
     * @return 日期字符串
     */
    public static String toAutoString(Date date) {
        if (date == null) {
            return null;
        }
        String string = toDateTimeString(date, false);
        return string.replace(" 00:00:00", "");
    }

    /**
     * 转换为日期字符串, 如 2012-08-08
     *
     * @param date 待处理的日期
     * @return 日期字符串
     */
    public static String toDateString(Date date) {
        return toDateString(date, false);
    }

    /**
     * 转换为日期字符串, 如 2012-08-08
     *
     * @param date 待处理的日期
     * @param compact 紧凑格式, 纯数字没有分隔符
     * @return 日期字符串
     */
    public static String toDateString(Date date, boolean compact) {
        if (date == null) {
            return null;
        }
        String fmt = compact ? PATTERN_COMPACT_DATE : PATTERN_GENERAL_DATE;
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    /**
     * 转换为时间字符串, 如 20:00:00
     *
     * @param date 待处理的日期
     * @return 时间字符串
     */
    public static String toTimeString(Date date) {
        return toTimeString(date, false);
    }

    /**
     * 转换为时间字符串, 如 20:00:00
     *
     * @param date 待处理的日期
     * @param compact 紧凑格式, 纯数字没有分隔符
     * @return 时间字符串
     */
    public static String toTimeString(Date date, boolean compact) {
        if (date == null) {
            return null;
        }
        String fmt = compact ? PATTERN_COMPACT_TIME : PATTERN_GENERAL_TIME;
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    /**
     * 转换为日期加时间字符串, 如 2012-08-08 20:00:00
     *
     * @param date 待处理的日期
     * @return 日期字符串
     */
    public static String toDateTimeString(Date date) {
        return toDateTimeString(date, false);
    }

    /**
     * 转换为日期加时间字符串, 如 2012-08-08 20:00:00
     *
     * @param date 待处理的日期
     * @param compact 紧凑格式, 纯数字没有分隔符
     * @return 日期字符串
     */
    public static String toDateTimeString(Date date, boolean compact) {
        if (date == null) {
            return null;
        }
        String fmt = compact ? PATTERN_COMPACT_DATETIME : PATTERN_GENERAL_DATETIME;
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    /**
     * 转换为第1时间<br>
     * Calendar.YEAR=当年第1时间, Calendar.MONTH=当月第1时间, Calendar.DAY_OF_MONTH=当日第1时间, ...<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.YEAR) --- 2016-01-01 00:00:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.MONTH) --- 2016-08-01 00:00:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.DAY_OF_MONTH) --- 2016-08-08 00:00:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.HOUR_OF_DAY) --- 2016-08-08 20:00:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.MINUTE) --- 2016-08-08 20:30:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.SECOND) --- 2016-08-08 20:30:40.000<br>
     *
     * @param date 待处理的日期
     * @param field 类型
     * @return 第1时间
     */
    public static Date toFirstTime(Date date, int field) {
        return toFirstTime(date, field, 0);
    }

    /**
     * 转换为第1时间<br>
     * Calendar.YEAR=当年第1时间, Calendar.MONTH=当月第1时间, Calendar.DAY_OF_MONTH=当日第1时间, ...<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.YEAR, 1) --- 2017-01-01 00:00:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.MONTH, 1) --- 2016-09-01 00:00:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.DAY_OF_MONTH, 1) --- 2016-08-09 00:00:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.HOUR_OF_DAY, 1) --- 2016-08-08 21:00:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.MINUTE, 1) --- 2016-08-08 20:31:00.000<br>
     * 如 toFirstTime(2016-08-08 20:30:40.500, Calendar.SECOND, 1) --- 2016-08-08 20:30:41.000<br>
     *
     * @param date 待处理的日期
     * @param field 类型
     * @param offset 类型的偏移量
     * @return 第1时间
     */
    private static Date toFirstTime(Date date, int field, int offset) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        addCalendarFieldOffset(calendar, field, offset);
        switch (field) {
        case Calendar.YEAR:
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
        case Calendar.MONTH:
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        case Calendar.DAY_OF_MONTH:
            calendar.set(Calendar.HOUR_OF_DAY, 0);
        case Calendar.HOUR_OF_DAY:
            calendar.set(Calendar.MINUTE, 0);
        case Calendar.MINUTE:
            calendar.set(Calendar.SECOND, 0);
        case Calendar.SECOND:
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar.getTime();
    }

    /**
     * 转换为最后时间<br>
     * Calendar.YEAR=当年最后时间, Calendar.MONTH=当月最后时间, Calendar.DAY_OF_MONTH=当日最后时间, ...<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.YEAR) --- 2016-12-31 23:59:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.MONTH) --- 2016-08-31 23:59:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.DAY_OF_MONTH) --- 2016-08-08 23:59:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.HOUR_OF_DAY) --- 2016-08-08 20:59:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.MINUTE) --- 2016-08-08 20:30:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.SECOND) --- 2016-08-08 20:30:40.999<br>
     *
     * @param date 待处理的日期
     * @param field 类型
     * @return 最后时间
     */
    public static Date toLastTime(Date date, int field) {
        return toLastTime(date, field, 0);
    }

    /**
     * 转换为最后时间<br>
     * Calendar.YEAR=当年最后时间, Calendar.MONTH=当月最后时间, Calendar.DAY_OF_MONTH=当日最后时间, ...<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.YEAR, 1) --- 2017-12-31 23:59:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.MONTH, 1) --- 2016-09-30 23:59:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.DAY_OF_MONTH, 1) --- 2016-08-09 23:59:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.HOUR_OF_DAY, 1) --- 2016-08-08 21:59:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.MINUTE, 1) --- 2016-08-08 20:31:59.999<br>
     * 如 toLastTime(2016-08-08 20:30:40.500, Calendar.SECOND, 1) --- 2016-08-08 20:30:41.999<br>
     *
     * @param date 待处理的日期
     * @param field 类型
     * @return 最后时间
     */
    private static Date toLastTime(Date date, int field, int offset) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        addCalendarFieldOffset(calendar, field, offset);
        switch (field) {
        case Calendar.YEAR:
            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        case Calendar.MONTH:
            // 下月1日的前一天
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        case Calendar.DAY_OF_MONTH:
            calendar.set(Calendar.HOUR_OF_DAY, 23);
        case Calendar.HOUR_OF_DAY:
            calendar.set(Calendar.MINUTE, 59);
        case Calendar.MINUTE:
            calendar.set(Calendar.SECOND, 59);
        case Calendar.SECOND:
            calendar.set(Calendar.MILLISECOND, 999);
        }
        return calendar.getTime();
    }

    private static void addCalendarFieldOffset(Calendar calendar, int field, int offset) {
        if (offset == 0) {
            return;
        }
        if (field == Calendar.YEAR) {
            calendar.add(Calendar.YEAR, offset);
        } else if (field == Calendar.MONTH) {
            calendar.add(Calendar.MONTH, offset);
        } else if (field == Calendar.DAY_OF_MONTH) {
            calendar.add(Calendar.DAY_OF_MONTH, offset);
        } else if (field == Calendar.HOUR_OF_DAY) {
            calendar.add(Calendar.HOUR_OF_DAY, offset);
        } else if (field == Calendar.MINUTE) {
            calendar.add(Calendar.MINUTE, offset);
        } else if (field == Calendar.SECOND) {
            calendar.add(Calendar.SECOND, offset);
        } else if (field == Calendar.MILLISECOND) {
            calendar.add(Calendar.MILLISECOND, offset);
        }
    }

    /**
     * 转换为开始时间, 即设置时分秒为00:00:00
     *
     * @param date 待处理的日期
     * @return 开始时间
     */
    public static Date toStartTime(Date date) {
        if (date == null) {
            return null;
        }
        return toFirstTime(date, Calendar.DAY_OF_MONTH, 0);
    }

    /**
     * 转换为结束时间, 即设置时分秒为23:59:59
     *
     * @param date 待处理的日期
     * @return 结束时间
     */
    public static Date toEndTime(Date date) {
        if (date == null) {
            return null;
        }
        return toLastTime(date, Calendar.DAY_OF_MONTH, 0);
    }

    /**
     * 转换为下一天的开始时间, 即日期加1并设置时分秒为00:00:00
     *
     * @param date 待处理的日期
     * @return 下一天的开始时间
     */
    public static Date toNextStartTime(Date date) {
        if (date == null) {
            return null;
        }
        return toFirstTime(date, Calendar.DAY_OF_MONTH, 1);
    }

    /**
     * 转换为下一天的结束时间, 即日期加1并设置时分秒为23:59:59
     *
     * @param date 待处理的日期
     * @return 下一天的结束时间
     */
    public static Date toNextEndTime(Date date) {
        if (date == null) {
            return null;
        }
        return toLastTime(date, Calendar.DAY_OF_MONTH, 1);
    }

    /**
     * 如果未设置时间(即时间部分是00:00:00)则设置为23:59:59
     *
     * @param date 待处理的日期
     * @return 结束时间
     */
    public static Date toEndTimeIfZeroTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int[] fields = new int[] { Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND };
        for (int i = 0; i < fields.length; i++) {
            if (calendar.get(fields[i]) != 0) {
                return date;
            }
        }
        return toLastTime(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当天的毫秒数
     *
     * @param date
     * @return
     */
    public static long getMillisOfDay(Date date) {
        Date start = toStartTime(date);
        return date.getTime() - start.getTime();
    }

    /** 日期加几年(负数为减) **/
    public static Date addYear(Date date, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /** 日期加几个月(负数为减) **/
    public static Date addMonth(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }

    /** 日期加几天(负数为减) **/
    public static Date addDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /** 日期加几个小时(负数为减) **/
    public static Date addHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hour);
        return calendar.getTime();
    }

    /** 日期加几分钟(负数为减) **/
    public static Date addMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    /** 日期加几秒(负数为减) **/
    public static Date addSecond(Date date, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }

    /** 日期加几毫秒(负数为减) **/
    public static Date addMillisecond(Date date, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    /** 日期设置年 **/
    public static Date setYear(Date date, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /** 日期设置月(从0开始) **/
    public static Date setMonth(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, month);
        return calendar.getTime();
    }

    // /** 日期设置月 **/
    // public static Date setMonth(Date date, Month month) {
    //     Calendar calendar = Calendar.getInstance();
    //     calendar.setTime(date);
    //     calendar.set(Calendar.MONTH, month.ordinal());
    //     return calendar.getTime();
    // }

    /** 日期设置日 **/
    public static Date setDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /** 日期设置小时 **/
    public static Date setHour(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, hour);
        return calendar.getTime();
    }

    /** 日期设置分钟 **/
    public static Date setMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    /** 日期设置秒 **/
    public static Date setSecond(Date date, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }

    /** 日期设置毫秒 **/
    public static Date setMillisecond(Date date, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    /** 获取日期的年份 **/
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /** 获取日期的月份(从0开始) **/
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    /** 获取日期的日 **/
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /** 获取日期位于一年的几周(从1开始) **/
    public static int getWeekOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取今天是星期几
     *
     * @return 今天是星期几
     * @see Calendar#SUNDAY
     * @see Calendar#MONDAY
     * @see Calendar#TUESDAY
     * @see Calendar#WEDNESDAY
     * @see Calendar#THURSDAY
     * @see Calendar#FRIDAY
     * @see Calendar#SATURDAY
     */
    public static int getDayOfWeek() {
        return getDayOfWeek(new Date());
    }

    /**
     * 获取当前日期是星期几
     *
     * @param date 当前日期
     * @return 当前日期是星期几
     * @see Calendar#SUNDAY
     * @see Calendar#MONDAY
     * @see Calendar#TUESDAY
     * @see Calendar#WEDNESDAY
     * @see Calendar#THURSDAY
     * @see Calendar#FRIDAY
     * @see Calendar#SATURDAY
     */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /** 获取日期的时 **/
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR);
    }

    /** 获取日期的分 **/
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /** 获取日期的秒 **/
    public static int getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /** 获取日期的毫秒 **/
    public static int getMillisecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MILLISECOND);
    }

    /** 计算表达式 **/
    private static Pattern CALC_EXP = Pattern.compile("([+\\-]?\\d+)\\s*([a-zA-Z])");

    /**
     * 计算相对日期<br>
     * DateTools.calculate("2016-03-01", "-1d"); = 2016-02-29<br>
     * DateTools.calculate("2016-03-01", "-2M"); = 2016-01-01<br>
     * DateTools.calculate("2016-03-01", "-3y"); = 2013-03-01<br>
     * DateTools.calculate("2016-03-01", "+2d"); = 2016-03-03<br>
     * DateTools.calculate("2016-03-01", "-2M+3d"); = 2016-01-04<br>
     * 
     * @param date 日期
     * @param expression 表达式
     * @return 计算结果
     */
    public static Date calculate(Date date, String expression) {
        if (date == null || VerifyTools.isBlank(expression) || VerifyTools.isBlank(expression.trim())) {
            return date;
        }
        String exp = expression.trim();
        if (StringTools.isDigit(exp)) {
            int value = ConvertTools.toInteger(exp);
            return addDay(date, value);
        } else {
            // "-3d"/"-2M"/"+1y"之类的相对日期
            Matcher matcher = CALC_EXP.matcher(exp);
            int lastIndex = 0;
            Calendar d = Calendar.getInstance();
            d.setTime(date);
            while (matcher.find()) {
                if (matcher.start() > lastIndex) {
                    String temp = exp.substring(lastIndex, matcher.start());
                    if (temp.trim().length() > 0) { // 两个表达式之间有非空字符串
                        String m = "Format error '" + temp + "' in expression '" + exp + "'";
                        throw new IllegalArgumentException(m);
                    }
                }
                int number = ConvertTools.toInteger(matcher.group(1));
                String type = matcher.group(2);
                switch (type) {
                case "y":
                case "Y":
                    d.set(Calendar.YEAR, d.get(Calendar.YEAR) + number);
                    break;
                case "M":
                    d.set(Calendar.MONTH, d.get(Calendar.MONTH) + number);
                    break;
                case "d":
                    d.set(Calendar.DAY_OF_MONTH, d.get(Calendar.DAY_OF_MONTH) + number);
                    break;
                case "h":
                case "H":
                    d.set(Calendar.HOUR_OF_DAY, d.get(Calendar.HOUR_OF_DAY) + number);
                    break;
                case "m":
                    d.set(Calendar.MINUTE, d.get(Calendar.MINUTE) + number);
                    break;
                case "s":
                    d.set(Calendar.SECOND, d.get(Calendar.SECOND) + number);
                    break;
                case "S":
                    d.set(Calendar.MILLISECOND, d.get(Calendar.MILLISECOND) + number);
                    break;
                default:
                    String m = "Unsupported type '" + type + "' in expression '" + exp + "'";
                    throw new IllegalArgumentException(m);
                }
                lastIndex = matcher.end();
            }
            if (lastIndex < exp.length()) {
                if (lastIndex == 0) {
                    throw new IllegalArgumentException("Unsupported expression '" + exp + "'");
                } else {
                    String temp = exp.substring(lastIndex);
                    if (temp.trim().length() > 0) {
                        String m = "Format error '" + temp + "' in expression '" + exp + "'";
                        throw new IllegalArgumentException(m);
                    }
                }
            }
            return d.getTime();
        }
    }
}
