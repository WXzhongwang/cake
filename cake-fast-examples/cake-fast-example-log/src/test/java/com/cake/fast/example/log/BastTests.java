package com.cake.fast.example.log;

import com.cake.framework.log.core.LogDO;
import com.cake.framework.log.core.Trace;
import com.cake.framework.log.utils.TraceUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/2 23:02
 * @email 18668485565163.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BastTests {

    @Test
    public void test() {
        Trace.enablePrintConsole();
        Trace.enableTrack();

        MockQueryProcess mockQueryProcess = new MockQueryProcess();
        mockQueryProcess.queryMethod();

        List<LogDO> logDOList = Trace.getTrackLog();
        String tree = TraceUtil.getTraceTree(logDOList.get(0).getTraceId(), logDOList);
        System.out.println(tree);
    }

}
