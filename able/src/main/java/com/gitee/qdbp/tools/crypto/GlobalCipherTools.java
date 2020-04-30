package com.gitee.qdbp.tools.crypto;

import java.util.HashMap;
import java.util.Map;
import com.gitee.qdbp.tools.utils.ConvertTools;
import com.gitee.qdbp.tools.utils.StringTools;

/**
 * 全局密钥配置工具类<br>
 * 便于业务应用更换自己的密钥或加密方式<br>
 * 只需要调用全局注册方法即可: GlobalCipherTools.register("db", new XxxCipherService());<br>
 * 命令行密码加密: java -cp qdbp-able.jar com.gitee.qdbp.tools.crypto.GlobalCipherTools db password<br>
 *
 * @author zhaohuihua
 * @version 20200419
 */
public class GlobalCipherTools {

    /**
     * 命令行加密入口
     * 
     * @param args 参数: type password
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Missing required arguments");
            System.out.println("java GlobalCipherTools {type} {args}");
            System.out.println("The types available are: " + ConvertTools.joinToString(CIPHER_SERVICE.keySet()));
            return;
        }
        String type = args[0];
        if (args.length < 2) {
            System.out.println("Missing required argument: {password}");
            return;
        }
        String password = args[1];

        CipherService service = CIPHER_SERVICE.get(type);
        if (service == null) {
            System.out.println("Unsupported operate type: " + type);
            System.out.println("The types available are: " + ConvertTools.joinToString(CIPHER_SERVICE.keySet()));
            return;
        }
        System.out.println(service.encrypt(password));
    }

    /**
     * 使用预配置的加密服务接口进行密码加密
     * 
     * @param type 密码类型
     * @param password 密码明文
     * @return 密码密文
     */
    public static String encrypt(String type, String password) {
        return doFindService(type).encrypt(password);
    }

    /**
     * 使用预配置的加密服务接口进行密码加密
     * 
     * @param type 密码类型
     * @param password 密码明文
     * @return 密码密文
     */
    public static byte[] encrypt(String type, byte[] password) {
        return doFindService(type).encrypt(password);
    }

    /**
     * 使用预配置的解密服务接口进行密码解密
     * 
     * @param type 密码类型
     * @param password 密码密文
     * @return 密码明文
     */
    public static String decrypt(String type, String password) {
        return doFindService(type).decrypt(password);
    }

    /**
     * 使用预配置的解密服务接口进行密码解密
     * 
     * @param type 密码类型
     * @param password 密码密文
     * @return 密码明文
     */
    public static byte[] decrypt(String type, byte[] password) {
        return doFindService(type).decrypt(password);
    }

    private static Map<String, CipherService> CIPHER_SERVICE = new HashMap<>();

    public static void register(String types, CipherService service) {
        doRegisterService(types, service);
    }

    private static CipherService doFindService(String type) {
        CipherService service = CIPHER_SERVICE.get(type);
        if (service == null) {
            String types = ConvertTools.joinToString(CIPHER_SERVICE.keySet());
            String msg = "Unsupported cipher type: " + type + ". The types available are: " + types;
            throw new IllegalStateException(msg);
        }
        return service;
    }

    private static void doRegisterService(String types, CipherService service) {
        String[] type = StringTools.split(types, ',', '|', ' ');
        for (String i : type) {
            CIPHER_SERVICE.put(i, service);
        }
    }

    static {
        register("rsa,db,redis", new DefaultRsaCipher());
    }
}
