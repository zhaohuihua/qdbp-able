package com.gitee.qdbp.tools.codec.bytes;

/**
 * Byte编码解码
 *
 * @author zhaohuihua
 * @version 190602
 */
public interface ByteCodec {

    /**
     * byte[]编码为字符串
     *
     * @param bytes byte数组
     * @return 字符串
     */
    String encode(byte[] bytes);

    /**
     * 字符串解码为byte数组
     *
     * @param string 字符串
     * @return byte数组
     */
    byte[] decode(String string);
}
