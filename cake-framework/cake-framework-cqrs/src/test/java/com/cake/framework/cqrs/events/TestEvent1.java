package com.cake.framework.cqrs.events;

import com.cake.framework.cqrs.base.Event;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 22:59
 * @email 18668485565163.com
 */
public class TestEvent1 implements Event {

    private String name;

    public TestEvent1(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestEvent1{" +
                "name='" + name + '\'' +
                '}';
    }
}
