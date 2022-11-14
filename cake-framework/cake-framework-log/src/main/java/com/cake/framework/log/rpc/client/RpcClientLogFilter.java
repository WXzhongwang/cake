package com.cake.framework.log.rpc.client;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.cake.framework.log.rpc.BaseRpcFilter;
import com.cake.framework.log.rpc.RpcLogConfig;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 23:31
 * @email 18668485565163.com
 */
@Activate(group = Constants.CONSUMER)
public class RpcClientLogFilter extends BaseRpcFilter {
    @Override
    public RpcLogConfig getRpcLogConfig() {
        return RpcClientLogConfig.getInstance();
    }
}
