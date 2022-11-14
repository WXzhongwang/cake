package com.cake.framework.cqrs.dispatcher;

import com.cake.framework.cqrs.base.Command;
import com.cake.framework.cqrs.base.Invoker;

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
public class AsyncCommandDispatcher extends CommandDispatcher {

    private final Executor executor;

    public AsyncCommandDispatcher(Executor executor) {
        this.executor = executor;
    }

    @Override
    public CompletableFuture dispatch(Command command, Invoker invoker) {
        return CompletableFuture.supplyAsync(() -> super.dispatch(command, invoker), executor);
    }
}
