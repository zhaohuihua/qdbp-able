package com.gitee.zhaohuihua.tools.codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.gitee.zhaohuihua.core.utils.StringTools;

/**
 * Bit工具类
 *
 * @author zhaohuihua
 * @version 170323
 */
public abstract class BitTools {

    /**
     * 按位解析字符串<br>
     * BitUtils.formString("11110000");<br>
     * BitUtils.formString("11111111");<br>
     *
     * @param string 二进制字符串
     * @return byte数字
     */
    public static byte formString(String string) {
        // Byte.parseByte("11111111", 2); // Value out of range
        return (byte) Integer.parseInt(string.replace(" ", ""), 2);
    }

    /**
     * 按位解析字符串<br>
     * BitUtils.formString("11110000", "00001111", "11111111");
     *
     * @param string 二进制字符串
     * @return byte[]
     */
    public static byte[] formString(String one, String two, String... more) {
        List<String> list = new ArrayList<>();
        list.add(one);
        list.add(two);
        if (more.length > 0) {
            Collections.addAll(list, more);
        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = formString(list.get(i));
        }
        return bytes;
    }

    /**
     * 按位转换为字符串<br>
     * BitUtils.toString(0xFF); = 11111111<br>
     * BitUtils.toString(0x0F); = 00001111<br>
     *
     * @param b byte数字
     * @return 二进制字符串
     */
    public static String toString(int b) {
        int unsigned = b & 0xFF;
        String string = Integer.toString(unsigned, 2);
        return StringTools.pad(string, '0', 8);
    }

    /**
     * 按位读取
     *
     * @param b byte数字
     * @param p 位置, 从左向右算, 从1开始, 左边第1位=1, 右边第1位=8
     * @return 二进制位:0/1
     */
    public static int get(byte b, int p) {
        if (p < 1 || p > 8) {
            throw new IllegalArgumentException("position out of range: p >= 1 && p <= 8");
        }
        return (b >> (8 - p) & 1);
    }

    /**
     * 按位判断状态
     *
     * @param b byte数字
     * @param p 位置(1~8), 从左向右算, 从1开始, 左边第1位=1, 右边第1位=8
     * @return 状态: 1=true, 0=false
     */
    public static boolean state(byte b, int p) {
        return get(b, p) == 1;
    }
}
