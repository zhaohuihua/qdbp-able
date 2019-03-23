package com.gitee.qdbp.able.utils;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 格式转换工具类
 *
 * @author zhaohuihua
 * @version 151221
 */
public abstract class ConvertTools {

    /**
     * List转数组
     * 
     * @param list 待转换的List
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @param <C> List内容的类型
     * @return 转换后的数组, 如果list=null则返回null
     */
    public static <T, C extends T> T[] toArray(Collection<C> list, Class<T> clazz) {
        if (list == null) return null;

        int size = list.size();
        @SuppressWarnings("unchecked")
        T[] copy = clazz == Object[].class ? (T[]) new Object[size] : (T[]) Array.newInstance(clazz, size);
        return size == 0 ? copy : list.toArray(copy);
    }

    /**
     * List转数组
     * 
     * @param list 待转换的List
     * @param clazz 目标类型
     * @param <T> 目标类型
     * @param <C> List内容的类型
     * @return 转换后的数组, 如果list=null则返回T[0]
     */
    public static <T, C extends T> T[] toArrayIfNullToEmpty(Collection<C> list, Class<T> clazz) {
        int size = list == null ? 0 : list.size();
        @SuppressWarnings("unchecked")
        T[] copy = clazz == Object[].class ? (T[]) new Object[size] : (T[]) Array.newInstance(clazz, size);
        return size == 0 ? copy : list.toArray(copy);
    }

    /**
     * 数组转List
     * 
     * @param array 数组
     * @param <T> 目标类型
     * @param <C> 数组内容的类型
     * @return 转换后的List, 如果array=null则返回null
     */
    @SafeVarargs
    public static <T, C extends T> List<T> toList(C... array) {
        if (array == null) {
            return null;
        } else {
            // JDK1.7必须强转, JDK1.8不需要
            @SuppressWarnings("unchecked")
            List<T> temp = (List<T>) new ArrayList<>(Arrays.asList(array));
            return temp;
        }
    }

    /**
     * 数组转List
     * 
     * @param array 数组
     * @param <T> 目标类型
     * @param <C> 数组内容的类型
     * @return 转换后的List, 如果array=null则返回EmptyList
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T, C extends T> List<T> toListIfNullToEmpty(C... array) {
        // JDK1.7必须强转, JDK1.8不需要
        return (List<T>) (array == null ? Collections.emptyList() : Arrays.asList(array));
    }

    /**
     * 数组转Set
     * 
     * @param array 数组
     * @param <T> 目标类型
     * @param <C> 数组内容的类型
     * @return 转换后的Set, 如果array=null则返回null
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T, C extends T> Set<T> toSet(C... array) {
        if (array == null) {
            return null;
        } else {
            Set<C> list = new HashSet<>();
            for (C field : array) {
                list.add(field);
            }
            // JDK1.7必须强转, JDK1.8不需要
            return (Set<T>) list;
        }
    }

    /**
     * 数组转Set
     * 
     * @param array 数组
     * @param <T> 目标类型
     * @param <C> 数组内容的类型
     * @return 转换后的Set, 如果array=null则返回EmptySet
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T, C extends T> Set<T> toSetIfNullToEmpty(C... array) {
        if (array == null) {
            return Collections.emptySet();
        } else {
            Set<C> list = new HashSet<>();
            for (C field : array) {
                list.add(field);
            }
            // JDK1.7必须强转, JDK1.8不需要
            return (Set<T>) list;
        }
    }

    /**
     * 对象转换为字符串<br>
     * 与String.valueOf()的区别是如果对象为null则返回null
     *
     * @param object 对象
     * @return 字符串
     */
    public static String toString(Object object) {
        return object == null ? null : String.valueOf(object);
    }

    /**
     * 转换为数字
     *
     * @param value 源字符串, 支持乘法
     * @return 数字
     * @throws NumberFormatException 数字格式错误
     */
    public static int toInteger(String value) throws NumberFormatException {
        if (VerifyTools.isBlank(value)) {
            throw new NumberFormatException("null");
        }
        Long number = toLong(value, null);
        if (number == null) {
            throw new NumberFormatException(value);
        } else {
            return number.intValue();
        }
    }

    /**
     * 转换为数字
     *
     * @param value 源字符串, 支持乘法
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 数字
     */
    public static Integer toInteger(String value, Integer defaults) {
        Long number = toLong(value, defaults == null ? null : Long.valueOf(defaults.intValue()));
        return number == null ? null : number.intValue();
    }

    /**
     * 转换为数字
     *
     * @param value 源字符串, 支持乘法
     * @return 数字
     * @throws NumberFormatException 数字格式错误
     */
    public static long toLong(String value) throws NumberFormatException {
        if (VerifyTools.isBlank(value)) {
            throw new NumberFormatException("null");
        }
        Long number = toLong(value, 0L);
        if (number == null) {
            throw new NumberFormatException(value);
        } else {
            return number;
        }
    }

    /**
     * 转换为数字
     *
     * @param value 源字符串, 支持乘法
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 数字
     */
    public static Long toLong(String value, Long defaults) {
        if (VerifyTools.isBlank(value)) {
            return defaults;
        }

        value = value.trim();

        if (!value.contains("*")) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return defaults;
            }
        } else {
            Pattern ptn = Pattern.compile("\\*");
            String[] values = ptn.split(value);
            long number = 1;
            for (String string : values) {
                string = string.trim();
                try {
                    number *= Long.parseLong(string);
                } catch (NumberFormatException e) {
                    return defaults;
                }
            }
            return number;
        }
    }

    /**
     * 转换为数字
     *
     * @param value 源字符串, 支持乘法
     * @return 数字
     * @throws NumberFormatException 数字格式错误
     */
    public static float toFloat(String value) throws NumberFormatException {
        if (VerifyTools.isBlank(value)) {
            throw new NumberFormatException("null");
        }
        Double number = toDouble(value, 0D);
        if (number == null) {
            throw new NumberFormatException(value);
        } else {
            return number.floatValue();
        }
    }

    /**
     * 转换为数字
     *
     * @param value 源字符串, 支持乘法
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 数字
     */
    public static Float toFloat(String value, Float defaults) {
        Long number = toLong(value, 0L);
        return number == null ? null : number.floatValue();
    }

    /**
     * 转换为数字
     *
     * @param value 源字符串, 支持乘法
     * @return 数字
     * @throws NumberFormatException 数字格式错误
     */
    public static double toDouble(String value) throws NumberFormatException {
        if (VerifyTools.isBlank(value)) {
            throw new NumberFormatException("null");
        }
        Double number = toDouble(value, 0D);
        if (number == null) {
            throw new NumberFormatException(value);
        } else {
            return number;
        }
    }

    /**
     * 转换为数字
     *
     * @param value 源字符串, 支持乘法
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 数字
     */
    public static Double toDouble(String value, Double defaults) throws NumberFormatException {
        if (VerifyTools.isBlank(value)) {
            return defaults;
        }

        value = value.trim();

        if (!value.contains("*")) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return defaults;
            }
        } else {
            Pattern ptn = Pattern.compile("\\*");
            String[] values = ptn.split(value);
            double number = 1.0;
            for (String string : values) {
                string = string.trim();
                try {
                    number *= Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return defaults;
                }
            }
            return number;
        }
    }

    /**
     * 转换为Boolean
     * 
     * @param value 源字符串
     * @return 解析结果
     * @throws IllegalArgumentException 格式错误
     */
    public static boolean toBoolean(String value) throws IllegalArgumentException {
        if (VerifyTools.isBlank(value)) {
            throw new IllegalArgumentException("null");
        }
        Boolean number = toBoolean(value, null);
        if (number == null) {
            throw new IllegalArgumentException(value);
        } else {
            return number;
        }
    }

    /**
     * 转换为Boolean
     * 
     * @param value 源字符串
     * @param defaults 默认值, 在表达式为空/表达式格式错误/表达式结果不是数字时返回默认值
     * @return 解析结果
     */
    public static Boolean toBoolean(String value, Boolean defaults) {
        if (VerifyTools.isBlank(value)) {
            return defaults;
        }

        value = value.trim();

        if (StringTools.isPositive(value, false)) {
            return true;
        } else if (StringTools.isNegative(value, false)) {
            return false;
        } else {
            return defaults;
        }
    }

    /**
     * 转换为持续时间字符串<br>
     * 8000=00:00:08<br>
     * 488000=00:08:08<br>
     * 11288000=03:08:08<br>
     * 97688000=1天03:08:08<br>
     * 31536000000=365天00:00:00<br>
     *
     * @param begin 开始时间
     * @param end 结束时间
     * @return 持续时间字符串
     */
    public static String toDuration(Date begin, Date end) {
        return toDuration(begin, end, false);
    }

    /**
     * 转换为持续时间字符串<br>
     * 8000=00:00:08<br>
     * 488000=00:08:08<br>
     * 11288000=03:08:08<br>
     * 97688000=1天03:08:08<br>
     * 31536000000=365天00:00:00<br>
     *
     * @param begin 开始时间
     * @param useMillisecond 是否需要显示毫秒数
     * @return 持续时间字符串
     */
    public static String toDuration(Date begin, Date end, boolean useMillisecond) {
        return toDuration(end.getTime() - begin.getTime(), useMillisecond);
    }

    /**
     * 转换为持续时间字符串<br>
     * 8000=00:00:08<br>
     * 488000=00:08:08<br>
     * 11288000=03:08:08<br>
     * 97688000=1天03:08:08<br>
     * 31536000000=365天00:00:00<br>
     *
     * @param begin 开始时间
     * @return 持续时间字符串
     */
    public static String toDuration(Date begin) {
        return toDuration(begin, false);
    }

    /**
     * 转换为持续时间字符串<br>
     * 8000=00:00:08<br>
     * 488000=00:08:08<br>
     * 11288000=03:08:08<br>
     * 97688000=1天03:08:08<br>
     * 31536000000=365天00:00:00<br>
     *
     * @param begin 开始时间
     * @param useMillisecond 是否需要显示毫秒数
     * @return 持续时间字符串
     */
    public static String toDuration(Date begin, boolean useMillisecond) {
        return toDuration(System.currentTimeMillis() - begin.getTime(), useMillisecond);
    }

    /**
     * 转换为持续时间字符串<br>
     * 8000=00:00:08<br>
     * 488000=00:08:08<br>
     * 11288000=03:08:08<br>
     * 97688000=1天03:08:08<br>
     * 31536000000=365天00:00:00<br>
     *
     * @param time 持续时间
     * @return 持续时间字符串
     */
    public static String toDuration(long time) {
        return toDuration(time, false);
    }

    /**
     * 转换为持续时间字符串<br>
     * 8000=00:00:08<br>
     * 488000=00:08:08<br>
     * 11288000=03:08:08<br>
     * 97688000=1天03:08:08<br>
     * 31536000000=365天00:00:00<br>
     *
     * @param time 持续时间
     * @param useMillisecond 是否需要显示毫秒数
     * @return 持续时间字符串
     */
    public static String toDuration(long time, boolean useMillisecond) {
        long day = 24 * 60 * 60 * 1000;
        String pattern = useMillisecond ? "HH:mm:ss.SSS" : "HH:mm:ss";
        SimpleDateFormat fmt = new SimpleDateFormat(pattern, Locale.US);
        fmt.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String string = fmt.format(new Date(time));
        if (time >= day) {
            string = (time / day) + "天" + string;
        }
        return string;
    }

    // KB/MB/GB/TB/PB/EB/ZB/YB
    private static long kibibyte = 1024;
    private static long mebibyte = kibibyte * kibibyte;
    private static long gibibyte = mebibyte * kibibyte;
    private static long tebibyte = gibibyte * kibibyte;
    private static long pebibyte = tebibyte * kibibyte;
    private static long exbibyte = pebibyte * kibibyte;
    private static long zebibyte = exbibyte * kibibyte;
    private static long yobibyte = zebibyte * kibibyte;
    private static Map<String, Long> BYTE_UNITS = new HashMap<>();
    static { // 单位对应的倍数
        BYTE_UNITS.put("B", 1L);
        BYTE_UNITS.put("KB", kibibyte);
        BYTE_UNITS.put("MB", mebibyte);
        BYTE_UNITS.put("GB", gibibyte);
        BYTE_UNITS.put("TB", tebibyte);
        BYTE_UNITS.put("PB", pebibyte);
        BYTE_UNITS.put("EB", exbibyte);
        BYTE_UNITS.put("ZB", zebibyte);
        BYTE_UNITS.put("YB", yobibyte);
        BYTE_UNITS.put("KiB".toUpperCase(), kibibyte);
        BYTE_UNITS.put("MiB".toUpperCase(), mebibyte);
        BYTE_UNITS.put("GiB".toUpperCase(), gibibyte);
        BYTE_UNITS.put("TiB".toUpperCase(), tebibyte);
        BYTE_UNITS.put("PiB".toUpperCase(), pebibyte);
        BYTE_UNITS.put("EiB".toUpperCase(), exbibyte);
        BYTE_UNITS.put("ZiB".toUpperCase(), zebibyte);
        BYTE_UNITS.put("YiB".toUpperCase(), yobibyte);
    }

    /**
     * 转换为Byte描述字符串
     *
     * @param size B
     * @return B/KB/MB/GB/TB/PB/EB/ZB/YB
     */
    public static String toByteString(long size) {
        DecimalFormat df = new DecimalFormat("0.##");
        if (size < kibibyte) {
            return df.format(size) + "B";
        } else if (size < mebibyte) {
            return df.format(1.0 * size / kibibyte) + "KB";
        } else if (size < gibibyte) {
            return df.format(1.0 * size / mebibyte) + "MB";
        } else if (size < tebibyte) {
            return df.format(1.0 * size / gibibyte) + "GB";
        } else if (size < pebibyte) {
            return df.format(1.0 * size / tebibyte) + "TB";
        } else if (size < exbibyte) {
            return df.format(1.0 * size / pebibyte) + "PB";
        } else if (size < zebibyte) {
            return df.format(1.0 * size / exbibyte) + "EB";
        } else if (size < yobibyte) {
            return df.format(1.0 * size / zebibyte) + "ZB";
        } else {
            return df.format(1.0 * size / yobibyte) + "YB";
        }
    }

    /**
     * 将Byte描述字符串转换字节数
     *
     * @param string B/KB/MB/GB/TB/PB/EB/ZB/YB
     * @return number of bytes
     */
    public static long parseByteString(String string) {
        Objects.requireNonNull(string, "ByteString");
        // 截取数字和单位
        String number = string.trim();
        String unit = null;
        char[] chars = number.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int p = chars.length - 1 - i;
            if (Character.isDigit(chars[p])) {
                if (i > 0) {
                    number = string.substring(0, p);
                    unit = string.substring(p + 1);
                }
                break;
            }
        }
        if (VerifyTools.isBlank(number)) {
            throw new NumberFormatException(string);
        }
        // 计算数值
        if (unit == null) {
            return Long.parseLong(number);
        } else {
            Long rate = BYTE_UNITS.get(unit.toUpperCase());
            if (rate == null) {
                throw new NumberFormatException(string);
            }
            return Long.parseLong(number) * rate;
        }
    }

    /**
     * 将数组合并为字符串
     *
     * @param list 数组
     * @return 合并后的字符串
     */
    public static String joinToString(Collection<?> list) {
        return joinToString(list, ",");
    }

    /**
     * 将数组合并为字符串
     *
     * @param list 数组
     * @param seprator 分隔符, 可为空
     * @return 合并后的字符串
     */
    public static String joinToString(Collection<?> list, String seprator) {

        StringBuilder buffer = new StringBuilder();
        if (VerifyTools.isNotBlank(list)) {
            for (Object tmp : list) {
                if (seprator != null && buffer.length() > 0) {
                    buffer.append(seprator);
                }
                buffer.append(tmp);
            }
        }
        return buffer.toString();
    }

    /**
     * 将数组合并为字符串
     *
     * @param array 数组
     * @return 合并后的字符串
     */
    public static String joinToString(Object[] array) {
        return joinToString(array, ",");
    }

    /**
     * 将数组合并为字符串
     *
     * @param array 数组
     * @param seprator 分隔符, 可为空
     * @return 合并后的字符串
     */
    public static String joinToString(Object[] array, String seprator) {

        StringBuilder buffer = new StringBuilder();
        if (VerifyTools.isNotBlank(array)) {
            for (Object tmp : array) {
                if (seprator != null && buffer.length() > 0) {
                    buffer.append(seprator);
                }
                buffer.append(tmp);
            }
        }
        return buffer.toString();
    }

    /**
     * 压缩列表, 保留指定数目
     *
     * @author zhaohuihua
     * @param list 列表
     * @param max 保留数目
     * @return 压缩后的列表
     */
    public static <T> List<T> compressList(List<T> list, int max) {
        int total = list.size();
        List<T> compressed = new ArrayList<>();
        if (total <= max) {
            compressed.addAll(list);
            // System.out.print(String.format("%32s", ""));
        } else {
            // 保留最后一个点(第一个点肯定是保留的, 最后一个点也强制保留)
            max--;
            total--;
            T last = list.get(total);

            // 如从500中取80个点, 去掉最后一个点, 还要取79个点
            // 隔几个点取一次, 6 = 499 / 79
            int interval = total / max; // 间隔
            // 总共取多少次, 84 = 499 / 6 + 1
            int times = total / interval + (total % interval == 0 ? 0 : 1);
            // 会多出多少个点, 5 = 84 - 79
            int diff = times - max;
            // 既然会多, 那么有一部分就不能按6个点取一次, 而要按7个点取一次
            // 那要取多少次才能减少掉5个点呢, 取最大公约数
            // 就是210 = 5 * 6 * 7
            int end = diff * interval * (interval + 1);
            for (int i = 0; i < total;) {
                compressed.add(list.get(i));
                i += interval;
                // 达到end之前多隔一个点取一个
                if (i < end) {
                    i++;
                }
            }
            compressed.add(last);
            // System.out.print(String.format("%8d%8d%8d%8d", space, times, diff, end));
        }
        return compressed;
    }

}
