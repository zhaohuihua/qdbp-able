package com.gitee.qdbp.tools.codec.bytes;

import com.gitee.qdbp.tools.codec.Base58Tools;

/**
 * Base58编码解码
 *
 * @author zhaohuihua
 * @version 190602
 */
public class Base58Codec implements ByteCodec {

    /** 默认实例 **/
    public static final Base58Codec INSTANCE = new Base58Codec();

    /**
     * byte[] 转换为Base58字符串
     *
     * @param bytes byte数组
     * @return Base58字符串
     */
    @Override
    public String encode(byte[] bytes) {
        return Base58Tools.encode(bytes);
    }

    /**
     * Base58字符串转换为byte数组
     *
     * @param string Base58字符串
     * @return byte数组
     */
    @Override
    public byte[] decode(String string) {
        return Base58Tools.decode(string);
    }

}
