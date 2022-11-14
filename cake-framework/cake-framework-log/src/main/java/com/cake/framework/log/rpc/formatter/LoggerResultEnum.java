package com.cake.framework.log.rpc.formatter;

import lombok.Getter;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 23:46
 * @email 18668485565163.com
 */
@Getter
public enum LoggerResultEnum {

    SUCCESS("00", "成功"),
    ERROR("00", "调用失败");

    private final String code;
    private final String name;

    LoggerResultEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
