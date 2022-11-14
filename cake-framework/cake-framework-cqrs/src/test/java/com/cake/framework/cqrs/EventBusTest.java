package com.cake.framework.cqrs;

import com.cake.framework.cqrs.base.EventHandler;
import com.cake.framework.cqrs.bus.EventBus;
import com.cake.framework.cqrs.events.TestEvent1;
import com.cake.framework.cqrs.events.TestEvent2;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 22:58
 * @email 18668485565163.com
 */
public class EventBusTest {


    @EventHandler(name = "handleEvent1")
    public void handleEvent1(TestEvent1 testEvent1) {
        System.out.println("handleEvent1: " + testEvent1.toString());
    }

    @EventHandler(name = "handleEvent2")
    public void handleEvent1(TestEvent2 testEvent2) {
        System.out.println("handleEvent2: " + testEvent2.toString());
    }

    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        eventBus.register(new EventBusTest());
        eventBus.register(new EventBusTest());
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                eventBus.dispatch(new TestEvent1("AAAA"));
            }).run();
        }
    }

}
