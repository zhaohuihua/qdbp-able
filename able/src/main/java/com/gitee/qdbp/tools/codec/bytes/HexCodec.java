package com.gitee.qdbp.tools.codec.bytes;

import com.gitee.qdbp.tools.codec.HexTools;

/**
 * 十六进制编码解码
 *
 * @author zhaohuihua
 * @version 190602
 */
public class HexCodec implements ByteCodec {

    /** 默认实例 **/
    public static final HexCodec INSTANCE = new HexCodec();

    /**
     * byte[] 转换为字符串<br>
     * byte[(byte)0xAB, (byte)0xCD, (byte)0xEF] --&gt; "ABCDEF"<br>
     * byte[(byte)0x12, (byte)0x34, (byte)0x56, (byte)0xEF] --&gt; "123456EF"<br>
     * byte[(byte)0x5, (byte)0x6, (byte)0x7, (byte)0xF] --&gt; "0506070F"
     *
     * @param bytes byte数组
     * @return 16进制字符串
     */
    @Override
    public String encode(byte[] bytes) {
        return HexTools.toString(bytes);
    }

    /**
     * HEX字符串转换为byte数组<br>
     * "ABCDEF" --&gt; byte[(byte)0xAB, (byte)0xCD, (byte)0xEF]<br>
     *
     * @param hexString HEX字符串
     * @return byte数组
     */
    @Override
    public byte[] decode(String hexString) {
        return HexTools.toBytes(hexString);
    }

}
