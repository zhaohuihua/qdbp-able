package com.gitee.qdbp.tools.crypto;

import javax.crypto.SecretKey;
import com.gitee.qdbp.tools.codec.bytes.ByteCodec;
import com.gitee.qdbp.tools.codec.bytes.HexCodec;
import com.gitee.qdbp.tools.codec.bytes.TextCodec;
import com.gitee.qdbp.tools.utils.RandomTools;
import com.gitee.qdbp.tools.utils.VerifyTools;

/**
 * AES/ECB加解密实例
 *
 * @author zhaohuihua
 * @version 20200510
 */
public class AesEcbCipher implements CipherService {

    /** 输入输出文本的编解码方式 **/
    private TextCodec textCodec;
    /** 输入输出Byte的编解码方式 **/
    private ByteCodec byteCodec;
    /** 密码字符串 **/
    private String stringKey;
    /** 密钥对象 **/
    private SecretKey secretKey;
    /** 增加随机字符参与加密 **/
    private int randomChar = 5;

    public AesEcbCipher() {
        this(HexCodec.INSTANCE);
    }

    public AesEcbCipher(ByteCodec byteCodec) {
        this(RandomTools.generateString(20), byteCodec);
    }

    public AesEcbCipher(String secretKey) {
        this(secretKey, HexCodec.INSTANCE);
    }

    public AesEcbCipher(String secretKey, ByteCodec byteCodec) {
        this.textCodec = TextCodec.UTF8;
        this.byteCodec = byteCodec;
        this.stringKey = secretKey;
        this.secretKey = AesTools.generateSecretKey(textCodec.decode(secretKey), true);
    }

    public String getSecretKey() {
        return this.stringKey;
    }

    /**
     * 加密, 使用密钥加密
     * 
     * @param plaintext 待加密的明文
     * @return 加密后的密文
     */
    @Override
    public String encrypt(String plaintext) {
        VerifyTools.requireNonNull(plaintext, "plaintext");
        String text = RandomTools.generateString(randomChar) + plaintext + RandomTools.generateString(randomChar);
        byte[] input = textCodec.decode(text);
        byte[] output = AesTools.ecbEncrypt(input, this.secretKey);
        return byteCodec.encode(output);
    }

    /**
     * 加密, 使用密钥加密
     * 
     * @param plaintext 待加密的明文
     * @return 加密后的密文
     */
    @Override
    public byte[] encrypt(byte[] plaintext) {
        VerifyTools.requireNonNull(plaintext, "plaintext");
        return AesTools.ecbEncrypt(plaintext, this.secretKey);
    }

    /**
     * 解密, 使用密钥解密
     * 
     * @param ciphertext 待解密的密文
     * @return 解密后的明文
     */
    @Override
    public String decrypt(String ciphertext) {
        VerifyTools.requireNonNull(ciphertext, "ciphertext");
        byte[] input = byteCodec.decode(ciphertext);
        byte[] output = AesTools.ecbDecrypt(input, this.secretKey);
        String text = textCodec.encode(output);
        return text.substring(randomChar, text.length() - randomChar);
    }

    /**
     * 解密, 使用密钥解密
     * 
     * @param ciphertext 待解密的密文
     * @return 解密后的明文
     */
    @Override
    public byte[] decrypt(byte[] ciphertext) {
        VerifyTools.requireNonNull(ciphertext, "ciphertext");
        return AesTools.ecbDecrypt(ciphertext, this.secretKey);
    }
}
