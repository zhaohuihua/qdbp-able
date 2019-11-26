package com.gitee.qdbp.tools.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 网络工具类
 * 
 * @author zhaohuihua
 * @version 190908
 */
public class NetworkTools {

    /** 静态工具类私有构造方法 **/
    private NetworkTools() {
    }

    /** 获取本机主机名称 **/
    public static String getLoalHostName() {
        String userName = System.getProperty("user.name");
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            return StringTools.concat('@', userName, hostName);
        } catch (UnknownHostException e) {
            return userName;
        }
    }

    /** 获取本机IP地址 **/
    public static List<String> getLocalIpAddress() {
        List<String> list = new ArrayList<>();
        // 遍历本机的所有网络接口
        Enumeration<NetworkInterface> networks;
        try {
            networks = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return list;
        }
        while (networks.hasMoreElements()) {
            NetworkInterface network = networks.nextElement();
            // String networkName = network.getName();
            String displayName = network.getDisplayName();
            if (displayName.toLowerCase().contains("virtual")) {
                continue; // 忽略虚拟机的IP地址
            }
            // 遍历该网络接口绑定的IP地址
            Enumeration<InetAddress> addresses = network.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (!(address instanceof Inet4Address)) {
                    continue; // 忽略非IPv4地址
                }
                if ("127.0.0.1".equals(address.getHostAddress())) {
                    continue; // 忽略127.0.0.1地址
                }
                list.add(address.getHostAddress());
            }
        }
        return list;
    }
}
