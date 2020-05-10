package com.gitee.qdbp.tools.codec.bytes;

import com.gitee.qdbp.tools.codec.Base64Tools;

/**
 * Base64编码解码
 *
 * @author zhaohuihua
 * @version 190602
 */
public class Base64Codec implements ByteCodec {

    /** 默认实例 **/
    public static final Base64Codec INSTANCE = new Base64Codec();

    /**
     * byte[] 转换为Base64字符串
     *
     * @param bytes byte数组
     * @return Base64字符串
     */
    @Override
    public String encode(byte[] bytes) {
        return Base64Tools.encode(bytes);
    }

    /**
     * Base64字符串转换为byte数组
     *
     * @param string Base64字符串
     * @return byte数组
     */
    @Override
    public byte[] decode(String string) {
        return Base64Tools.decode(string);
    }

}
