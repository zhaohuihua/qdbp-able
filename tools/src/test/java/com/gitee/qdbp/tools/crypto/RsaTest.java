package com.gitee.qdbp.tools.crypto;

import com.gitee.qdbp.tools.codec.bytes.Base58Codec;

public class RsaTest {

    public static void main(String[] args) {
        String string = "RSA 加密解密 测试";
        testCipher(string);
    }

    protected static void testCipher(String string) {
        RsaCipher rsaCipher = new RsaCipher(512, Base58Codec.INSTANCE);
        System.out.println("公钥: " + rsaCipher.getPublicKey());
        String ciphertext = rsaCipher.encrypt(string);
        System.out.println("加密: " + ciphertext);
        // A用B的公钥+自己的私钥解密
        String output = rsaCipher.decrypt(ciphertext);
        System.out.println("解密: " + output);
    }
}
