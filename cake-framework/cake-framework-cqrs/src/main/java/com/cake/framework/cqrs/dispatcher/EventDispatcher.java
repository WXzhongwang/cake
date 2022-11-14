package com.cake.framework.cqrs.dispatcher;

import com.cake.framework.cqrs.base.Invoker;

import java.util.Collection;
import java.util.concurrent.Executor;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 23:00
 * @email 18668485565163.com
 */
public class EventDispatcher {

    private final Executor executor;

    public EventDispatcher(Executor executor) {
        this.executor = executor;
    }

    public void dispatch(Object event, Collection<Invoker> invokers) {
        if (event == null || invokers == null || invokers.isEmpty()) {
            throw new IllegalArgumentException("EventDispatcher argument is null");
        }
        // 逐个分发
        invokers.forEach(invoker -> { executor.execute(() -> invoker.handleMethod(event));});
    }
}
