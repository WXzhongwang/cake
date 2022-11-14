package com.cake.framework.log.rpc;

import org.slf4j.Logger;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 22:28
 * @email 18668485565163.com
 */
public interface RpcLogConfig {

    /**
     * 获取监听器
     *
     * @return
     */
    Listener getListener();


    /**
     * 获取日志配置
     *
     * @return
     */
    Logger getLogger();
}
