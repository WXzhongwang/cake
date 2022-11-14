package com.cake.framework.cqrs.registry;

import com.cake.framework.cqrs.base.Invoker;
import com.cake.framework.cqrs.base.Query;
import com.cake.framework.cqrs.base.QueryHandler;
import com.cake.framework.cqrs.bus.QueryBus;
import com.cake.framework.cqrs.utis.CheckUtils;

import java.util.*;
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
public class QueryRegistry extends MethodRegistry {
    /**
     * query执行器
     * key 事件类型, value执行器集合
     */
    private final Map<Class<?>, CopyOnWriteArraySet<Invoker>> queryInvokers = new ConcurrentHashMap<>();

    /**
     * 重复检测
     */
    private final Set<Object> registryCheckSet = Collections.synchronizedSet(new HashSet<>());


    /**
     * 查询总线
     */
    private final QueryBus queryBus;


    /**
     * 构造器
     *
     * @param queryBus
     */
    public QueryRegistry(QueryBus queryBus) {
        this.queryBus = queryBus;
    }


    /**
     * 获取所有事件及对应的invokers
     *
     * @return
     */
    public Map<Class<?>, CopyOnWriteArraySet<Invoker>> getInvokers() {
        return queryInvokers;
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
        Map<Class<?>, CopyOnWriteArraySet<Invoker>> methodInvokers = getMethodInvokers(listener, QueryHandler.class);
        registerAll(methodInvokers);
    }

    /**
     * 将方法的监听器注册到全局中
     *
     * @param methodInvokers
     */
    private void registerAll(Map<Class<?>, CopyOnWriteArraySet<Invoker>> methodInvokers) {
        methodInvokers.forEach((parameterType, listeners) -> {
            if (!Query.class.isAssignableFrom(parameterType)) {
                return;
            }
            if (listeners == null || listeners.isEmpty()) {
                return;
            }
            CopyOnWriteArraySet<Invoker> executors = queryInvokers.getOrDefault(parameterType, new CopyOnWriteArraySet<>());
            executors.addAll(listeners);
            queryInvokers.putIfAbsent(parameterType, executors);
            // check
            checkMultipleRegistration(queryInvokers, parameterType);
        });
    }


    /**
     * 获取特定事件对应的invoker
     *
     * @return
     */
    public Invoker getInvoker(Query query) {
        if (query == null) {
            throw new NullPointerException("query is null");
        }
        CopyOnWriteArraySet<Invoker> invokers = queryInvokers.getOrDefault(query.getClass(), new CopyOnWriteArraySet<>());
        if (invokers.isEmpty()) {
            throw new NullPointerException("query invoker is not found, query is " + query);
        }
        return new ArrayList<>(invokers).get(0);
    }
}
