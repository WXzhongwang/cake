package com.cake.framework.log.rpc.client;

import com.cake.framework.log.appender.CakeLogbackFactory;
import com.cake.framework.log.rpc.Listener;
import com.cake.framework.log.rpc.RpcLogConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 23:12
 * @email 18668485565163.com
 */
@Data
@Slf4j
public class RpcClientLogConfig implements RpcLogConfig {

    private static RpcClientLogConfig clientLogConfig = new RpcClientLogConfig();

    private Listener listener;

    public RpcClientLogConfig() {
        listener = new RpcClientLogListener(this);
    }

    public static RpcClientLogConfig getInstance() {
        return clientLogConfig;
    }

    @Override
    public Logger getLogger() {
        return CakeLogbackFactory.getInstance().getRpcClientMonitor();
    }
}
