package com.cake.framework.common.utils;

import java.io.File;

/**
 * AppNameUtils
 *
 * @author zhongshengwang
 * @description AppNameUtils
 * @date 2022/10/28 23:05
 * @email 18668485565163.com
 */
public class AppNameUtils {

    private AppNameUtils() {
    }

    private static final String PARAM_MARKING_PROJECT = "project.name";
    private static final String PARAM_MARKING_USER_HOME = "user.home";
    private static final String PARAM_MARKING_JBOSS = "jboss.server.home.dir";
    private static final String PARAM_MARKING_JETTY = "jetty.home";
    private static final String PARAM_MARKING_TOMCAT = "catalina.base";
    private static final String LINUX_ADMIN_HOME = "/home/admin";
    private static final String SERVER_JBOSS = "jboss";
    private static final String SERVER_JETTY = "jetty";
    private static final String SERVER_TOMCAT = "tomcat";
    private static final String SERVER_UNKNOWN = "unknown";

    public static String getAppName() {
        String appName = null;
        appName = getAppNameByProjectName();
        if (appName != null) {
            return appName;
        }
        appName = getAppNameByServerHome();
        if (appName != null) {
            return appName;
        }
        return SERVER_UNKNOWN;
    }

    private static String getAppNameByProjectName() {
        return System.getProperty(PARAM_MARKING_PROJECT);
    }

    private static String getAppNameByServerHome() {
        String serverHome = null;
        if (SERVER_JBOSS.equals(getServerType())) {
            serverHome = System.getProperty(PARAM_MARKING_JBOSS);
        } else if (SERVER_JETTY.equals(getServerType())) {
            serverHome = System.getProperty(PARAM_MARKING_JETTY);
        } else if (SERVER_TOMCAT.equals(getServerType())) {
            serverHome = System.getProperty(PARAM_MARKING_TOMCAT);
        }
        if (serverHome != null && serverHome.startsWith(LINUX_ADMIN_HOME)) {
            return substringBetween(serverHome, LINUX_ADMIN_HOME, File.separator);
        }
        return null;
    }

    public static String getServerType() {
        String serverType = null;
        if (System.getProperty(PARAM_MARKING_JBOSS) != null) {
            serverType = SERVER_JBOSS;
        } else if (System.getProperty(PARAM_MARKING_JETTY) != null) {
            serverType = SERVER_JETTY;
        } else if (System.getProperty(PARAM_MARKING_TOMCAT) != null) {
            serverType = SERVER_TOMCAT;
        } else {
            serverType = SERVER_UNKNOWN;
        }
        return serverType;
    }

    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static String getUserHome() {
        return System.getProperty(PARAM_MARKING_USER_HOME);
    }
}
