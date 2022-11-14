package com.cake.framework.cqrs.dispatcher;

import com.cake.framework.cqrs.base.Invoker;
import com.cake.framework.cqrs.base.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 23:00
 * @email 18668485565163.com
 */
public class QueryDispatcher {

    private final Executor executor;

    public QueryDispatcher(Executor executor) {
        this.executor = executor;
    }

    /**
     * 查询分发
     *
     * @param query
     * @param invoker
     * @return
     */
    public Object dispatch(Query query, Invoker invoker) {
        if (query == null || invoker == null) {
            throw new IllegalArgumentException("QueryDispatcher argument is null");
        }
        return invoker.handleMethod(query);
    }

    /**
     * 查询分发异步
     *
     * @param query
     * @param invoker
     * @return
     */
    public <T> CompletableFuture<T> dispatchAsync(Query<T> query, Invoker invoker) {
        if (query == null || invoker == null) {
            throw new IllegalArgumentException("QueryDispatcher.dispatchAsync argument is null");
        }
        return CompletableFuture.supplyAsync(() -> (T) invoker.handleMethod(query), executor);
    }

    /**
     * 查询分发异步批量
     *
     * @param queryInvokerMap
     * @return
     */
    public Map<Class, CompletableFuture> dispatchAsync(Map<Query, Invoker> queryInvokerMap) {
        if (queryInvokerMap == null || queryInvokerMap.isEmpty()) {
            throw new IllegalArgumentException("QueryDispatcher.dispatchAsync argument is null");
        }
        Map<Class, CompletableFuture> resultMap = new HashMap<>(queryInvokerMap.size());
        queryInvokerMap.forEach((query, invoker) -> {
            CompletableFuture future = CompletableFuture.supplyAsync(() -> invoker.handleMethod(query), executor);
            resultMap.putIfAbsent(query.getClass(), future);
        });
        return resultMap;
    }
}
