package com.cake.framework.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码定义
 * <p>
 * 原则：越靠近底层错误代码越大
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    /**
     * 业务错误 BusinessException
     */
    BIZ("cb-biz", "业务错误-错误区间[0-20000]"),
    /**
     * 外部错误 ManagerException
     */
    OUT("cb-out", "外部错误-错误区间[20000-39999]");

    private final String code;

    private final String desc;
}
