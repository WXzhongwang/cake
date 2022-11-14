package com.cake.framework.fast.cqrs.processor;

import com.cake.framework.cqrs.base.CommandHandler;
import com.cake.framework.cqrs.base.EventHandler;
import com.cake.framework.cqrs.base.QueryHandler;
import com.cake.framework.cqrs.bus.CommandBus;
import com.cake.framework.cqrs.bus.EventBus;
import com.cake.framework.cqrs.bus.QueryBus;
import com.cake.framework.cqrs.utis.ReflectionUtils;
import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * BusRegisterProcessor
 *
 * @author zhongshengwang
 * @description BusRegisterProcessor
 * @date 2022/11/2 22:16
 * @email 18668485565163.com
 */
public class BusRegisterProcessor implements BeanPostProcessor {
    private final EventBus eventBus;
    private final QueryBus queryBus;
    private final CommandBus commandBus;

    public BusRegisterProcessor(EventBus eventBus, QueryBus queryBus, CommandBus commandBus) {
        this.eventBus = eventBus;
        this.queryBus = queryBus;
        this.commandBus = commandBus;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        registerToBus(bean);
        return bean;
    }

    private void registerToBus(Object bean) {
        List<Method> methodList = ReflectionUtils.getMethodList(bean);
        if (methodList.isEmpty()) {
            return;
        }
        int eventCount = 0, commandCount = 0, queryCount = 0;
        for (Method method : methodList) {
            boolean eventHandler = AnnotationUtils.findAnnotation(method, EventHandler.class) != null;
            boolean queryHandler = AnnotationUtils.findAnnotation(method, QueryHandler.class) != null;
            boolean commandHandler = AnnotationUtils.findAnnotation(method, CommandHandler.class) != null;
            eventCount = eventHandler ? eventCount + 1 : eventCount;
            queryCount = queryHandler ? queryCount + 1 : queryCount;
            commandCount = commandHandler ? commandCount + 1 : commandCount;
        }

        if (eventCount > 0) {
            eventBus.register(getTargetObject(bean));
        }
        if (queryCount > 0) {
            queryBus.register(getTargetObject(bean));
        }
        if (commandCount > 0) {
            commandBus.register(getTargetObject(bean));
        }
    }

    private static Object getTargetObject(Object bean) {
        String name = bean.getClass().getName();
        if (name.toLowerCase().contains("cglib")) {
            return extractTargetObject(bean);
        }
        return bean;
    }

    private static Object extractTargetObject(Object proxyBean) {
        try {
            return findSpringTargetSource(proxyBean).getTarget();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static TargetSource findSpringTargetSource(Object proxyBean) {
        Method[] declaredMethods = proxyBean.getClass().getDeclaredMethods();
        Method target = findTargetMethod(declaredMethods);
        target.setAccessible(true);
        try {
            return (TargetSource) target.invoke(proxyBean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method findTargetMethod(Method[] declaredMethods) {
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.getName().endsWith("getTargetSource")) {
                return declaredMethod;
            }
        }
        throw new IllegalStateException("cannot find target method on spring managed object");
    }
}
