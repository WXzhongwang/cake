package com.cake.framework.log.appender;

import org.slf4j.Logger;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/28 23:00
 * @email 18668485565163.com
 */
public interface CakeLogFactory {

    /**
     * 获取RPC CLIENT LOGGER 用户xflush接口成功率
     *
     * @return
     */
    Logger getRpcClientMonitor();

    /**
     * 获取RPC SERVER LOGGER 用户xflush接口成功率
     *
     * @return
     */
    Logger getRpcServerMonitor();

    /**
     * 获取RPC LOGGER 记录调用异常日志
     *
     * @return
     */
    Logger getRpcLogger();

    /**
     * 用户AgentLog LOGGER
     *
     * @return
     */
    Logger getLogAgentLogger();
}
