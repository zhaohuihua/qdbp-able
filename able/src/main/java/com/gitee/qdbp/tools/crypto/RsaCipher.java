package com.gitee.qdbp.tools.crypto;

import java.security.KeyPair;
import com.gitee.qdbp.tools.codec.bytes.ByteCodec;
import com.gitee.qdbp.tools.codec.bytes.HexCodec;
import com.gitee.qdbp.tools.codec.bytes.TextCodec;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * RSA加解密实例
 *
 * @author zhaohuihua
 * @version 191228
 */
public class RsaCipher implements CipherService {

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

    public RsaCipher(String publicKey, String privateKey) {
        this(publicKey, privateKey, HexCodec.INSTANCE);
    }

    public RsaCipher(String publicKey, String privateKey, ByteCodec byteCodec) {
        this.textCodec = TextCodec.UTF8;
        this.byteCodec = byteCodec;
        if (publicKey != null) {
            this.publicKey = byteCodec.decode(publicKey);
        }
        if (privateKey != null) {
            this.privateKey = byteCodec.decode(privateKey);
        }
    }

    public RsaCipher(byte[] publicKey, byte[] privateKey) {
        this(publicKey, privateKey, HexCodec.INSTANCE);
    }

    public RsaCipher(byte[] publicKey, byte[] privateKey, ByteCodec byteCodec) {
        this.textCodec = TextCodec.UTF8;
        this.byteCodec = byteCodec;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /** 公钥 **/
    public String getPublicKey() {
        return publicKey == null ? null : byteCodec.encode(publicKey);
    }

    /**
     * 加密, 使用公钥加密
     * 
     * @param plaintext 待加密的明文
     * @return 加密后的密文
     */
    @Override
    public String encrypt(String plaintext) {
        VerifyTools.requireNonNull(plaintext, "plaintext");
        if (publicKey == null) {
            throw new IllegalStateException("PublicKey not configured");
        }
        byte[] input = textCodec.decode(plaintext);
        byte[] output = RsaTools.encrypt(input, publicKey);
        return byteCodec.encode(output);
    }

    /**
     * 加密, 使用公钥加密
     * 
     * @param plaintext 待加密的明文
     * @return 加密后的密文
     */
    @Override
    public byte[] encrypt(byte[] plaintext) {
        VerifyTools.requireNonNull(plaintext, "plaintext");
        if (publicKey == null) {
            throw new IllegalStateException("PublicKey not configured");
        }
        return RsaTools.encrypt(plaintext, publicKey);
    }

    /**
     * 解密, 使用私钥解密
     * 
     * @param ciphertext 待解密的密文
     * @return 解密后的明文
     */
    @Override
    public String decrypt(String ciphertext) {
        VerifyTools.requireNonNull(ciphertext, "ciphertext");
        if (privateKey == null) {
            throw new IllegalStateException("PrivateKey not configured");
        }
        byte[] input = byteCodec.decode(ciphertext);
        byte[] output = RsaTools.decrypt(input, privateKey);
        return textCodec.encode(output);
    }

    /**
     * 解密, 使用私钥解密
     * 
     * @param ciphertext 待解密的密文
     * @return 解密后的明文
     */
    @Override
    public byte[] decrypt(byte[] ciphertext) {
        VerifyTools.requireNonNull(ciphertext, "ciphertext");
        if (privateKey == null) {
            throw new IllegalStateException("PrivateKey not configured");
        }
        return RsaTools.decrypt(ciphertext, privateKey);
    }
}
