package com.cake.framework.log.core;

import com.cake.framework.log.agent.CakeLogAgentProperties;
import com.cake.framework.log.annotation.LogPoint;
import com.cake.framework.log.utils.HostUtils;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/29 00:25
 * @email 18668485565163.com
 */
public class LogBuilder {

    public static LogDO build(LogContext logContext, Node node) {
        LogPoint logPoint = logContext.getLogPoint();
        LogDO builder = new LogDO()
                .setIndex(node.getPath())
                .setTitle(logPoint.title() != null ? logPoint.title() : logContext.getMethod().getName())
                .setBizDomain(CakeLogAgentProperties.getBizDomain())
                .setBizScenario(logPoint.bizScenario())
                .setTimestamp(System.currentTimeMillis())
                .setHost(HostUtils.host)
                .setIp(HostUtils.ip)
                .setClassName(logContext.getObject().getClass().getName())
                .setMethodName(logContext.getMethod().getName())
                .setParams(buildParam(logContext))
                .setTraceId("")
                .setContent(String.valueOf(logContext.getResult()))
                .setRpcId("")
                .setCost(System.currentTimeMillis() - logContext.getStart());
        return builder;
    }

    private static String buildParam(LogContext logContext) {
        Object[] args = logContext.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("[");
        for (Object arg : args) {
            sb.append(arg).append(",");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("]");
        return sb.toString();
    }
}
