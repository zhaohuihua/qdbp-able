package com.gitee.qdbp.tools.crypto;

/**
 * 密码加密解密服务接口
 *
 * @author zhaohuihua
 * @version 20200419
 */
public interface CipherService {

    /**
     * 加密
     * 
     * @param plaintext 待加密的明文
     * @return 加密后的密文
     */
    String encrypt(String plaintext);

    /**
     * 加密
     * 
     * @param plaintext 待加密的明文
     * @return 加密后的密文
     */
    byte[] encrypt(byte[] plaintext);

    /**
     * 解密
     * 
     * @param ciphertext 待解密的密文
     * @return 解密后的明文
     */
    String decrypt(String ciphertext);

    /**
     * 解密
     * 
     * @param ciphertext 待解密的密文
     * @return 解密后的明文
     */
    byte[] decrypt(byte[] ciphertext);
}
