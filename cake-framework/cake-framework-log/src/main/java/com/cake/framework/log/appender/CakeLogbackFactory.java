package com.cake.framework.log.appender;

import ch.qos.logback.core.util.FileSize;
import com.rany.cake.framework.common.utils.AppNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * CakeLogbackFactory
 *
 * @author zhongshengwang
 * @description CakeLogbackFactory
 * @date 2022/10/28 23:00
 * @email 18668485565163.com
 */
public class CakeLogbackFactory implements CakeLogFactory {

    /**
     * 初始化
     */
    private static volatile Boolean initialized = false;

    /**
     * 应用名称
     */
    private static final String APP_NAME = AppNameUtils.getAppName();

    /**
     * 文件大小
     */
    private static final FileSize FILE_SIZE = FileSize.valueOf("200mb");

    /**
     * 文件历史文件个数
     */
    private static final int MAX_HISTORY = 10;
    private static final String RPC = "rpc";
    private static final String RPC_CLIENT_MONITOR = "rpcClientMonitor";
    private static final String RPC_SERVER_MONITOR = "rpcServerMonitor";
    private static final String LOG_AGENT = "logAgent";
    private static final String USER_HOME = AppNameUtils.getUserHome();
    private static CakeLogbackFactory cakeLogbackFactory = new CakeLogbackFactory();

    /**
     * 维护单例
     *
     * @return
     */
    public static CakeLogbackFactory getInstance() {
        return cakeLogbackFactory;
    }

    private CakeLogbackFactory() {
        init();
    }

    private synchronized void init() {
        if (initialized) {
            return;
        }
        initLogAgentAppender();
        initRpcAppender();
        initRpcClientMonitorAppender();
        initRpcServerMonitorAppender();
        initialized = true;
    }

    public void initRpcClientMonitorAppender() {
        String fileName = USER_HOME + File.separator + APP_NAME + "/logs/rpc_client_monitor.log";
        String filePattern = fileName + ".%d{yyyy-MM-dd}.%i.log";
        String encodePattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n";
        CakeLogBackAppenderBuilder.initRollingFileAppender(RPC_CLIENT_MONITOR, fileName, filePattern, encodePattern, FILE_SIZE, MAX_HISTORY);
    }

    public void initRpcServerMonitorAppender() {
        String fileName = USER_HOME + File.separator + APP_NAME + "/logs/rpc_server_monitor.log";
        String filePattern = fileName + ".%d{yyyy-MM-dd}.%i.log";
        String encodePattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n";
        CakeLogBackAppenderBuilder.initRollingFileAppender(RPC_SERVER_MONITOR, fileName, filePattern, encodePattern, FILE_SIZE, MAX_HISTORY);
    }

    public void initRpcAppender() {
        String fileName = USER_HOME + File.separator + APP_NAME + "/logs/rpc.log";
        String filePattern = fileName + ".%d{yyyy-MM-dd}.%i.log";
        String encodePattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n";
        CakeLogBackAppenderBuilder.initRollingFileAppender(RPC, fileName, filePattern, encodePattern, FILE_SIZE, MAX_HISTORY);
    }

    public void initLogAgentAppender() {
        String fileName = USER_HOME + File.separator + APP_NAME + "/logs/log_agent.log";
        String filePattern = fileName + ".%d{yyyy-MM-dd}.%i.log";
        String encodePattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n";
        CakeLogBackAppenderBuilder.initRollingFileAppender(LOG_AGENT, fileName, filePattern, encodePattern, FILE_SIZE, MAX_HISTORY);
    }

    @Override
    public Logger getRpcClientMonitor() {
        return LoggerFactory.getLogger(RPC_CLIENT_MONITOR);
    }

    @Override
    public Logger getRpcServerMonitor() {
        return LoggerFactory.getLogger(RPC_SERVER_MONITOR);
    }

    @Override
    public Logger getRpcLogger() {
        return LoggerFactory.getLogger(RPC);
    }

    @Override
    public Logger getLogAgentLogger() {
        return LoggerFactory.getLogger(LOG_AGENT);
    }
}
