package com.gitee.zhaohuihua.tools.codec;

import com.gitee.zhaohuihua.tools.utils.VerifyTools;

/**
 * BCD编码工具<br>
 * BCD码只能用来传输数字<br>
 * 典型的8421码(BigEndian)用法: <br>
 * toBytesOfBigEndian("123456") --> byte[(byte)0x12, (byte)0x34, (byte)0x56]<br>
 * toStringOfBigEndian(byte[(byte)0x12, (byte)0x34, (byte)0x56]) --> "123456"<br>
 * toLongOfBigEndian(byte[(byte)0x12, (byte)0x34, (byte)0x56]) --> 123456<br>
 * <br>
 * BigEndian: 0x4200 --> 4200, 按高位优先的顺序存储字(最低位字节存储在最高地址)<br>
 * LittleEndian: 0x4200 --> 0042, 按低位优先的顺序存储字(最低位字节存储在最低地址)<br>
 *
 * @author zhaohuihua
 * @version 151107
 */
public abstract class BcdTools {

    /**
     * byte数组按BCD格式转换为数字<br>
     * byte[(byte)0x12, (byte)0x34, (byte)0x56] --> 123456<br>
     * byte[0, 0, (byte)0x08, (byte)0x34, (byte)0x56] --> 83456<br>
     *
     * @param bytes
     * @return
     */
    public static long toLongOfBigEndian(byte... bytes) {
        long number = 0;
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            int high = (bytes[i] & 0xF0) >> 4;
            int low = bytes[i] & 0x0F;
            number += (high * 10 + low) * Math.pow(10, (length - i - 1) * 2);
        }
        return number;
    }

    /**
     * byte数组按BCD格式转换为数字<br>
     * byte[(byte)0x12, (byte)0x34, (byte)0x56] --> 123456<br>
     * byte[0, 0, (byte)0x08, (byte)0x34, (byte)0x56] --> 83456<br>
     *
     * @param bytes
     * @return
     */
    public static int toIntegerOfBigEndian(byte... bytes) {
        return (int) toLongOfBigEndian(bytes);
    }

    /**
     * byte数组转换为BCD字符串(前置的0将被清除)<br>
     * byte[(byte)0x12, (byte)0x34, (byte)0x56] --> "123456"<br>
     * byte[0, 0, (byte)0x08, (byte)0x34, (byte)0x56] --> "083456"<br>
     *
     * @param bytes
     * @return
     */
    public static String toStringOfBigEndian(byte... bytes) {
        return toStringOfBigEndian(true, bytes);
    }

    /**
     * byte数组转换为BCD格式字符串<br>
     * byte[(byte)0x12, (byte)0x34, (byte)0x56] --> "123456"<br>
     * byte[0, 0, (byte)0x08, (byte)0x34, (byte)0x56]<br>
     * clearUselessZero=false --> "0000083456"<br>
     * clearUselessZero=true --> "083456"
     *
     * @param bytes
     * @param clearUselessZero 是否清除前置的0
     * @return
     */
    public static String toStringOfBigEndian(boolean clearUselessZero, byte[] bytes) {
        if (VerifyTools.isBlank(bytes)) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        for (byte i : bytes) {

            if (clearUselessZero && buffer.length() == 0 && i == 0) {
                continue; // 高低位都是0
            }

            int high = (i & 0xF0) >> 4;
            int low = i & 0x0F;

            buffer.append(byteToChar(high)).append(byteToChar(low));
        }
        return buffer.length() == 0 ? "0" : buffer.toString();
    }

    /**
     * 数字型字形串按BCD编码转换为byte数组<br>
     * "123456" --> byte[(byte)0x12, (byte)0x34, (byte)0x56]<br>
     *
     * @param bytes
     * @return
     */
    public static byte[] toBytesOfBigEndian(String number) {
        return toBytesOfBigEndian(number, 0);
    }

    /**
     * 数字型字形串按BCD编码转换为byte数组<br>
     * ("123456", 0) --> byte[(byte)0x12, (byte)0x34, (byte)0x56]<br>
     * ("123456", 5) --> byte[0, 0, (byte)0x12, (byte)0x34, (byte)0x56]<br>
     * ("12345678", 3) --> byte[(byte)0x12, (byte)0x34, (byte)0x56]<br>
     *
     * @param number 数字型字形串
     * @param length 填充为多少byte位, 不足该长度时低位补0, 超过该长度时忽略超长部分
     * @return
     */
    public static byte[] toBytesOfBigEndian(String number, int length) {
        if (VerifyTools.isBlank(number)) {
            return null;
        }
        String string = number.toUpperCase().replaceAll(" ", "");
        if (string.length() % 2 != 0) {
            string = '0' + string;
        }
        int real = string.length() / 2; // 实际长度
        if (length <= 0) {
            length = real;
        }
        int offset = Math.max(0, length - real);
        byte[] bytes = new byte[length];
        char[] chars = string.toCharArray();
        for (int i = 0, p = offset; i < chars.length && p < length;) {
            int high = charToByte(chars[i++]);
            int low = charToByte(chars[i++]);
            bytes[p++] = (byte) (high << 4 | low);
        }
        return bytes;
    }

    /**
     * byte数组按BCD格式转换为数字<br>
     * byte[(byte)0x56, (byte)0x34, (byte)0x12] --> 123456<br>
     * byte[(byte)0x56, (byte)0x34, (byte)0x08, 0, 0] --> 83456<br>
     *
     * @param bytes
     * @return
     */
    public static long toLongOfLittleEndian(byte... bytes) {
        return toLongOfBigEndian(reverse(bytes));
    }

    /**
     * byte数组按BCD格式转换为数字<br>
     * byte[(byte)0x56, (byte)0x34, (byte)0x12] --> 123456<br>
     * byte[(byte)0x56, (byte)0x34, (byte)0x08, 0, 0] --> 83456<br>
     *
     * @param bytes
     * @return
     */
    public static int toIntegerOfLittleEndian(byte... bytes) {
        return (int) toLongOfLittleEndian(bytes);
    }

    /**
     * byte数组转换为BCD字符串(前置的0将被清除)<br>
     * byte[(byte)0x56, (byte)0x34, (byte)0x12] --> "123456"<br>
     * byte[(byte)0x56, (byte)0x34, (byte)0x08, 0, 0] --> "083456"<br>
     *
     * @param bytes
     * @return
     */
    public static String toStringOfLittleEndian(byte... bytes) {
        return toStringOfLittleEndian(true, bytes);
    }

    /**
     * byte数组转换为BCD格式字符串<br>
     * byte[(byte)0x56, (byte)0x34, (byte)0x12] --> "123456"<br>
     * byte[(byte)0x56, (byte)0x34, (byte)0x08, 0, 0]<br>
     * clearUselessZero=false --> "0000083456"<br>
     * clearUselessZero=true --> "083456"<br>
     *
     * @param bytes
     * @param clearUselessZero 是否清除前置的0
     * @return
     */
    public static String toStringOfLittleEndian(boolean clearUselessZero, byte[] bytes) {
        if (VerifyTools.isBlank(bytes)) {
            return null;
        }
        return toStringOfBigEndian(clearUselessZero, reverse(bytes));
    }

    /**
     * 数字型字形串按BCD编码转换为byte数组<br>
     * "123456" --> byte[(byte)0x56, (byte)0x34, (byte)0x12]<br>
     *
     * @param bytes
     * @return
     */
    public static byte[] toBytesOfLittleEndian(String number) {
        return toBytesOfLittleEndian(number, 0);
    }

    /**
     * 数字型字形串按BCD编码转换为byte数组<br>
     * ("123456", 0) --> byte[(byte)0x56, (byte)0x34, (byte)0x12]<br>
     * ("123456", 5) --> byte[(byte)0x56, (byte)0x34, (byte)0x12, 0, 0]<br>
     * ("12345678", 3) --> byte[(byte)0x56, (byte)0x34, (byte)0x12]<br>
     *
     * @param number 数字型字形串
     * @param length 填充为多少byte位, 不足该长度时高位补0, 超过该长度时忽略超长部分
     * @return
     */
    public static byte[] toBytesOfLittleEndian(String number, int length) {
        if (VerifyTools.isBlank(number)) {
            return null;
        }
        String string = number.toUpperCase().replaceAll(" ", "");
        if (string.length() % 2 != 0) {
            string = '0' + string;
        }
        int real = string.length() / 2; // 实际长度
        if (length <= 0) {
            length = real;
        }
        int offset = Math.max(0, real - length);
        byte[] bytes = new byte[length];
        char[] chars = string.toCharArray();
        for (int i = 0, p = real - offset - 1; i < chars.length && p >= 0;) {
            int high = charToByte(chars[i++]);
            int low = charToByte(chars[i++]);
            bytes[p--] = (byte) (high << 4 | low);
        }
        return bytes;
    }

    public static byte[] fromOfBigEndian(byte... bytes) {
        int size = bytes.length;
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            int b = bytes[i] & 0xFF;
            int high = b / 10;
            int low = b % 10;
            result[i] = (byte) (high << 4 | low);
        }
        return result;
    }

    private static byte[] reverse(byte[] bytes) {
        int length = bytes.length;
        byte[] array = new byte[length];
        for (int i = length; i > 0; i--) {
            array[length - i] = bytes[i - 1];
        }
        return array;
    }

    private static char byteToChar(int b) {
        return "0123456789ABCDEF".charAt(b);
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
