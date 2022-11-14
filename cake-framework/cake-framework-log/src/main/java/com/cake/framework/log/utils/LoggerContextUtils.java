package com.cake.framework.log.utils;

import com.cake.framework.log.annotation.LogEntry;
import com.cake.framework.log.annotation.LogPoint;
import com.cake.framework.log.core.LogContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/28 23:46
 * @email 18668485565163.com
 */
@Slf4j
public class LoggerContextUtils {

    public static LogContext build(Method method, Object object, Object[] args) {
        LogContext logContext = new LogContext();
        try {
            LogEntry logEntry = method.getAnnotation(LogEntry.class);
            LogPoint logPoint = method.getAnnotation(LogPoint.class);

            logContext.setLogEntry(logEntry);
            logContext.setLogPoint(logPoint);
            logContext.setMethod(method);
            logContext.setArgs(args);
            logContext.setObject(object);
        } catch (Throwable throwable) {
            log.error("LogIntercept error", throwable);
        }
        return logContext;
    }
}
