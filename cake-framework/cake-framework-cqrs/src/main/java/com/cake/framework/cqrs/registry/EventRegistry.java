package com.cake.framework.cqrs.registry;

import com.cake.framework.cqrs.base.Event;
import com.cake.framework.cqrs.base.EventHandler;
import com.cake.framework.cqrs.base.Invoker;
import com.cake.framework.cqrs.bus.EventBus;
import com.cake.framework.cqrs.utis.CheckUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 23:05
 * @email 18668485565163.com
 */
public class EventRegistry extends MethodRegistry {

    /**
     * event执行器
     * key 事件类型, value执行器集合
     */
    private final Map<Class<?>, CopyOnWriteArraySet<Invoker>> eventInvokers = new ConcurrentHashMap<>();

    /**
     * 重复检测
     */
    private final Set<Object> registryCheckSet = Collections.synchronizedSet(new HashSet<>());


    /**
     * 事件总线
     */
    private final EventBus eventBus;


    /**
     * 构造器
     *
     * @param eventBus
     */
    public EventRegistry(EventBus eventBus) {
        this.eventBus = eventBus;
    }


    /**
     * 获取所有事件及对应的invokers
     *
     * @return
     */
    public Map<Class<?>, CopyOnWriteArraySet<Invoker>> getInvokers() {
        return eventInvokers;
    }

    public Set<Invoker> getInvokers(Event event) {
        if (event == null) {
            throw new NullPointerException("event is null");
        }
        return eventInvokers.getOrDefault(event.getClass(), new CopyOnWriteArraySet<>());
    }


    public void registry(Object listener) {
        if (listener == null) {
            throw new NullPointerException("listener shouldn't be null");
        }

        // 1. 重复检测
        synchronized (this) {
            if (CheckUtils.isExist(registryCheckSet, listener.getClass())) {
                return;
            }
            registryCheckSet.add(listener.getClass());
        }
        // 2. 加载监听器
        Map<Class<?>, CopyOnWriteArraySet<Invoker>> methodInvokers = getMethodInvokers(listener, EventHandler.class);
        registerAll(methodInvokers);
    }

    /**
     * 将方法的监听器注册到全局中
     *
     * @param methodInvokers
     */
    private void registerAll(Map<Class<?>, CopyOnWriteArraySet<Invoker>> methodInvokers) {
        methodInvokers.forEach((parameterType, listeners) -> {
            if (!Event.class.isAssignableFrom(parameterType)) {
                return;
            }
            if (listeners == null || listeners.isEmpty()) {
                return;
            }
            CopyOnWriteArraySet<Invoker> executors = eventInvokers.getOrDefault(parameterType, new CopyOnWriteArraySet<>());
            executors.addAll(listeners);
            eventInvokers.putIfAbsent(parameterType, executors);
            // check
            checkMultipleRegistration(eventInvokers, parameterType);
        });
    }
}
