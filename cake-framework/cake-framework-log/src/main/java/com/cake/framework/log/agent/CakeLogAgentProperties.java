package com.cake.framework.log.agent;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/28 22:42
 * @email 18668485565163.com
 */
public class CakeLogAgentProperties {

    public static final String AGENT_LOG_SCAN_PACKAGE = "cake.agent.log.scan.packages";
    public static final String AGENT_LOG_SCAN_PACKAGE_EMPTY = "";
    public static final String AGENT_LOG_BIZ_DOMAIN = "agent.log.biz.domain";
    public static final String AGENT_LOG_DOMAIN_DEFAULT_VALUE = "cake";
    private static final List<String> scanPackages;
    private static final String bizDomain;

    static {
        scanPackages = initScanPackages();
        bizDomain = initBizDomain();
    }

    public static List<String> getScanPackages() {
        return scanPackages;
    }

    public static String getBizDomain() {
        return bizDomain;
    }

    public static List<String> initScanPackages() {
        String agentLogScanPackages = System.getProperty(AGENT_LOG_SCAN_PACKAGE, "");
        List<String> packages = new ArrayList<>();
        if (agentLogScanPackages.equals(AGENT_LOG_SCAN_PACKAGE_EMPTY)) {
            return packages;
        }
        String[] scanPackageArray = agentLogScanPackages.split(",");
        for (String scanPackage : scanPackageArray) {
            if (!scanPackage.trim().equals(AGENT_LOG_SCAN_PACKAGE_EMPTY)) {
                packages.add(scanPackage.trim());
            }
        }
        return packages;
    }

    public static String initBizDomain() {
        return System.getProperty(AGENT_LOG_BIZ_DOMAIN, AGENT_LOG_DOMAIN_DEFAULT_VALUE);
    }
}
