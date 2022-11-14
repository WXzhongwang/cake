package com.cake.framework.log.rpc.client;

import com.cake.framework.log.core.Trace;
import com.cake.framework.log.rpc.DefaultListener;
import com.cake.framework.log.rpc.RpcContext;
import com.cake.framework.log.rpc.RpcLogConfig;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 23:15
 * @email 18668485565163.com
 */
public class RpcClientLogListener extends DefaultListener {

    public RpcClientLogListener(RpcLogConfig rpcLogConfig) {
        super(rpcLogConfig);
    }

    @Override
    public void enterRpc(RpcContext rpcContext) {
        Trace.startRpcTrace();
    }
}
