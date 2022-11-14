package com.cake.framework.log.rpc.formatter;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 23:44
 * @email 18668485565163.com
 */
@Data
@Builder
public class LoggerFormatter {

    private static final String SYMBOL_SPLIT = "|";
    private static final String SYMBOL_EMPTY = "_";

    private String traceId;
    
    private String rpcType;
    private String logReceiver;
    private String serviceName;
    private String methodName;
    private String resultCode;
    private Long start;
    private List<Object> parameters;

    public static LoggerFormatter normal(String interfaceName, String methodName, Object... params) {
        return LoggerFormatter.builder().logReceiver(LoggerReceiverEnum.NORMAL.getCode()).serviceName(interfaceName)
                .methodName(methodName)
                .start(System.currentTimeMillis())
                .parameters(Arrays.asList(params))
                .build();
    }

    public static LoggerFormatter goc(String interfaceName, String methodName, Object... params) {
        return LoggerFormatter.builder().logReceiver(LoggerReceiverEnum.GOC.getCode()).serviceName(interfaceName)
                .methodName(methodName)
                .start(System.currentTimeMillis())
                .parameters(Arrays.asList(params))
                .build();
    }

    public String error() {
        this.setResultCode(LoggerResultEnum.ERROR.getCode());
        return this.toString();
    }

    public String success() {
        this.setResultCode(LoggerResultEnum.SUCCESS.getCode());
        return this.toString();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append(traceId != null ? traceId : SYMBOL_EMPTY).append(SYMBOL_SPLIT)
                .append("RPC").append(SYMBOL_SPLIT)
                .append(logReceiver != null ? logReceiver : LoggerReceiverEnum.NORMAL.getCode()).append(SYMBOL_SPLIT)
                .append(serviceName != null ? serviceName : SYMBOL_EMPTY).append(SYMBOL_SPLIT)
                .append(methodName != null ? serviceName : SYMBOL_EMPTY).append(SYMBOL_SPLIT)
                .append(resultCode != null ? resultCode : LoggerResultEnum.SUCCESS.getCode()).append(SYMBOL_SPLIT)
                .append(start != null ? start : SYMBOL_EMPTY).append(SYMBOL_SPLIT)
                .append(parameters != null ? parameters : SYMBOL_EMPTY).append(SYMBOL_SPLIT)
                .append(System.currentTimeMillis() - start)
                .toString();
    }
}
