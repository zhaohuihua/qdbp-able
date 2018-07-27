package com.gitee.qdbp.tools.codec;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 摘要工具类
 *
 * @author zhaohuihua
 * @version 160301
 */
public abstract class DigestTools {

    private static final Logger log = LoggerFactory.getLogger(DigestTools.class);

    public static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return HexTools.toString(md.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 error", e);
            return null;
        }
    }

    public static String sha256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexTools.toString(md.digest(text.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 error", e);
            return null;
        }
    }
}
