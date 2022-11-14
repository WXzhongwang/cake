package com.cake.framework.fast.bizflow.base;

import lombok.Getter;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/9 22:44
 * @email 18668485565163.com
 */
@Getter
public class BizFlowException extends Exception {

    private final String code;
    private final String message;


    public BizFlowException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "BizFlowException{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
