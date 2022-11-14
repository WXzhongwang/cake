package com.cake.framework.cqrs.utis;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 23:13
 * @email 18668485565163.com
 */
public final class ReflectionUtils {

    ReflectionUtils() {}

    /**
     * 获取当前类与父类的所有方法
     * @param object
     * @return
     */
    public static List<Method> getMethodList(Object object) {
        if (object == null) {
            return new ArrayList<>();
        }

        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Method[] methods = clazz.getMethods();
                return Arrays.asList(methods);
            } catch (Exception ex) {
                throw new RuntimeException("BusRegistryProcessor.getMethodList occur error, object is" + object);
            }
        }
        return new ArrayList<>();
    }
}
