package com.cake.framework.cqrs.command;

import com.cake.framework.cqrs.base.Command;

import java.util.UUID;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 22:13
 * @email 18668485565163.com
 */
public class TestAsyncCommand implements Command {

    private String name;


    public TestAsyncCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTraceId() {
        return UUID.randomUUID().toString();
    }


    @Override
    public String toString() {
        return "TestAsyncCommand{" +
                "name='" + name + '\'' +
                '}';
    }
}
