package com.rany.framework.config.utils;


import com.rany.framework.config.ClientService;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 17:04
 * @slogon 找到银弹
 */
public class K8sApiUtils {

    public static CoreV1Api getCoreApi() {
        return new CoreV1Api(ClientService.getApiClientByEnv());
    }

    public static AppsV1Api getAppApi() {
        return new AppsV1Api(ClientService.getApiClientByEnv());
    }
}
