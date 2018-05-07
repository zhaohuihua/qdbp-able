package com.gitee.zhaohuihua.tools.utils;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.zhaohuihua.core.beans.KeyString;
import ognl.Ognl;
import ognl.OgnlException;

/**
 * 格式转换工个
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
    public static <T, C extends T> T[] toArray(List<C> list, Class<T> clazz) {
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
    public static <T, C extends T> T[] toArrayIfNullToEmpty(List<C> list, Class<T> clazz) {
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
    @SuppressWarnings("unchecked")
    public static <T, C extends T> List<T> toList(C... array) {
        // JDK1.7必须强转, JDK1.8不需要
        return array == null ? null : (List<T>) new ArrayList<>(Arrays.asList(array));
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
     */
    public static int toInteger(String value) {
        return (int) toLong(value);
    }

    /**
     * 转换为数字
     *
     * @param value 源字符串, 支持乘法
     * @return 数字
     */
    public static long toLong(String value) throws NumberFormatException {
        if (VerifyTools.isBlank(value)) {
            return 0;
        }

        value = value.trim();

        if (!value.contains("*")) {
            return Long.parseLong(value);
        } else {
            Pattern ptn = Pattern.compile("\\*");
            String[] values = ptn.split(value);
            long number = 1;
            for (String string : values) {
                string = string.trim();
                try {
                    number *= Long.parseLong(string);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(value);
                }
            }
            return number;
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @return 解析结果
     * @throws NumberFormatException 数字格式错误
     */
    public static int parseIntegerExpression(String expression) throws NumberFormatException {
        if (expression == null) {
            throw new NumberFormatException("null");
        }

        expression = expression.trim();

        try {
            return Integer.parseInt(expression);
        } catch (NumberFormatException nfe) {
            Object result;
            try {
                result = Ognl.getValue(expression, null);
            } catch (OgnlException e) {
                throw new NumberFormatException(expression);
            }
            if (result instanceof Number) {
                return ((Number) result).intValue();
            } else {
                throw new NumberFormatException(expression);
            }
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @return 解析结果
     * @throws NumberFormatException 数字格式错误
     */
    public static long parseLongExpression(String expression) throws NumberFormatException {
        if (expression == null) {
            throw new NumberFormatException("null");
        }

        expression = expression.trim();

        try {
            return Long.parseLong(expression);
        } catch (NumberFormatException nfe) {
            Object result;
            try {
                result = Ognl.getValue(expression, null);
            } catch (OgnlException e) {
                throw new NumberFormatException(expression);
            }
            if (result instanceof Number) {
                return ((Number) result).longValue();
            } else {
                throw new NumberFormatException(expression);
            }
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @return 解析结果
     * @throws NumberFormatException 数字格式错误
     */
    public static float parseFloatExpression(String expression) throws NumberFormatException {
        if (expression == null) {
            throw new NumberFormatException("null");
        }

        expression = expression.trim();

        try {
            return Float.parseFloat(expression);
        } catch (NumberFormatException nfe) {
            Object result;
            try {
                result = Ognl.getValue(expression, null);
            } catch (OgnlException e) {
                throw new NumberFormatException(expression);
            }
            if (result instanceof Number) {
                return ((Number) result).floatValue();
            } else {
                throw new NumberFormatException(expression);
            }
        }
    }

    /**
     * 解析数字表达式
     * 
     * @param expression 数学表达式, 支持数学运算符
     * @return 解析结果
     * @throws NumberFormatException 数字格式错误
     */
    public static double parseDoubleExpression(String expression) throws NumberFormatException {
        if (expression == null) {
            throw new NumberFormatException("null");
        }

        expression = expression.trim();

        try {
            return Double.parseDouble(expression);
        } catch (NumberFormatException nfe) {
            Object result;
            try {
                result = Ognl.getValue(expression, null);
            } catch (OgnlException e) {
                throw new NumberFormatException(expression);
            }
            if (result instanceof Number) {
                return ((Number) result).doubleValue();
            } else {
                throw new NumberFormatException(expression);
            }
        }
    }

    /**
     * 解析Boolean表达式
     * 
     * @param expression 表达式, 支持运算符
     * @return 解析结果
     * @throws IllegalArgumentException 表达式格式错误
     */
    public static boolean parseBooleanExpression(String expression) throws IllegalArgumentException {
        if (expression == null) {
            throw new IllegalArgumentException("null");
        }

        expression = expression.trim();

        if (StringTools.isPositive(expression, false)) {
            return true;
        } else if (StringTools.isNegative(expression, false)) {
            return false;
        } else {
            Object result;
            try {
                result = Ognl.getValue(expression, null);
            } catch (OgnlException e) {
                throw new IllegalArgumentException(expression);
            }
            if (result instanceof Boolean) {
                return (Boolean) result;
            } else if (result instanceof Number) {
                return ((Number) result).doubleValue() != 0;
            } else {
                throw new IllegalArgumentException(expression);
            }
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
     * @param seprator 分隔符, 可为空
     * @return 合并后的字符串
     */
    public static String joinToString(List<?> list, String seprator) {

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

    /**
     * 将字符串转换为KeyString对象<br>
     * toKeyString("{'key':1,'value':'冷水'}")<br>
     * 或: toKeyString("{'1':'冷水'}")<br>
     *
     * @param text 字符串
     * @return KeyString对象
     */
    public static KeyString toKeyString(String text) {
        if (VerifyTools.isBlank(text)) {
            return null;
        }
        JSONObject json = JSON.parseObject(text);
        return toKeyString(json);
    }

    /**
     * 将字符串转换为KeyString对象数组<br>
     * listOf("[{'key':1,'value':'冷水'},{'key':2,'value':'热水'}]")<br>
     * 或: listOf("{'1':'冷水','2':'热水','3':'直饮水'}")<br>
     * --&gt; List&lt;KeyString&gt;
     *
     * @param text 字符串
     * @return List&lt;KeyString&gt; 对象数组
     */
    public static List<KeyString> toKeyStrings(String text) {
        if (VerifyTools.isBlank(text)) {
            return null;
        }
        List<KeyString> list = new ArrayList<>();

        Object object = JSON.parse(text);
        if (object instanceof JSONArray) {
            JSONArray array = (JSONArray) object;
            for (Object i : array) {
                list.add(toKeyString((JSONObject) i));
            }
        } else if (object instanceof JSONObject) {
            JSONObject json = (JSONObject) object;
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String string = TypeUtils.castToJavaBean(value, String.class);
                list.add(new KeyString(key, string));
            }
        }

        Collections.sort(list);
        return list;

    }

    private static KeyString toKeyString(JSONObject json) {
        if (json.containsKey("key")) {
            return JSON.toJavaObject(json, KeyString.class);
        } else {
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String string = TypeUtils.castToJavaBean(value, String.class);
                return new KeyString(key, string);
            }
            throw new IllegalArgumentException("json is empty.");
        }
    }
}
