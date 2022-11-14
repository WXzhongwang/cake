package com.cake.framework.cqrs.bus;

import com.cake.framework.cqrs.base.Invoker;
import com.cake.framework.cqrs.base.Query;
import com.cake.framework.cqrs.dispatcher.QueryDispatcher;
import com.cake.framework.cqrs.executor.ExecutorFactory;
import com.cake.framework.cqrs.registry.QueryRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * QueryBus
 *
 * @author zhongshengwang
 * @description QueryBus
 * @date 2022/10/26 22:47
 * @email 18668485565163.com
 */
public class QueryBus {

    /**
     * 注册器
     */
    private final QueryRegistry invokeRegistry = new QueryRegistry(this);


    /**
     * 同步事件分发
     */
    private final QueryDispatcher dispatcher = new QueryDispatcher(ExecutorFactory.getThreadPoolExecutor());

    public QueryBus() {
    }

    /**
     * 事件分发
     *
     * @param query
     * @param <T>
     * @return
     */
    public <T> T dispatch(Query<T> query) {
        checkQuery(query);
        Invoker invoker = invokeRegistry.getInvoker(query);
        return (T) dispatcher.dispatch(query, invoker);
    }


    /**
     * 事件异步分发
     *
     * @param query
     * @param <T>
     * @return
     */
    public <T> CompletableFuture<T> dispatchAsync(Query<T> query) {
        checkQuery(query);
        Invoker invoker = invokeRegistry.getInvoker(query);
        return (CompletableFuture<T>) dispatcher.dispatchAsync(query, invoker);
    }


    /**
     * 事件异步分发批量
     *
     * @param queries
     * @param <T>
     * @return
     */
    public Map<Class, CompletableFuture> dispatchAsync(Query... queries) {
        if (queries == null || queries.length == 0) {
            throw new IllegalArgumentException("queries is null");
        }
        Map<Query, Invoker> queriesMap = new HashMap<>(queries.length);
        for (Query query : queries) {
            queriesMap.put(query, invokeRegistry.getInvoker(query));
        }
        return dispatcher.dispatchAsync(queriesMap);
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

    private void checkQuery(Query query) {
        if (query == null) {
            throw new IllegalArgumentException("query is null");
        }
    }
}
