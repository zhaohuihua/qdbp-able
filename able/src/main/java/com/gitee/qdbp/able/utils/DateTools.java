package com.gitee.qdbp.able.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    // 解析日期时支持的格式
    private static DateParsers PARSERS = new DateParsers(PATTERN_GENERAL_NORMATIVE, PATTERN_COMPACT_NORMATIVE,
            PATTERN_GENERAL_DATE, PATTERN_COMPACT_DATE, PATTERN_GENERAL_TIME, PATTERN_COMPACT_TIME,
            PATTERN_GENERAL_DATETIME, PATTERN_COMPACT_DATETIME);

    /**
     * 日期解析类
     * 
     * @author zhaohuihua
     * @version 170330
     */
    private static class DateParser {

        /** 正则表达式转义字符 **/
        private static final Pattern REG_CHAR = Pattern.compile("([\\{\\}\\[\\]\\(\\)\\^\\$\\.\\*\\?\\-\\+\\\\])");
        /** 日期格式字符 **/
        private static final Pattern DATE_CHAR = Pattern.compile("[yMdHmsS]");

        private int length;
        private Pattern regexp;
        private String pattern;

        public DateParser(String pattern) {
            this.length = pattern.length();
            this.pattern = pattern;
            String regexp = REG_CHAR.matcher(pattern).replaceAll("\\\\$1");
            regexp = DATE_CHAR.matcher(pattern).replaceAll("[0-9]");
            this.regexp = Pattern.compile(regexp);
        }

        public boolean supported(String date) {
            return date.length() == length && this.regexp.matcher(date).matches();
        }

        public Date parse(String date) {
            try {
                return new SimpleDateFormat(this.pattern).parse(date);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Date format is not supported.", e);
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
            throw new IllegalArgumentException("Date format is not supported.");
        }
    }

    /**
     * 按标准格式解析日期<br>
     * parse("2000-10-20 15:25:35.450");<br>
     * parse("20001020152535450");<br>
     * parse("2000-10-20 15:25:35");<br>
     * parse("20001020152535");<br>
     * parse("2000-10-20");<br>
     * parse("20001020");<br>
     * parse("15:25:35");<br>
     * parse("152535");<br>
     * 
     * @param string 日期字符串
     * @return 日期
     */
    public static Date parse(String string) {
        if (string == null) {
            return null;
        }
        return PARSERS.parse(string);
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
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
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
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
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

    /**
     * 转换为结束时间, 即设置时分秒为00:00:00
     *
     * @param date 待处理的日期
     * @return 结束时间
     */
    public static Date toStartTime(Date date) {
        if (date == null) {
            return null;
        }
        return toFirstTime(date, Calendar.DAY_OF_MONTH);
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

    /** 日期设置月 **/
    public static Date setMonth(Date date, Month month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, month.ordinal());
        return calendar.getTime();
    }

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

    /** 获取当天是星期几(从0开始) **/
    public static DayOfWeek getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return DayOfWeek.of(calendar.get(Calendar.DAY_OF_WEEK));
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
    public static int getWeekOfDate() {
        return getWeekOfDate(new Date());
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
    public static int getWeekOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
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
                    if (temp.trim().length() > 0) {
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
