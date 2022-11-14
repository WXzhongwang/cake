package com.cake.framework.cqrs.registry;

import com.cake.framework.cqrs.base.*;
import com.cake.framework.cqrs.utis.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 23:05
 * @email 18668485565163.com
 */
public class MethodRegistry {

    protected void checkMultipleRegistration(Map<Class<?>, CopyOnWriteArraySet<Invoker>> invokerMap, Class<?> parameterType) {
        CopyOnWriteArraySet<Invoker> invokers = invokerMap.get(parameterType);
        if (invokers == null || invokers.isEmpty()) {
            return;
        }

        // query handler只能一对一
        if (Query.class.isAssignableFrom(parameterType) && invokers.size() > 1) {
            throw new IllegalStateException("Query " + parameterType + " can't have multiple handlers");
        }

        // command handler只能一对一
        if (Command.class.isAssignableFrom(parameterType) && invokers.size() > 1) {
            throw new IllegalStateException("Command " + parameterType + " can't have multiple handlers");
        }
    }

    protected Map<Class<?>, CopyOnWriteArraySet<Invoker>> getMethodInvokers(Object listener, Class handlerClazz) {
        List<Method> methods = ReflectionUtils.getMethodList(listener);
        Map<Class<?>, CopyOnWriteArraySet<Invoker>> resultMap = new HashMap<>(methods.size());
        for (Method method : methods) {
            if (!isBusHandler(method, handlerClazz)) {
                continue;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                // handler method 只能有一个参数
                throw new IllegalArgumentException("Method " + method + " has @Handler annotation, but subscribe must require a single argument");
            }
            Class<?> parameterType = parameterTypes[0];
            CopyOnWriteArraySet<Invoker> invokers = resultMap.getOrDefault(parameterType, new CopyOnWriteArraySet<>());
            String handlerName = getHandlerName(method, handlerClazz);
            invokers.add(new Invoker(listener, method, handlerName));
            resultMap.putIfAbsent(parameterType, invokers);
            // check
            checkMultipleRegistration(resultMap, parameterType);
        }
        return resultMap;
    }

    protected boolean isBusHandler(Method method, Class handlerClazz) {
        if (method == null) {
            return false;
        }
        return method.isAnnotationPresent(handlerClazz);
    }

    private String getHandlerName(Method method, Class handlerClazz) {
        String name = null;
        if (CommandHandler.class == handlerClazz) {
            CommandHandler handler = method.getAnnotation(CommandHandler.class);
            name = handler != null ? handler.name() : null;
        }
        if (QueryHandler.class == handlerClazz) {
            QueryHandler handler = method.getAnnotation(QueryHandler.class);
            name = handler != null ? handler.name() : null;
        }
        if (EventHandler.class == handlerClazz) {
            EventHandler handler = method.getAnnotation(EventHandler.class);
            name = handler != null ? handler.name() : null;
        }
        return name;
    }
}
