package com.cake.framework.cqrs.bus;

import com.cake.framework.cqrs.base.Command;
import com.cake.framework.cqrs.base.Invoker;
import com.cake.framework.cqrs.dispatcher.AsyncCommandDispatcher;
import com.cake.framework.cqrs.dispatcher.CommandDispatcher;
import com.cake.framework.cqrs.executor.ExecutorFactory;
import com.cake.framework.cqrs.registry.CommandRegistry;

import java.util.concurrent.CompletableFuture;

/**
 * 事件总线
 *
 * @author zhongshengwang
 * @description 事件总线
 * @date 2022/10/26 22:47
 * @email 18668485565163.com
 */
public class CommandBus {

    /**
     * 注册器
     */
    private final CommandRegistry invokeRegistry = new CommandRegistry(this);

    /**
     * 同步事件分发
     */
    private final CommandDispatcher dispatcher = new CommandDispatcher();

    /**
     * 异步事件分发
     */
    private final CommandDispatcher asyncCommandDispatcher = new AsyncCommandDispatcher(ExecutorFactory.getThreadPoolExecutor());

    public CommandBus() {

    }

    public CommandRegistry getInvokeRegistry() {
        return invokeRegistry;
    }

    /**
     * 事件分发
     *
     * @param command
     * @param <T>
     * @return
     */
    public <T> T dispatch(Command<T> command) {
        checkCommand(command);
        Invoker invoker = invokeRegistry.getInvoker(command);
        return (T) dispatcher.dispatch(command, invoker);
    }

    /**
     * 事件异步分发
     *
     * @param command
     * @param <T>
     * @return
     */
    public <T> CompletableFuture<T> dispatchAsync(Command<T> command) {
        checkCommand(command);
        Invoker invoker = invokeRegistry.getInvoker(command);
        return (CompletableFuture<T>) asyncCommandDispatcher.dispatch(command, invoker);
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

    private void checkCommand(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("command is null");
        }
    }
}
