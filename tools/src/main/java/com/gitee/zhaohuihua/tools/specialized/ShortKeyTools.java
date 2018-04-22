package com.gitee.zhaohuihua.tools.specialized;

import com.gitee.zhaohuihua.tools.codec.DigestTools;

/**
 * 短链接生成器<br>
 * https://www.cnblogs.com/zdz8207/p/java-shorturl-md5.html
 *
 * @author zhaohuihua
 * @version 180110
 */
public class ShortKeyTools {

    private static final String DEFAULT_SOURCE = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final char[] DEFAULT_CHARS = DEFAULT_SOURCE.toCharArray();

    private static final String SALT_PREFIX = "{<(";
    private static final String SALT_SUFFIX = ")>}";

    /**
     * 生成短链接
     * 
     * @param string 字符串
     * @return 短链接
     */
    public static String generate(String string) {
        String md5Hex = DigestTools.md5(SALT_PREFIX + string + SALT_SUFFIX);
        int i = string.length() % 4; // 四个短链接, 随便取一个
        return truncat(md5Hex, i);
    }

    /**
     * 生成短链接
     * 
     * @param string 字符串
     * @return 四个短链接数组, 取任意一个即可
     */
    public static String[] generates(String string) {

        // 对传入字符串取MD5摘要
        String md5Hex = DigestTools.md5(SALT_PREFIX + string + SALT_SUFFIX);
        String[] result = new String[4];

        // 生成4个短地址, 8位一组
        for (int i = 0; i < 4; i++) {
            // 把字符串存入对应索引的输出数组
            result[i] = truncat(md5Hex, i);
        }

        return result;
    }

    private static String truncat(String md5Hex, int i) {

        // 把加密字符按照8位一组16进制与0x3FFFFFFF进行位与运算
        String subString = md5Hex.substring(i * 8, i * 8 + 8);

        // 这里需要使用 long 型来转换
        // Inteper.parseInt()只能处理31位,首位为符号位, 如果不用long则会越界
        long number = 0x3FFFFFFF & Long.parseLong(subString, 16);

        StringBuilder buffer = new StringBuilder();
        // 生成6次-6位短地址
        for (int j = 0; j < 6; j++) {
            // 把得到的值与0x0000003D进行位与运算, 取得字符数组chars索引
            long index = 0x0000003D & number;

            // 把取得的字符相加
            buffer.append(DEFAULT_CHARS[(int) index]);

            // 每次循环按位右移 5 位
            number = number >> 5;
        }
        return buffer.toString();
    }
}
