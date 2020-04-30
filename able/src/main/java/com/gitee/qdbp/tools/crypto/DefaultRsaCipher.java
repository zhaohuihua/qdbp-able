package com.gitee.qdbp.tools.crypto;

import com.gitee.qdbp.tools.codec.bytes.Base58Codec;

/**
 * 默认的RSA加密解密服务程序
 *
 * @author zhaohuihua
 * @version 20200419
 */
public class DefaultRsaCipher extends RsaCipher {

    private static final String DEFAULT_PUBLIC_KEY =
            "rbanryFCC6yJnCRCQgVWyYXvqkSBz4k8Kh2Kd4ZjPzHKmFhxjh8maz2cHoPaepsHfcEg1MLgT7KMx2BKsFn2h6kxMFVPKYi4EDYZQUnYP5uGSzFqSsvNTTCwLWpUSNwS";
    private static final String DEFAULT_PRIVATE_KEY =
            "MP38SNHbnVWjkcky22qMDoFbcXapv3fJcfQk1Zjs1UWwKGapFDuW2Dos64PpBefHy4n4p1RdksgXNJmtT1KLBRaUUjnQ79HRrazdGcq2rrCiKjEEN56D2HCQouUXrpA6weSE6p5y3GAZ69bjBW8CbQPy6YNtf18Jor3iJZ5foJJhn6oauY2mxQXNJ4j2ajuFvsAWhNRLLubhFQrJj9HePWfLF1K4fGBFn6x3nPG38xvVmBRoetqT6tH4z8UgchUwr2Zx9rscvYfyShAwGiM8ncudkYzyD1UxUQiuuJJFWEv2iMDEARJ89d36x1C885gqrrFaYYC3bPMAxJ4gWuLQWYxqJjCx7vv4YU1TyigAwAr6MqgdkGMXZen4sGefLS7PkKyGmN16ecjS4qm9WEwhqoGq9vx85NFNqHAUMWd5Y1KgzsHBCZVhdTxXdkZUWrz8xCeVZMHZqAStJ46wKuDsgZX";

    public DefaultRsaCipher() {
        super(DEFAULT_PUBLIC_KEY, DEFAULT_PRIVATE_KEY, Base58Codec.INSTANCE);
    }

}
