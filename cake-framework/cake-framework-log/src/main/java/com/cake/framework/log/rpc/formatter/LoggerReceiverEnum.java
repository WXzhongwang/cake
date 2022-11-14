package com.cake.framework.log.rpc.formatter;

import lombok.Getter;

/**
 * LoggerReceiverEnum
 *
 * @author zhongshengwang
 * @description LoggerReceiverEnum
 * @date 2022/11/11 23:46
 * @email 18668485565163.com
 */
@Getter
public enum LoggerReceiverEnum {

    GOC("01", "上报GOC"),
    NORMAL("00", "普通监控");

    private final String code;
    private final String name;

    LoggerReceiverEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
