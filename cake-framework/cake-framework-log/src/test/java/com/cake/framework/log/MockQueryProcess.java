package com.cake.framework.log;

import com.cake.framework.log.annotation.EnableTrace;
import com.cake.framework.log.annotation.LogEntry;
import com.cake.framework.log.annotation.LogPoint;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/29 22:20
 * @email 18668485565163.com
 */
@EnableTrace
public class MockQueryProcess {

    @LogEntry(bizType = "查询场景")
    @LogPoint(title = "查询A", bizScenario = "查询用户余额")
    public void queryMethod() {
        System.out.println("A");
        queryMethodB();
    }

    @LogPoint(title = "查询B")
    public void queryMethodB() {
        System.out.println("B");
        queryMethodC();
    }

    @LogPoint(title = "查询C")
    public void queryMethodC() {
        System.out.println("C");
    }

}
