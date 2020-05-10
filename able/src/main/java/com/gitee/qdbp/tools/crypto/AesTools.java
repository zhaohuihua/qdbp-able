package com.gitee.qdbp.tools.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;

/**
 * AES/ECB加密工具类
 *
 * @author zhaohuihua
 * @version 20200510
 */
public class AesTools {

    private final static String ALGORITHM = "AES";
    private final static String SHA1PRNG = "SHA1PRNG";
    private final static String ECB_CIPHER = "AES/ECB/PKCS5Padding";

    /**
     * 数据加密
     * 
     * @param data 待加密的数据
     * @param key 密钥
     * @return 加密后的数据
     */
    public static byte[] ecbEncrypt(byte[] data, SecretKey key) {
        Cipher cipher = getEcbCipherInstance(key, Cipher.ENCRYPT_MODE);
        try {
            return cipher.doFinal(data);
        } catch (BadPaddingException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        } catch (IllegalBlockSizeException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        }
    }

    /**
     * 数据解密
     * 
     * @param data 待解密的数据
     * @param key 密钥
     * @return 解密后的数据
     */
    public static byte[] ecbDecrypt(byte[] data, SecretKey key) {
        Cipher cipher = getEcbCipherInstance(key, Cipher.DECRYPT_MODE);
        try {
            return cipher.doFinal(data);
        } catch (BadPaddingException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        } catch (IllegalBlockSizeException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        }
    }

    /**
     * 根据密钥数据生成密钥对象
     * 
     * @param keyBytes 密钥数据
     * @param keyPadding key是否需要补全(如果key是32位的16进制字符串则不需要)
     * @return SecretKey对象
     */
    public static SecretKey generateSecretKey(byte[] keyBytes, boolean keyPadding) {
        try {
            if (!keyPadding) {
                return new SecretKeySpec(keyBytes, ALGORITHM);
            } else {
                KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
                // 不能用这个, 在linux下每次生成密钥是随机的
                // kgen.init(128, new SecureRandom(pwd.getBytes()));
                SecureRandom secureRandom = SecureRandom.getInstance(SHA1PRNG);
                secureRandom.setSeed(keyBytes);
                kgen.init(128, secureRandom);
                SecretKey secretKey = kgen.generateKey();
                return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        }
    }

    private static Cipher getEcbCipherInstance(Key secretKey, int cipherMode) {
        try {
            Cipher cipher = Cipher.getInstance(ECB_CIPHER);
            cipher.init(cipherMode, secretKey);
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        } catch (NoSuchPaddingException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        } catch (InvalidKeyException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        }
    }

}
