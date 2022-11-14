package com.cake.framework.cqrs.bus;

import com.cake.framework.cqrs.base.Event;
import com.cake.framework.cqrs.base.Invoker;
import com.cake.framework.cqrs.dispatcher.EventDispatcher;
import com.cake.framework.cqrs.executor.ExecutorFactory;
import com.cake.framework.cqrs.registry.EventRegistry;

import java.util.Set;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 22:47
 * @email 18668485565163.com
 */
public class EventBus {

    /**
     * 注册器
     */
    private final EventRegistry invokeRegistry = new EventRegistry(this);

    /**
     * 同步事件分发
     */
    private final EventDispatcher dispatcher = new EventDispatcher(ExecutorFactory.getDirectExecutor());

    /**
     * 异步事件分发
     */
    private final EventDispatcher asyncEventDispatcher = new EventDispatcher(ExecutorFactory.getThreadPoolExecutor());

    public EventBus() {
    }

    public EventRegistry getInvokeRegistry() {
        return invokeRegistry;
    }

    /**
     * 事件分发
     *
     * @param event
     * @return
     */
    public boolean dispatch(Event event) {
        return dispatch(event, dispatcher);
    }


    /**
     * 事件分发异步
     *
     * @param event
     * @return
     */
    public boolean dispatchAsync(Event event) {
        return dispatch(event, asyncEventDispatcher);
    }

    private boolean dispatch(Event event, EventDispatcher dispatcher) {
        checkEvent(event);
        Set<Invoker> invokers = invokeRegistry.getInvokers(event);
        dispatcher.dispatch(event, invokers);
        return true;
    }

    private void checkEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("event is null");
        }
    }

    /**
     * 事件总线注册
     *
     * @param listener
     */
    public void register(Object listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener can't be null");
        }
        invokeRegistry.registry(listener);
    }


    /**
     * 取消注册
     *
     * @param listener
     */
    public void unRegister(Object listener) {
        // not supported
    }
}
