package com.cake.framework.log.core;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/29 00:20
 * @email 18668485565163.com
 */
@Data
@Accessors(chain = true)
public class LogDO {
    private static final String SEPARATOR = "|";

    private String index;
    private String traceId;
    private String rpcId;
    private String flowType;
    private Long timestamp;
    private String bizDomain;
    private String bizScenario;
    private String title;
    private String bizType;
    private String className;
    private String methodName;
    private String params;
    private String content;
    private String ip;
    private String host;
    private Long cost;

    @Override
    public String toString() {
        return "LogDO{" +
                "index='" + index + '\'' +
                ", traceId='" + traceId + '\'' +
                ", rpcId='" + rpcId + '\'' +
                ", flowType='" + flowType + '\'' +
                ", timestamp=" + timestamp +
                ", bizDomain='" + bizDomain + '\'' +
                ", bizScenario='" + bizScenario + '\'' +
                ", title='" + title + '\'' +
                ", bizType='" + bizType + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", params='" + params + '\'' +
                ", content='" + content + '\'' +
                ", ip='" + ip + '\'' +
                ", host='" + host + '\'' +
                ", cost=" + cost +
                '}';
    }
}
