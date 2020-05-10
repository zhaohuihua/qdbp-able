package com.gitee.qdbp.tools.utils;

import java.util.Random;
import java.util.UUID;

/**
 * Random string generator
 *
 * @author 王波
 * @version 140823
 */
public final class RandomTools {

    /** Default source **/
    private static final String DEFAULT_SOURCE = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** Alphabet source **/
    private static final String ALPHABET_SOURCE = "abcdefghijklmnopqrstuvwxyz";

    /** Number source **/
    private static final String NUMBER_SOURCE = "0123456789";

    /** 可读性强的字符串(没有EIOZ) **/
    private static final String READABLE = "0123456789ABCDFGHJKLMNPQRSTUVWXY";
    
    /** 16进制源字符串 **/
    private static final String HEX_SOURCE = "0123456789ABCDEF";

    private static final Random RANDOM = new Random();

    /** 构造函数 **/
    private RandomTools() {
    }

    /**
     * Randomly generated 'codeCount' string
     *
     * @param baseString baseString
     * @param codeCount codeCount
     * @return string
     */
    public static String generateString(String baseString, int codeCount) {
        if (null == baseString) {
            return null;
        }

        int baseLength = baseString.length();

        StringBuilder result = new StringBuilder();

        char[] chars = baseString.toCharArray();

        for (int i = 0; i < codeCount; i++) {
            result.append(chars[RANDOM.nextInt(baseLength)]);
        }

        return result.toString();
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @author zhaohuihua
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateString(final int length) {
        return generateString(DEFAULT_SOURCE, length);
    }

    /**
     * 生成指定长度的随机字母字符串
     *
     * @author zhaohuihua
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateAlphabet(final int length) {
        return generateString(ALPHABET_SOURCE, length);
    }

    /**
     * 生成指定长度的随机字符串(可读性强的)
     *
     * @author zhaohuihua
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateReadable(final int length) {
        return generateString(READABLE, length);
    }

    /**
     * 生成指定长度的随机数
     *
     * @author zhaohuihua
     * @param length 随机数长度
     * @return 随机数
     */
    public static String generateNumber(final int length) {
        return generateString(NUMBER_SOURCE, length);
    }

    /**
     * 生成指定长度的16进制随机数
     *
     * @author zhaohuihua
     * @param length 随机数长度
     * @return 随机数
     */
    public static String generateHexNumber(final int length) {
        return generateString(HEX_SOURCE, length);
    }

    /**
     * 生成指定范围的随机数
     *
     * @author zhaohuihua
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int generateNumber(final int min, final int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }

    /**
     * 生成指定范围的随机数
     *
     * @author zhaohuihua
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static long generateNumber(final long min, final long max) {
        return min + RANDOM.nextLong() % (max - min + 1);
    }

    /**
     * 生成随机序列号
     *
     * @return 序列号
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
