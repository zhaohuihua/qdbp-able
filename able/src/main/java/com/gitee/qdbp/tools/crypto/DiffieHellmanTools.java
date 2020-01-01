package com.gitee.qdbp.tools.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import com.gitee.qdbp.able.exception.ServiceException;
import com.gitee.qdbp.able.result.ResultCode;

/** Diffie-Hellman算法工具类 */
public class DiffieHellmanTools {

    private final static String ALGORITHM = "DiffieHellman";
    private final static String SHA1PRNG = "SHA1PRNG";

    /** 生成密钥 **/
    public static KeyPair generateKeyPair() {
        return generateKeyPair(1024);
    }

    /** 生成密钥 **/
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

    /** 根据对方公钥生成密码 **/
    public static KeyPair generateKeyPairByPublicKey(byte[] otherPublicKey) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(SHA1PRNG);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(otherPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            DHPublicKey pubKey = (DHPublicKey) keyFactory.generatePublic(x509KeySpec);

            // 对方公钥生成密码
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyFactory.getAlgorithm());
            keyPairGenerator.initialize(pubKey.getParams(), secureRandom);

            return keyPairGenerator.generateKeyPair();
        } catch (InvalidAlgorithmParameterException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        } catch (InvalidKeySpecException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        }
    }

    /**
     * 加密
     * 
     * @param data 待加密的数据
     * @param privateKey 己方私钥
     * @param publicKey 对方公钥
     * @param algorithm 对称算法
     * @return 加密后的数据
     */
    public static byte[] encrypt(byte[] data, byte[] privateKey, byte[] publicKey, String algorithm) {
        SecretKey secretKey = generateSecretKey(privateKey, publicKey, algorithm);
        Cipher cipher = getCipherInstance(secretKey, algorithm, Cipher.ENCRYPT_MODE);
        try {
            return cipher.doFinal(data);
        } catch (BadPaddingException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        } catch (IllegalBlockSizeException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        }
    }

    /**
     * 解密
     * 
     * @param data 待解密的数据
     * @param privateKey 己方私钥
     * @param publicKey 对方公钥
     * @param algorithm 对称算法
     * @return 解密后的数据
     */
    public static byte[] decrypt(byte[] data, byte[] privateKey, byte[] publicKey, String algorithm) {
        SecretKey secretKey = generateSecretKey(privateKey, publicKey, algorithm);
        Cipher cipher = getCipherInstance(secretKey, algorithm, Cipher.DECRYPT_MODE);
        try {
            return cipher.doFinal(data);
        } catch (BadPaddingException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        } catch (IllegalBlockSizeException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        }
    }

    private static Cipher getCipherInstance(SecretKey secretKey, String algorithm, int cipherMode) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
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
     * 根据己方私钥和对方公钥生成对称加密的密钥
     * 
     * @param privateKeyBytes 己方私钥
     * @param publicKeyBytes 对方公钥
     * @param algorithm 对称加密算法
     * @return SecretKey
     */
    private static SecretKey generateSecretKey(byte[] privateKeyBytes, byte[] publicKeyBytes, String algorithm) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

            KeyAgreement keyAgreement = KeyAgreement.getInstance(keyFactory.getAlgorithm());
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true);
            // 取主算法, 如AES/CBC/PKCS5Padding取主算法AES
            int slashIndex = algorithm.indexOf('/');
            String mainAlgorithm = slashIndex < 0 ? algorithm : algorithm.substring(0, slashIndex);
            // 生成对称加密的密钥
            return keyAgreement.generateSecret(mainAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        } catch (InvalidKeySpecException e) {
            throw new ServiceException(ResultCode.SERVER_INNER_ERROR, e);
        } catch (InvalidKeyException e) {
            throw new ServiceException(ResultCode.PARAMETER_VALUE_ERROR, e);
        }
    }

}
