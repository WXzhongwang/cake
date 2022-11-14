package com.cake.framework.log;

import com.cake.framework.log.agent.CakeLogAgentProperties;
import com.cake.framework.log.core.LogDO;
import com.cake.framework.log.core.Trace;
import com.cake.framework.log.utils.TraceUtil;

import java.util.List;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/29 21:59
 * @email 18668485565163.com
 */
public class MainTests {


    public static void main(String[] args) {
        System.setProperty(CakeLogAgentProperties.AGENT_LOG_SCAN_PACKAGE, "com.cake.framework.log");
        CakeLogInitializer.init();

        Trace.enablePrintConsole();
        Trace.enableTrack();

        MockQueryProcess mockQueryProcess = new MockQueryProcess();
        mockQueryProcess.queryMethod();

        List<LogDO> logDOList = Trace.getTrackLog();
        String tree = TraceUtil.getTraceTree(logDOList.get(0).getTraceId(), logDOList);
        System.out.println(tree);
    }
}
