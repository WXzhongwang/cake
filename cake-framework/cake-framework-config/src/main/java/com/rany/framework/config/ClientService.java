package com.rany.framework.config;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 16:36
 * @slogon 找到银弹
 */
public class ClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    private static ApiClient client;

    static {
        try {
            client = Config.fromCluster();
        } catch (IOException e) {
            logger.error("获取k8s配置失败", e);
        }
    }

    public static ApiClient getApiClientByEnv() {
        return client;
    }
}
