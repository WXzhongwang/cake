package com.cake.framework.log.rpc;

import com.alibaba.dubbo.rpc.Result;
import com.cake.framework.log.appender.CakeLogbackFactory;
import com.cake.framework.log.rpc.client.RpcClientLogConfig;
import com.cake.framework.log.rpc.formatter.LoggerFormatter;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;


/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 22:31
 * @email 18668485565163.com
 */
public class DefaultListener implements Listener {

    /**
     * rpc logger
     */
    public static final Logger logger = CakeLogbackFactory.getInstance().getRpcLogger();

    private Logger monitorLog;
    private RpcLogConfig rpcLogConfig;


    public DefaultListener(RpcLogConfig rpcLogConfig) {
        this.rpcLogConfig = rpcLogConfig;
        this.monitorLog = rpcLogConfig.getLogger();
    }

    @Override
    public void enterRpc(RpcContext rpcContext) {
        // do nothing
    }

    @Override
    public void onRpcError(RpcContext rpcContext, Throwable e) {
        String interfaceName = rpcContext.getInterfaceName();
        String methodName = rpcContext.getMethodName();
        Object[] params = rpcContext.getMethodArgs();
        Class<?>[] classes = rpcContext.getParamClasses();
        String paramClasses = "";
        if (classes != null && classes.length > 0) {
            paramClasses = Arrays.stream(rpcContext.getParamClasses()).map(Class::getSimpleName).collect(Collectors.joining(","));
        }
        logger.error("{}: {}#{}~{}, param:{}, error:{}", getTipMessage(), interfaceName,
                methodName, paramClasses, params, e.getClass().getName());
        LoggerFormatter msg = LoggerFormatter.normal(interfaceName, methodName, params);
        monitorLog.error(msg.error());
    }

    @Override
    public void existRpc(RpcResponseContext rpcResponseContext) {
        Result result = rpcResponseContext.getRpcResult();
        String interfaceName = rpcResponseContext.getInterfaceName();
        String methodName = rpcResponseContext.getMethodName();
        Object[] params = rpcResponseContext.getMethodArgs();
        Class<?>[] classes = rpcResponseContext.getParamClasses();
        String paramClasses = "";
        if (classes != null && classes.length > 0) {
            paramClasses = Arrays.stream(rpcResponseContext.getParamClasses()).map(Class::getSimpleName).collect(Collectors.joining(","));
        }
        LoggerFormatter msg = LoggerFormatter.normal(interfaceName, methodName, params);
        if (result.hasException()) {
            logger.error("{}:{}#{}~{}, params:{}, error:{}", getTipMessage(), interfaceName, methodName, paramClasses,
                    result.getException().getMessage(), result.getException());
            monitorLog.error(msg.toString());
            return;
        }
        monitorLog.warn(msg.success());
    }

    private String getTipMessage() {
        if (rpcLogConfig instanceof RpcClientLogConfig) {
            return "Client-side error";
        }
        return "Server-side error";
    }
}
