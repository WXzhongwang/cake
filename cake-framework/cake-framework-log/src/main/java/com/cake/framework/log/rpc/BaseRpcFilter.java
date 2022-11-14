package com.cake.framework.log.rpc;

import com.alibaba.dubbo.rpc.*;
import com.cake.framework.log.appender.CakeLogbackFactory;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 22:35
 * @email 18668485565163.com
 */
public abstract class BaseRpcFilter implements Filter {

    /**
     * rpc logger
     */
    public static final Logger logger = CakeLogbackFactory.getInstance().getRpcLogger();

    /**
     * 获取rpc日志配置
     *
     * @return
     */
    public abstract RpcLogConfig getRpcLogConfig();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Listener listener = getRpcLogConfig().getListener();
        RpcContext clientContext = null;
        if (null != listener) {
            try {
                clientContext = wrap(invocation);
                listener.enterRpc(clientContext);
            } catch (Throwable e) {
                logger.error("listener enter rpc error.", e);
            }
        }

        try {
            Result rpcResult = invoker.invoke(invocation);
            if (null != listener) {
                try {
                    RpcResponseContext responseContext = wrap(invocation, rpcResult, rpcResult.getValue());
                    listener.existRpc(responseContext);
                } catch (Throwable exit) {
                    logger.error("listener exit rpc error.", exit);
                }
            }
            return rpcResult;
        } catch (Throwable e) {
            if (null != listener && clientContext != null) {
                try {
                    listener.onRpcError(clientContext, e);
                } catch (Throwable ex) {
                    logger.error("listener execute rpc error.", ex);
                }
            }
            throw e;
        }
    }

    protected RpcContext wrap(Invocation invocation) {
        RpcContext clientContext = new RpcContext();
        wrapInternal(clientContext, invocation);
        return clientContext;
    }

    protected RpcResponseContext wrap(Invocation invocation, Result rpcResult, Object value) {
        RpcResponseContext responseContext = new RpcResponseContext();
        wrapInternal(responseContext, invocation);
        responseContext.setResponseValue(value);
        responseContext.setRpcResult(rpcResult);
        return responseContext;
    }

    protected void wrapInternal(RpcContext context, Invocation invocation) {
        Method method = null;
        try {
            method = invocation.getInvoker().getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            context.setInterfaceName(invocation.getInvoker().getInterface().getName());
            context.setMethod(method);
            context.setMethodName(invocation.getMethodName());
            context.setReturnType(method.getReturnType());
            context.setInvocation(invocation);
            context.setParamClasses(method.getParameterTypes());
            context.setMethodArgs(invocation.getArguments());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
