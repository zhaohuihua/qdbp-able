package com.gitee.qdbp.tools.codec;

import java.nio.charset.Charset;

public class Base58Test {

    public static void main(String[] args) {
        Charset charset = Charset.forName("UTF-8");
        String original = "Base58 编码测试";
        String encoded = Base58Tools.encode(original.getBytes(charset));
        System.out.println("编码: " + encoded);
        byte[] decoded = Base58Tools.decode(encoded);
        String string = new String(decoded, charset);
        System.out.println("还原: " + string);
    }
}
