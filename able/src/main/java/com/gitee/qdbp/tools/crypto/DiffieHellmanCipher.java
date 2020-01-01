package com.gitee.qdbp.tools.crypto;

import java.security.KeyPair;
import com.gitee.qdbp.tools.codec.bytes.ByteCodec;
import com.gitee.qdbp.tools.codec.bytes.HexCodec;
import com.gitee.qdbp.tools.codec.bytes.TextCodec;

/**
 * Diffie-Hellman: 密钥一致性算法<br>
 * Whitfield Diffie与Martin Hellman在1976年提出了一个奇妙的密钥交换协议, <br>
 * 称为Diffie-Hellman密钥交换协议/算法(Diffie-Hellman Key Exchange/Agreement Algorithm)<br>
 * 简单来说就是允许双方用户通过公开途径交换信息以生成一致可共享的密钥<br>
 * 1. 双方约定一种对称加密算法<br>
 * 2. A生成一组密钥对, 保留私钥, 将公钥发送给B<br>
 * 3. B通过A的公钥生成密钥对, 保留私钥, 将公钥发送给A<br>
 * 4. 双方通过自己的私钥和对方的公钥生成对称加密的密钥, 进行数据的加密和解密<br>
 * 
 * @author zhaohuihua
 * @version 191228
 */
public class DiffieHellmanCipher {

    /** 对称加密算法 **/
    private String algorithm;
    /** 输入输出文本的编解码方式 **/
    private TextCodec textCodec;
    /** 输入输出Byte的编解码方式 **/
    private ByteCodec byteCodec;
    /** 私钥 **/
    private byte[] privateKey;
    /** 公钥 **/
    private byte[] publicKey;

    public DiffieHellmanCipher(int keySize, String algorithm) {
        this(keySize, algorithm, HexCodec.INSTANCE);
    }

    public DiffieHellmanCipher(int keySize, String algorithm, ByteCodec byteCodec) {
        this.algorithm = algorithm;
        this.textCodec = TextCodec.UTF8;
        this.byteCodec = byteCodec;
        KeyPair keyPair = DiffieHellmanTools.generateKeyPair(keySize);
        this.privateKey = keyPair.getPrivate().getEncoded();
        this.publicKey = keyPair.getPublic().getEncoded();
    }

    public DiffieHellmanCipher(String otherPublicKey, String algorithm) {
        this(otherPublicKey, algorithm, HexCodec.INSTANCE);
    }

    public DiffieHellmanCipher(String otherPublicKey, String algorithm, ByteCodec byteCodec) {
        this.algorithm = algorithm;
        this.textCodec = TextCodec.UTF8;
        this.byteCodec = byteCodec;
        byte[] otherPublicKeys = byteCodec.decode(otherPublicKey);
        KeyPair keyPair = DiffieHellmanTools.generateKeyPairByPublicKey(otherPublicKeys);
        this.privateKey = keyPair.getPrivate().getEncoded();
        this.publicKey = keyPair.getPublic().getEncoded();
    }

    /** 对称加密算法 **/
    public String getAlgorithm() {
        return algorithm;
    }

    /** 公钥 **/
    public String getPublicKey() {
        return byteCodec.encode(publicKey);
    }

    /**
     * 加密, 使用自己的私钥和对方的公钥生成对称加密的密钥进行加密
     * 
     * @param plaintext 待加密的明文
     * @param otherPublicKey 对方的公钥
     * @return 加密后的密文
     */
    public String encrypt(String plaintext, String otherPublicKey) {
        byte[] input = textCodec.decode(plaintext);
        byte[] otherPublicKeys = byteCodec.decode(otherPublicKey);
        byte[] output = DiffieHellmanTools.encrypt(input, privateKey, otherPublicKeys, algorithm);
        return byteCodec.encode(output);
    }

    /**
     * 解密, 使用自己的私钥和对方的公钥生成对称加密的密钥进行解密
     * 
     * @param ciphertext 待解密的密文
     * @param otherPublicKey 对方的公钥
     * @return 解密后的明文
     */
    public String decrypt(String ciphertext, String otherPublicKey) {
        byte[] input = byteCodec.decode(ciphertext);
        byte[] otherPublicKeys = byteCodec.decode(otherPublicKey);
        byte[] output = DiffieHellmanTools.decrypt(input, privateKey, otherPublicKeys, algorithm);
        return textCodec.encode(output);
    }
}
