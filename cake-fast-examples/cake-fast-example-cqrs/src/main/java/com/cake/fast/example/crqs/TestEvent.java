package com.cake.fast.example.crqs;

import com.cake.framework.cqrs.base.Event;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 22:15
 * @email 18668485565163.com
 */
public class TestEvent implements Event {

    private final String eventName;

    public TestEvent(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return "TestEvent{" +
                "eventName='" + eventName + '\'' +
                '}';
    }
}
