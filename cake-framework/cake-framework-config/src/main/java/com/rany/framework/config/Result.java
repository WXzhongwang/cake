package com.rany.framework.config;

import java.io.Serializable;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 16:59
 * @slogon 找到银弹
 */
public class Result<T> implements Serializable {

    public static final Result<Boolean> SUCCESS = new Result<>(true, "", 200, true);
    private static final long serialVersionUID = 5756492287408296791L;
    private  final  boolean success;
    private final  T data;
    private  final  int errorCode;
    private final String errorMessage;

    public <T> Result() {
        this.success = true;
        this.data = null;
        this.errorCode = 200;
        this.errorMessage = null;
    }

    public Result(T data) {
        super();
        this.success = true;
        this.data = data;
        this.errorCode = 200;
        this.errorMessage = null;
    }

    public Result(boolean success,String errorMessage, int errorCode, T data) {
        super();
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static  <T> Result<T> fail(String errorMessage, int errorCode) {
        return fail(errorMessage, errorCode, null);
    }


    public static <T> Result<T> fail(String errorMessage, int errorCode, T data) {
        return new Result<T>(false, errorMessage, errorCode, data);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static  Result<Boolean> buildSuccessResponse() {
       return SUCCESS;
    }
}
