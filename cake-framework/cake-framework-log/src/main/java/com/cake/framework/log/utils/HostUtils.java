package com.cake.framework.log.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/29 00:35
 * @email 18668485565163.com
 */
public class HostUtils {

    public static String host;
    public static String ip;

    static {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            host = ia.getHostName();
            ip = ia.getHostAddress();
        } catch (UnknownHostException ignore) {

        }
    }
}
