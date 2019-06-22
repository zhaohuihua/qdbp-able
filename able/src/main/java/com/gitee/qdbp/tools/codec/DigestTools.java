package com.gitee.qdbp.tools.codec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 摘要工具类
 *
 * @author zhaohuihua
 * @version 160301
 */
public abstract class DigestTools {

    private static final String MD5 = "MD5";
    private static final String SHA256 = "SHA-256";

    /**
     * 生成MD5摘要
     * 
     * @param text 目标文本
     * @return 摘要文本
     */
    public static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            return HexTools.toString(md.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm: " + MD5, e);
        }
    }

    /**
     * 生成SHA-256摘要
     * 
     * @param text 目标文本
     * @return 摘要文本
     */
    public static String sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA256);
            return HexTools.toString(md.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm: " + SHA256, e);
        }
    }
}
