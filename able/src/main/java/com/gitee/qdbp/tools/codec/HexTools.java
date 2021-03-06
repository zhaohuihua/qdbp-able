package com.gitee.qdbp.tools.codec;

import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * 十六进制工具
 *
 * @author zhaohuihua
 * @version 151107
 */
public abstract class HexTools {

    /**
     * 16进制字符串转换为byte数组<br>
     * "ABCDEF" --&gt; byte[(byte)0xAB, (byte)0xCD, (byte)0xEF]<br>
     * "AB CD EF" --&gt; byte[(byte)0xAB, (byte)0xCD, (byte)0xEF]<br>
     * "123456EF" --&gt; byte[(byte)0x12, (byte)0x34, (byte)0x56, (byte)0xEF]<br>
     * "12 34 56 EF" --&gt; byte[(byte)0x12, (byte)0x34, (byte)0x56, (byte)0xEF]<br>
     * "0506070F" --&gt; byte[(byte)0x5, (byte)0x6, (byte)0x7, (byte)0xF]<br>
     * "05 06 07 0F" --&gt; byte[(byte)0x5, (byte)0x6, (byte)0x7, (byte)0xF]<br>
     *
     * @param hex 16进制字符串
     * @return byte数组
     */
    public static byte[] toBytes(String hex) {
        if (VerifyTools.isBlank(hex)) {
            return null;
        }
        String string = hex.replaceAll(" ", "").toUpperCase();
        if (string.startsWith("0X")) {
            string = string.substring(2);
        }
        if (string.length() % 2 != 0) {
            string = "0" + string;
        }
        int length = string.length() / 2;
        char[] chars = string.toCharArray();
        byte[] bytes = new byte[length];
        for (int i = 0, p = 0; i < chars.length && p < length;) {
            int high = charToByte(chars[i++]);
            int low = charToByte(chars[i++]);
            bytes[p++] = (byte) (high << 4 | low);
        }
        return bytes;
    }

    /**
     * byte[] 转换为字符串<br>
     * byte[(byte)0xAB, (byte)0xCD, (byte)0xEF] --&gt; "ABCDEF"<br>
     * byte[(byte)0x12, (byte)0x34, (byte)0x56, (byte)0xEF] --&gt; "123456EF"<br>
     * byte[(byte)0x5, (byte)0x6, (byte)0x7, (byte)0xF] --&gt; "0506070F"
     *
     * @param bytes byte数组
     * @return 16进制字符串
     */
    public static String toString(byte[] bytes) {
        return toString(bytes, false);
    }

    /**
     * byte数组转换为日志字符串<br>
     * byte[(byte)0xAB, (byte)0xCD, (byte)0xEF] --&gt; "AB CD EF"<br>
     * byte[(byte)0x12, (byte)0x34, (byte)0x56, (byte)0xEF] --&gt; "12 34 56 EF"<br>
     * byte[(byte)0x5, (byte)0x6, (byte)0x7, (byte)0xF] --&gt; "05 06 07 0F"
     *
     * @param bytes byte数组
     * @return 日志字符串
     */
    public static String toLogString(byte[] bytes) {
        return toString(bytes, true);
    }

    /**
     * byte数字转换为日志字符串<br>
     * toLogString(byte) = toLogString(byte[]) = toByteString(byte)<br>
     *
     * @param number byte数字
     * @return 日志字符串
     */
    public static String toLogString(byte number) {
        return toByteString(number);
    }

    private static String toString(byte[] bytes, boolean split) {
        if (VerifyTools.isBlank(bytes)) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        for (byte i : bytes) {
            if (split && buffer.length() > 0) {
                buffer.append(' ');
            }
            buffer.append(byteToChar((i & 0xF0) >> 4)).append(byteToChar(i & 0x0F));
        }
        return buffer.toString();
    }

    /**
     * byte数字转换为16进制字符串<br>
     * 0x08 --&gt; "08"<br>
     * 0xAB --&gt; "AB"<br>
     *
     * @param number byte数字
     * @return 16进制字符串
     */
    public static String toByteString(long number) {
        StringBuilder buffer = new StringBuilder();
        byte n = (byte) number;
        buffer.append(byteToChar((n & 0xF0) >> 4)).append(byteToChar(n & 0x0F));
        return buffer.toString();
    }

    /**
     * short数字转换为16进制字符串<br>
     * 0x0008 --&gt; "0008"<br>
     * 0xABCD --&gt; "ABCD"<br>
     *
     * @param number short数字
     * @return 16进制字符串
     */
    public static String toShortString(long number) {
        short b = (short) number;
        byte H8 = (byte) ((b & 0xFF00) >> 8);
        byte L8 = (byte) (b & 0x00FF);
        StringBuilder buffer = new StringBuilder();
        buffer.append(toByteString(H8)).append(toByteString(L8));
        return buffer.toString();
    }

    /**
     * int数字转换为16进制字符串<br>
     * 0x00000008 --&gt; "00000008"<br>
     * 0xABCDEFEF --&gt; "ABCDEFEF"<br>
     *
     * @param number int数字
     * @return 16进制字符串
     */
    public static String toIntString(long number) {
        int b = (int) number;
        short H16 = (short) ((b & 0xFFFF0000) >> 16);
        short L16 = (short) (b & 0x0000FFFF);
        StringBuilder buffer = new StringBuilder();
        buffer.append(toShortString(H16)).append(toShortString(L16));
        return buffer.toString();
    }

    /**
     * 数字转换为16进制字符串<br>
     * 8 --&gt; "08"<br>
     * 300 = 0x012C --&gt; "012C"<br>
     *
     * @param number 数字
     * @return 16进制字符串
     */
    public static String toString(long number) {
        if (number > 0 && number <= 0xFF) {
            return toByteString(number);
        } else if (number > 0 && number <= 0xFFFF) {
            return toShortString(number);
        } else if (number < 0 && number >= Integer.MIN_VALUE) {
            return toIntString(number);
        } else {
            String hex = Long.toHexString(number).toUpperCase();
            return hex.length() % 2 == 0 ? hex : ("0" + hex);
        }
    }

    /**
     * byte数组转换为 数字<br>
     * byte[(byte)0xAB, (byte)0xCD, (byte)0xEF] --&gt; 0xABCDEF<br>
     * byte[(byte)0x12, (byte)0x34, (byte)0x56, (byte)0xEF] --&gt; 0x123456EF<br>
     * byte[(byte)0x5, (byte)0x6, (byte)0x7, (byte)0xF] --&gt; 0x0506070F
     *
     * @param bytes byte数组
     * @return 长整形数字
     */
    public static long toLong(byte... bytes) {
        long number = 0;
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            number += (bytes[i] & 0xFF) << (length - i - 1) * 8;
        }
        return number;
    }

    /**
     * byte数组转换为 数字<br>
     * byte[(byte)0xAB, (byte)0xCD, (byte)0xEF] --&gt; 0xABCDEF<br>
     * byte[(byte)0x12, (byte)0x34, (byte)0x56, (byte)0xEF] --&gt; 0x123456EF<br>
     * byte[(byte)0x5, (byte)0x6, (byte)0x7, (byte)0xF] --&gt; 0x0506070F
     *
     * @param bytes byte数组
     * @return Int数字
     */
    public static int toInteger(byte... bytes) {
        return (int) toLong(bytes);
    }

    /** 比较两段byte数组是否相同 **/
    public static boolean equals(byte[] source, byte[] target) {
        if (source.length != target.length) {
            return false;
        }
        for (int i = 0; i < source.length; i++) {
            if (source[i] != target[i]) {
                return false;
            }
        }
        return true;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static char byteToChar(int b) {
        return "0123456789ABCDEF".charAt(b);
    }
}
