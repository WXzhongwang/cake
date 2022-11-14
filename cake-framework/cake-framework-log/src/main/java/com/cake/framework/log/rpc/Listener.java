package com.cake.framework.log.rpc;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 22:18
 * @email 18668485565163.com
 */
public interface Listener {

    /**
     * 进入RPC
     *
     * @param rpcContext
     */
    void enterRpc(RpcContext rpcContext);

    /**
     * RPC异常
     *
     * @param rpcContext
     * @param e
     */
    void onRpcError(RpcContext rpcContext, Throwable e);

    /**
     * 退出RPC
     *
     * @param rpcResponseContext
     */
    void existRpc(RpcResponseContext rpcResponseContext);
}
