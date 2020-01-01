package com.gitee.qdbp.tools.codec.bytes;

import java.nio.charset.Charset;

/**
 * 包含汉字的文本的编码解码
 *
 * @author zhaohuihua
 * @version 190602
 */
public class TextCodec implements ByteCodec {

    /** UTF-8实例 **/
    public static final TextCodec UTF8 = new TextCodec("UTF-8");
    /** 编码格式 **/
    private Charset charset;

    public TextCodec(String charset) {
        this.charset = Charset.forName(charset);
    }

    /**
     * byte数组按UTF-8格式编码为字符串
     *
     * @param bytes byte数组
     * @return 字符串
     */
    @Override
    public String encode(byte[] bytes) {
        return new String(bytes, charset);
    }

    /**
     * 字符串按UTF-8格式转换为byte数组
     *
     * @param string 字符串
     * @return byte数组
     */
    @Override
    public byte[] decode(String string) {
        return string.getBytes(charset);
    }

}
