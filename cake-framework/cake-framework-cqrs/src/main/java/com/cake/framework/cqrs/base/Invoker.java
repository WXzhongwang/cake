package com.cake.framework.cqrs.base;

import com.cake.framework.cqrs.exception.BusInvocationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Invoker
 *
 * @author zhongshengwang
 * @description Invoker
 * @date 2022/10/26 22:37
 * @email 18668485565163.com
 */
public class Invoker {

    /**
     * 监听器
     */
    private final Object listener;
    /**
     * 方法名称
     */
    private final Method method;
    /**
     * 名称
     */
    private final String name;

    /**
     * 构造方法
     *
     * @param listener
     * @param method
     * @param name
     */
    public Invoker(Object listener, Method method, String name) {
        this.listener = listener;
        this.method = method;
        this.name = name;
        method.setAccessible(true);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listener, method);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Invoker)) {
            return false;
        }
        Invoker exe = (Invoker) obj;
        return Objects.equals(listener, exe.listener) && Objects.equals(method, exe.method);
    }

    @Override
    public String toString() {
        return "Invoker{" +
                "listener=" + listener +
                ", method=" + method +
                ", name='" + name + '\'' +
                '}';
    }


    /**
     * 事件处理
     *
     * @param parameter 参数
     * @return
     */
    public Object handleMethod(Object parameter) {
        try {
            return method.invoke(listener, parameter);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) (e.getCause());
            } else {
                throw new BusInvocationException(e);
            }
        } catch (Exception ex) {
            throw new BusInvocationException(ex);
        }
    }
}
