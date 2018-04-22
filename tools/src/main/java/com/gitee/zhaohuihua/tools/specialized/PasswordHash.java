package com.gitee.zhaohuihua.tools.specialized;

/*
 * Password Hashing With PBKDF2 (http://crackstation.net/hashing-security.htm).
 * Copyright (c) 2013, Taylor Hornby
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * PBKDF2 salted password hashing. Author: havoc AT defuse.ca www: <br>
 * http://crackstation.net/hashing-security.htm <br>
 */

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * 密码盐渍算法工具类,生成70个字符的密码hash,可以调整SALT_BYTE_SIZE,HASH_BYTE_SIZE来改变<br>
 * <br>
 * how to use:
 * <pre>
 * String password = &quot;123456&quot;;
 * String ciphertext = PasswordHash.DEFAULT.createHash(password);
 * boolean success = PasswordHash.DEFAULT.validatePassword(password, ciphertext);
 * </pre>
 **/
public class PasswordHash {

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";

    private static final int CHAR_SIZE = 70;
    private static final int PBKDF2_ITERATIONS = 200;

    public static final PasswordHash DEFAULT = new PasswordHash(CHAR_SIZE, PBKDF2_ITERATIONS);

    // The following constants may be changed without breaking existing hashes.
    private final int saltSize;
    private final int hashSize;
    private final int pbkdf2Iterations;

    /**
     * 构造函数
     *
     * @param charSize 字符个数, 其中salt和hash各占一半
     * @param pbkdf2Iterations 重复计算次数, 这个数字太大会影响速度, 最好不要超过1000
     */
    public PasswordHash(int charSize, int pbkdf2Iterations) {
        int half = charSize / 2 / 2 * 2; // 保证该数值是双数, 如70变成34
        this.saltSize = half;
        this.hashSize = charSize - half;
        this.pbkdf2Iterations = pbkdf2Iterations;
    }

    /**
     *
     * 构造函数
     *
     * @param saltSize salt字符个数
     * @param hashSize hash字符个数
     * @param pbkdf2Iterations 重复计算次数, 这个数字太大会影响速度, 最好不要超过1000
     */
    public PasswordHash(int saltSize, int hashSize, int pbkdf2Iterations) {
        this.saltSize = saltSize;
        this.hashSize = hashSize;
        this.pbkdf2Iterations = pbkdf2Iterations;
    }

    /**
     * 加盐处理密码,返回处理后的hash
     *
     * @param password
     * @return 加盐处理后的hash
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public String createHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return createHash(password.toCharArray());
    }

    /**
     * 加盐处理密码,返回处理后的hash
     *
     * @param password
     * @return 加盐处理后的hash
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public String createHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[saltSize / 2]; // byte转换为16进制占两个字符, 所以除2
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password, salt, pbkdf2Iterations, hashSize / 2);
        // format iterations:salt:hash
        return toHex(salt) + toHex(hash);
    }

    /**
     * 验证密码与 盐渍hash 是否匹配
     * <p>
     * return true 表示匹配,反之则false
     * </p>
     *
     * @param password
     * @param correctHash
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public boolean validatePassword(String password, String correctHash) throws NoSuchAlgorithmException,
    InvalidKeySpecException {
        return validatePassword(password.toCharArray(), correctHash);
    }

    /**
     * 验证密码与 盐渍hash 是否匹配
     * <p>
     * return true 表示匹配,反之则false
     * </p>
     *
     * @param password
     * @param correctHash
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public boolean validatePassword(char[] password, String correctHash) throws NoSuchAlgorithmException,
    InvalidKeySpecException {
        if (correctHash.length() != saltSize + hashSize) {
            return false;
        }
        // Decode the hash into its parameters
        int iterations = pbkdf2Iterations;
        byte[] salt = fromHex(correctHash.substring(0, saltSize));
        byte[] hash = fromHex(correctHash.substring(saltSize));
        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method is used so that password hashes cannot
     * be extracted from an on-line system using a timing attack and then attacked off-line.
     *
     * @param a the first byte array
     * @param b the second byte array
     * @return true if both byte arrays are the same, false if not
     */
    private boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    /**
     * Computes the PBKDF2 hash of a password.
     *
     * @param password the password to hash.
     * @param salt the salt
     * @param iterations the iteration count (slowness factor)
     * @param bytes the length of the hash to compute in bytes
     * @return the PBDKF2 hash of the password
     */
    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param hex the hex string
     * @return the hex string decoded into a byte array
     */
    private byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param array the byte array to convert
     * @return a length*2 character string encoding the byte array
     */
    private String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = array.length * 2 - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }
}
