package com.cake.framework.log;

import com.cake.framework.log.agent.CakeAgent;
import com.cake.framework.log.agent.LogAgent;

/**
 * CakeLogInitializer
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/28 22:30
 * @email 18668485565163.com
 */
public class CakeLogInitializer {
    public static void init() {
        LogAgent.install();
        CakeAgent.init();
    }
}
