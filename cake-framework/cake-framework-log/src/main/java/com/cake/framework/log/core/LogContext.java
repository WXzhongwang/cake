package com.cake.framework.log.core;

import com.cake.framework.log.annotation.LogEntry;
import com.cake.framework.log.annotation.LogPoint;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * LogContext
 *
 * @author zhongshengwang
 * @description LogContext
 * @date 2022/10/28 23:48
 * @email 18668485565163.com
 */

@Getter
@Setter
public class LogContext {

    private long start = System.currentTimeMillis();

    private Method method;

    private LogPoint logPoint;

    private LogEntry logEntry;

    private Object[] args;

    private Object result;

    private Object object;

    private Throwable exception;

    public LogContext() {
    }
}
