package com.cake.framework.log.annotation;

import com.cake.framework.log.appender.CakeLogbackFactory;
import com.cake.framework.log.core.LogContext;
import com.cake.framework.log.core.Trace;
import com.cake.framework.log.utils.LoggerContextUtils;
import net.bytebuddy.implementation.bind.annotation.*;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * CakeLogInterceptor
 *
 * @author zhongshengwang
 * @description CakeLogInterceptor
 * @date 2022/10/28 22:55
 * @email 18668485565163.com
 */
public class CakeLogInterceptor {
    private static final Logger logger = CakeLogbackFactory.getInstance().getLogAgentLogger();

    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<?> callable,
                                   @This Object object,
                                   @AllArguments Object[] args) throws Throwable {
        LogContext logContext = LoggerContextUtils.build(method, object, args);
        if (logContext.getLogPoint() != null) {
            Trace.startTrace(logContext);
        }
        try {
            Object value = callable.call();
            logContext.setResult(value);
            return value;
        } catch (Exception e) {
            logContext.setException(e);
            throw e;
        } finally {
            if (logContext.getLogPoint() != null) {
                try {
                    Trace.endTrace(logContext);
                } catch (Throwable e) {
                    logger.error("LogInterceptor trace end error", e);
                }
            }
        }
    }
}
