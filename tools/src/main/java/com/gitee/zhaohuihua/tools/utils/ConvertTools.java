package com.gitee.zhaohuihua.tools.utils;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.gitee.zhaohuihua.core.beans.KeyString;

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
        if (StringTools.isDigit(value)) {
            return Long.valueOf(value);
        }

        if (value.contains("*")) {
            Pattern ptn = Pattern.compile("\\*");
            String[] values = ptn.split(value);
            long number = 1;
            boolean success = true;
            for (String string : values) {
                string = string.trim();
                if (VerifyTools.isNotBlank(string) && StringTools.isDigit(string)) {
                    number *= Long.valueOf(string);
                } else {
                    success = false;
                }
            }
            if (success) {
                return number;
            }
        }

        throw new NumberFormatException(value);
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

    /**
     * 转换为Byte描述字符串
     *
     * @param size B
     * @return B/KB/MB/GB/TB/PB
     */
    public static String toByteString(double size) {
        double kibibyte = 1024.0;
        double mebibyte = kibibyte * kibibyte;
        double gibibyte = mebibyte * kibibyte;
        double tebibyte = gibibyte * kibibyte;
        double pebibyte = tebibyte * kibibyte;
        double exbibyte = pebibyte * kibibyte;
        double zebibyte = exbibyte * kibibyte;
        double yobibyte = zebibyte * kibibyte;
        DecimalFormat df = new DecimalFormat("0.##");
        if (size < kibibyte) {
            return df.format(size) + "B";
        } else if (size < mebibyte) {
            return df.format(size / kibibyte) + "KB";
        } else if (size < gibibyte) {
            return df.format(size / mebibyte) + "MB";
        } else if (size < tebibyte) {
            return df.format(size / gibibyte) + "GB";
        } else if (size < pebibyte) {
            return df.format(size / tebibyte) + "TB";
        } else if (size < exbibyte) {
            return df.format(size / pebibyte) + "PB";
        } else if (size < zebibyte) {
            return df.format(size / exbibyte) + "EB";
        } else if (size < yobibyte) {
            return df.format(size / zebibyte) + "ZB";
        } else {
            return df.format(size / yobibyte) + "YB";
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
     * valueOf("{'key':1,'value':'冷水'}")<br>
     * 或: valueOf("{'1':'冷水'}")<br>
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
