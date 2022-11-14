package com.cake.framework.log.agent;

import com.cake.framework.log.annotation.CakeLogInterceptor;
import com.cake.framework.log.annotation.EnableTrace;
import com.cake.framework.log.annotation.LogPoint;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.List;

/**
 * LogAgent
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/28 22:31
 * @email 18668485565163.com
 */
@Slf4j
public class LogAgent {

    public static void install() {
        CakeAgent.installAgent(ElementMatchers.isAnnotatedWith(EnableTrace.class),
                ElementMatchers.isAnnotatedWith(LogPoint.class),
                CakeLogInterceptor.class);
        installScanPackage();
    }

    public static void installScanPackage() {
        List<String> scanPackages = CakeLogAgentProperties.getScanPackages();
        if (scanPackages.isEmpty()) {
            return;
        }
        for (String scanPackage : scanPackages) {
            CakeAgent.installAgent(ElementMatchers.nameStartsWith(scanPackage),
                    ElementMatchers.isAnnotatedWith(LogPoint.class),
                    CakeLogInterceptor.class);
            log.info("package installed: {}", scanPackage);
        }
    }
}
