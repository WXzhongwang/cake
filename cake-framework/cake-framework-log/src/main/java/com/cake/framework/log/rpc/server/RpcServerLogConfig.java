package com.cake.framework.log.rpc.server;

import com.cake.framework.log.appender.CakeLogbackFactory;
import com.cake.framework.log.rpc.DefaultListener;
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
public class RpcServerLogConfig implements RpcLogConfig {
    private static RpcServerLogConfig serverLogConfig = new RpcServerLogConfig();
    private Listener listener;

    public RpcServerLogConfig() {
        listener = new DefaultListener(this);
    }

    public static RpcServerLogConfig getInstance() {
        return serverLogConfig;
    }

    @Override
    public Logger getLogger() {
        return CakeLogbackFactory.getInstance().getRpcServerMonitor();
    }
}
