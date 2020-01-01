package com.gitee.qdbp.tools.crypto;

import java.security.KeyPair;
import com.gitee.qdbp.tools.codec.bytes.ByteCodec;
import com.gitee.qdbp.tools.codec.bytes.HexCodec;
import com.gitee.qdbp.tools.codec.bytes.TextCodec;

/**
 * RSA加解密实例
 *
 * @author zhaohuihua
 * @version 191228
 */
public class RsaCipher {

    /** 输入输出文本的编解码方式 **/
    private TextCodec textCodec;
    /** 输入输出Byte的编解码方式 **/
    private ByteCodec byteCodec;
    /** 私钥 **/
    private byte[] privateKey;
    /** 公钥 **/
    private byte[] publicKey;

    public RsaCipher(int keySize) {
        this(keySize, HexCodec.INSTANCE);
    }

    public RsaCipher(int keySize, ByteCodec byteCodec) {
        this.textCodec = TextCodec.UTF8;
        this.byteCodec = byteCodec;
        KeyPair keyPair = RsaTools.generateKeyPair(keySize);
        this.privateKey = keyPair.getPrivate().getEncoded();
        this.publicKey = keyPair.getPublic().getEncoded();
    }

    /** 公钥 **/
    public String getPublicKey() {
        return byteCodec.encode(publicKey);
    }

    /**
     * 加密, 使用公钥加密
     * 
     * @param plaintext 待加密的明文
     * @return 加密后的密文
     */
    public String encrypt(String plaintext) {
        byte[] input = textCodec.decode(plaintext);
        byte[] output = RsaTools.encrypt(input, publicKey);
        return byteCodec.encode(output);
    }

    /**
     * 解密, 使用私钥解密
     * 
     * @param ciphertext 待解密的密文
     * @return 解密后的明文
     */
    public String decrypt(String ciphertext) {
        byte[] input = byteCodec.decode(ciphertext);
        byte[] output = RsaTools.decrypt(input, privateKey);
        return textCodec.encode(output);
    }
}
