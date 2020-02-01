package com.gitee.qdbp.tools.crypto;

import com.gitee.qdbp.tools.codec.bytes.Base58Codec;

/**
 * RSA实例池
 *
 * @author zhaohuihua
 * @version 191228
 */
public class RsaPool {

    private static final int KEY_SIZE_DEFAULT = 1024;
    public static final RsaPool instance = new RsaPool();
    /** 主实例 **/
    private RsaCipher majorCipher = new RsaCipher(KEY_SIZE_DEFAULT, new Base58Codec());
    /** 备用实例, 更换主实例后, 原先的主实例降为备用实例继续服务一段时间 **/
    private RsaCipher minorCipher;

    /** 获取当前公钥 **/
    public String getPublicKey() {
        return majorCipher.getPublicKey();
    }

    /** 使用私钥解密 **/
    public String decrypt(String ciphertext) {
        try {
            return majorCipher.decrypt(ciphertext);
        } catch (Exception e) {
            if (minorCipher != null) {
                return minorCipher.decrypt(ciphertext);
            }
            throw e;
        }
    }

    // 定期更换RsaCipher
}