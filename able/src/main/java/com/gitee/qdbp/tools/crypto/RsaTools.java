package com.gitee.qdbp.tools.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;

/**
 * RSA算法工具类
 *
 * @author zhaohuihua
 * @version 191228
 */
public class RsaTools {

    private final static String ALGORITHM = "RSA";
    private final static String SHA1PRNG = "SHA1PRNG";

    /** 生成密钥 **/
    public static KeyPair generateKeyPair() {
        return generateKeyPair(1024);
    }

    /** 生成密钥对 **/
    public static KeyPair generateKeyPair(int keySize) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SHA1PRNG);
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(keySize, secureRandom);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        }
    }

    /**
     * 根据公钥加密
     * 
     * @param data 待加密的数据
     * @param publicKeyBytes 公钥
     * @return 加密后的数据
     */
    public static byte[] encrypt(byte[] data, byte[] publicKeyBytes) {
        PublicKey publicKey = generatePublicKey(publicKeyBytes);
        Cipher cipher = getCipherInstance(publicKey, Cipher.ENCRYPT_MODE);
        try {
            return cipher.doFinal(data);
        } catch (BadPaddingException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        } catch (IllegalBlockSizeException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        }
    }

    /**
     * 根据私钥解密
     * 
     * @param data 待解密的数据
     * @param privateKeyBytes 私钥
     * @param algorithm 对称算法
     * @return 解密后的数据
     */
    public static byte[] decrypt(byte[] data, byte[] privateKeyBytes) {
        PrivateKey privateKey = generatePrivateKey(privateKeyBytes);
        Cipher cipher = getCipherInstance(privateKey, Cipher.DECRYPT_MODE);
        try {
            return cipher.doFinal(data);
        } catch (BadPaddingException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        } catch (IllegalBlockSizeException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        }
    }

    private static Cipher getCipherInstance(Key secretKey, int cipherMode) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
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

    /**
     * 根据公钥数据生成公钥对象
     * 
     * @param publicKeyBytes 公钥数据
     * @return PublicKey对象
     */
    private static PublicKey generatePublicKey(byte[] publicKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(x509KeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        } catch (InvalidKeySpecException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        }
    }

    /**
     * 根据私钥数据生成私钥对象
     * 
     * @param privateKeyBytes 私钥数据
     * @return PrivateKey对象
     */
    private static PrivateKey generatePrivateKey(byte[] publicKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        } catch (InvalidKeySpecException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        }
    }

}
