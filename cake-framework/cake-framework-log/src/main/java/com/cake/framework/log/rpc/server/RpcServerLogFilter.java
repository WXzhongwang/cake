package com.cake.framework.log.rpc.server;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.cake.framework.log.rpc.BaseRpcFilter;
import com.cake.framework.log.rpc.RpcLogConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 23:31
 * @email 18668485565163.com
 */
@Slf4j
@Activate(group = Constants.PROVIDER)
public class RpcServerLogFilter extends BaseRpcFilter {
    @Override
    public RpcLogConfig getRpcLogConfig() {
        return RpcServerLogConfig.getInstance();
    }
}
