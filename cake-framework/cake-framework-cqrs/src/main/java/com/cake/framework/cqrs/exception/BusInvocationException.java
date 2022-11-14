package com.cake.framework.cqrs.exception;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 22:45
 * @email 18668485565163.com
 */
public class BusInvocationException extends RuntimeException {
    public BusInvocationException() {

    }

    public BusInvocationException(String message) {
        super(message);
    }


    public BusInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusInvocationException(Throwable cause) {
        super(cause);
    }

    public BusInvocationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
