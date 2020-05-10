package com.gitee.qdbp.tools.crypto;

import com.gitee.qdbp.tools.codec.bytes.Base58Codec;

public class AesEcbTest {

    public static void main(String[] args) {
        String string = "[AES 加密解密 测试]";
        testCipher(string);
    }

    // 3K6WA4rWXuyqLacSVNjMTqJNJvNBeCoFLG6V5GiNzUvN
    protected static void testCipher(String string) {
        AesEcbCipher aesCipher = new AesEcbCipher("w1Ghhxh5jTLLsJFHXgtr", Base58Codec.INSTANCE);
        System.out.println("密钥: " + aesCipher.getSecretKey());
        String ciphertext = aesCipher.encrypt(string);
        System.out.println("加密: " + ciphertext);
        String output = aesCipher.decrypt(ciphertext);
        System.out.println("解密: " + output);
    }
}
