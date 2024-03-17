package com.cake.framework.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * 通用异常代码，此文件仅定义常用的，简单的代码
 *
 * @author zhongshengwang
 */

@Getter
@AllArgsConstructor
public enum CommonReturnCode implements ResponseCode{

    SUCCESS("200", "处理成功"),

    PARAMETER_MISSING("400", "参数缺失"),
    PARAMETER_ILLEGAL("400", "参数异常"),
    UIC_UN_LOGIN("401", "用户未登陆"),
    UIC_NOT_ENABLE("402", "账号已停用"),
    NOT_AUTHORIZED("403", "未权限"),
    NOT_FOUND("404", "未找到"),

    TIMEOUT("408", "请求超时"),
    METHOD_NOT_ALLOWED("405", "方法调用不被允许"),
    SYSTEM_ERROR("500", "系统异常"),

    OPERATION_ILLEGAL("500", "非法操作");

    private final String code;

    /**
     * 错误信息
     */

    private final String message;

    @Override
    public String getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMessage() {
        return this.message;
    }
}
