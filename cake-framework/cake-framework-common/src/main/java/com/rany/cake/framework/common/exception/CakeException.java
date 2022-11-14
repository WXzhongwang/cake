package com.rany.cake.framework.common.exception;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 22:53
 * @email 18668485565163.com
 */
public class CakeException extends RuntimeException {

    public CakeException() {
    }

    public CakeException(String message) {
        super(message);
    }

    public CakeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CakeException(Throwable cause) {
        super(cause);
    }

    public CakeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
