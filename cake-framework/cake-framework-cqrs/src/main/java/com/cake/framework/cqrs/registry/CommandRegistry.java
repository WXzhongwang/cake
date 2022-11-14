package com.cake.framework.cqrs.registry;

import com.cake.framework.cqrs.base.Command;
import com.cake.framework.cqrs.base.CommandHandler;
import com.cake.framework.cqrs.base.Invoker;
import com.cake.framework.cqrs.bus.CommandBus;
import com.cake.framework.cqrs.utis.CheckUtils;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class CommandRegistry extends MethodRegistry {

    /**
     * command执行器
     * key 事件类型, value执行器集合
     */
    private final Map<Class<?>, CopyOnWriteArraySet<Invoker>> commandInvokers = new ConcurrentHashMap<>();

    /**
     * 重复检测
     */
    private final Set<Object> registryCheckSet = Collections.synchronizedSet(new HashSet<>());


    /**
     * 命令总线
     */
    private final CommandBus commandBus;


    /**
     * 构造器
     *
     * @param commandBus
     */
    public CommandRegistry(CommandBus commandBus) {
        this.commandBus = commandBus;
    }


    /**
     * 获取所有事件及对应的invokers
     *
     * @return
     */
    public Map<Class<?>, CopyOnWriteArraySet<Invoker>> getInvokers() {
        return commandInvokers;
    }


    /**
     * 获取特定事件对应的invoker
     *
     * @return
     */
    public Invoker getInvoker(Command command) {
        if (command == null) {
            throw new NullPointerException("command is null");
        }
        CopyOnWriteArraySet<Invoker> invokers = commandInvokers.getOrDefault(command.getClass(), new CopyOnWriteArraySet<>());
        if (invokers.isEmpty()) {
            throw new NullPointerException("command invoker is not found, command is " + command);
        }
        return new ArrayList<>(invokers).get(0);
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
        Map<Class<?>, CopyOnWriteArraySet<Invoker>> methodInvokers = getMethodInvokers(listener, CommandHandler.class);
        registerAll(methodInvokers);
    }

    /**
     * 将方法的监听器注册到全局中
     *
     * @param methodInvokers
     */
    private void registerAll(Map<Class<?>, CopyOnWriteArraySet<Invoker>> methodInvokers) {
        methodInvokers.forEach((parameterType, listeners) -> {
            if (!Command.class.isAssignableFrom(parameterType)) {
                return;
            }
            if (listeners == null || listeners.isEmpty()) {
                return;
            }
            CopyOnWriteArraySet<Invoker> executors = commandInvokers.getOrDefault(parameterType, new CopyOnWriteArraySet<>());
            executors.addAll(listeners);
            commandInvokers.putIfAbsent(parameterType, executors);
            // check
            checkMultipleRegistration(commandInvokers, parameterType);
        });
    }
}
