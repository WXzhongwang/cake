package com.cake.framework.cqrs;

import com.cake.framework.cqrs.base.CommandHandler;
import com.cake.framework.cqrs.bus.CommandBus;
import com.cake.framework.cqrs.command.TestAsyncCommand;
import com.cake.framework.cqrs.command.TestCommand;
import com.cake.framework.cqrs.events.TestEvent;

import java.util.concurrent.ExecutionException;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 22:11
 * @email 18668485565163.com
 */
public class CommonBusTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CommandBus commonBus = new CommandBus();
        commonBus.register(new CommonBusTest());
        System.out.println(commonBus.dispatch(new TestCommand("AAA")));
        System.out.println(commonBus.dispatchAsync(new TestAsyncCommand("BBB")).get());
    }

    @CommandHandler(name = "testEvent")
    public TestEvent testEvent(TestCommand command) {
        System.out.println("common command: " + command);
        return new TestEvent("new-test-event");
    }

    @CommandHandler(name = "testEvent2")
    public TestEvent testEvent2(TestAsyncCommand asyncCommand) {
        System.out.println("async command: " + asyncCommand);
        return new TestEvent("new-test-event-async");
    }
}
