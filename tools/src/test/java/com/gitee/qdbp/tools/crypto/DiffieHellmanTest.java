package com.gitee.qdbp.tools.crypto;

import java.nio.charset.Charset;
import java.security.KeyPair;
import com.gitee.qdbp.tools.codec.bytes.ByteCodec;
import com.gitee.qdbp.tools.codec.bytes.HexCodec;

public class DiffieHellmanTest {

    public static void main(String[] args) {
        String string = "Diffie-Hellman 测试";
        testCipher(string);
        testTools(string);
    }

    protected static void testCipher(String string) {
        DiffieHellmanCipher aCipher = new DiffieHellmanCipher(512, "AES/ECB/PKCS5Padding");
        DiffieHellmanCipher bCipher = new DiffieHellmanCipher(aCipher.getPublicKey(), aCipher.getAlgorithm());
        System.out.println("A公钥: " + aCipher.getPublicKey());
        System.out.println("B公钥: " + bCipher.getPublicKey());
        // B用A的公钥+自己的私钥加密
        String ciphertext = bCipher.encrypt(string, aCipher.getPublicKey());
        System.out.println("加密: " + ciphertext);
        // A用B的公钥+自己的私钥解密
        String output = aCipher.decrypt(ciphertext, bCipher.getPublicKey());
        System.out.println("解密: " + output);
    }

    protected static void testTools(String string) {
        Charset charset = Charset.forName("UTF-8");
        System.out.println("原字符串: " + string);
        ByteCodec encoder = new HexCodec();
        KeyPair aKeyPair = DiffieHellmanTools.generateKeyPair();
        byte[] aPrivateKey = aKeyPair.getPrivate().getEncoded();
        byte[] aPublicKey = aKeyPair.getPublic().getEncoded();
        System.out.println("A私钥: " + encoder.encode(aPrivateKey));
        System.out.println("A公钥: " + encoder.encode(aPublicKey));
        KeyPair bKeyPair = DiffieHellmanTools.generateKeyPairByPublicKey(aPublicKey);
        byte[] bPrivateKey = bKeyPair.getPrivate().getEncoded();
        byte[] bPublicKey = bKeyPair.getPublic().getEncoded();
        System.out.println("B私钥: " + encoder.encode(bPrivateKey));
        System.out.println("B公钥: " + encoder.encode(bPublicKey));
        byte[] input = string.getBytes(charset);
        for (String algorithm : new String[] { "DES", "DESede", "AES/ECB/PKCS5Padding" }) {
            System.out.println("-----------------------------------------");
            System.out.println("对称加密算法: " + algorithm);
            byte[] encrypt = DiffieHellmanTools.encrypt(input, aPrivateKey, bPublicKey, algorithm);
            System.out.println("加密: " + encoder.encode(encrypt));
            byte[] output = DiffieHellmanTools.decrypt(encrypt, bPrivateKey, aPublicKey, algorithm);
            System.out.println("解密: " + new String(output, charset));
        }
    }
}
