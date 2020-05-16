package com.gitee.qdbp.tools.crypto;

import com.gitee.qdbp.tools.codec.bytes.Base58Codec;
import com.gitee.qdbp.tools.utils.RandomTools;

public class AesEcbTest {

    public static void main(String[] args) {
        String string = "[AES 加密解密 测试]";
        testCipher(string);
    }

    protected static void testCipher(String string) {
        // 生成随机AES密码
        String aesKey = RandomTools.generateString(20);
        System.out.println("密钥: " + aesKey);
        // AES实例
        AesEcbCipher aesCipher = new AesEcbCipher(aesKey, Base58Codec.INSTANCE);
        // 加密测试
        String ciphertext = aesCipher.encrypt(string);
        System.out.println("加密: " + ciphertext);
        // 解密测试
        String output = aesCipher.decrypt(ciphertext);
        System.out.println("解密: " + output);
    }
}
