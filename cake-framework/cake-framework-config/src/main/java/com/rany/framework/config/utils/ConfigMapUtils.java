package com.rany.framework.config.utils;

import com.google.common.collect.Maps;
import com.rany.framework.config.ClientService;
import com.rany.framework.config.Config;
import com.rany.framework.config.Result;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 17:00
 * @slogon 找到银弹
 */
public class ConfigMapUtils {

    private static final Logger log = LoggerFactory.getLogger(ConfigMapUtils.class);

    public static final String SHAMAN_SECRET_NAME = "cake-secret";
    public static final String FIX_LABEL = "k8s-app";
    public static final String AK_SK_CONFIG_KEY = "cake.s.aliyun.aksk";
    private static final Logger LOG = LoggerFactory.getLogger(ConfigMapUtils.class);

    public static Map<String, String> readAppConfigMap(String namespace,
                                                       String appName) {
        try {
            CoreV1Api coreV1Api = new CoreV1Api(ClientService.getApiClientByEnv());
            // 获取要更新的 ConfigMap
            V1ConfigMap existingConfigMap = coreV1Api.readNamespacedConfigMap(appName, namespace, null);
            // 修改 ConfigMap 的数据
            log.info("ConfigMap get successfully.");
            return existingConfigMap.getData();
        } catch (ApiException e) {
            log.error("Failed to get ConfigMap.", e);
            if (e.getCode() == 404) {
                ConfigMapUtils.createConfigMap(namespace, appName, Maps.newConcurrentMap(), !appName.equals(SHAMAN_SECRET_NAME));
            }
            return Collections.emptyMap();
        }
    }

    public static void createConfigMap(String namespace,
                                       String appName, Map<String, String> configMapPair, boolean withFixedLabel) {
        try {
            CoreV1Api coreV1Api = new CoreV1Api(ClientService.getApiClientByEnv());

            V1ObjectMeta meta = new V1ObjectMeta();
            meta.setName(appName);
            Map<String, String> labels = Maps.newHashMapWithExpectedSize(1);
            if (withFixedLabel) {
                labels.put(FIX_LABEL, appName);
                meta.setLabels(labels);
            }

            // 创建 ConfigMap 对象
            V1ConfigMap configMap = new V1ConfigMap()
                    .apiVersion("v1")
                    .kind("ConfigMap")
                    .metadata(meta)
                    .data(configMapPair);
            // 添加需要的键值对
            // 创建 ConfigMap
            coreV1Api.createNamespacedConfigMap(namespace, configMap,
                    null, null, null, null);
            log.info("ConfigMap create successfully.");
        } catch (ApiException e) {
            log.error("Failed to create ConfigMap.", e);
        }
    }

    public static void setDateItemFromConfigMap(String namespace, String appName, Map<String, String> dataMap) {
        try {
            CoreV1Api coreV1Api = new CoreV1Api(ClientService.getApiClientByEnv());
            // 获取要更新的 ConfigMap
            V1ConfigMap existingConfigMap = coreV1Api.readNamespacedConfigMap(appName, namespace, null);
            // 修改 ConfigMap 的数据
            existingConfigMap.setData(dataMap);
            // 更新 ConfigMap
            coreV1Api.replaceNamespacedConfigMap(appName, namespace, existingConfigMap, null, null, null, null);
            log.info("ConfigMap update successfully.");
        } catch (ApiException e) {
            log.error("Failed to update ConfigMap.", e);
        }
    }

    public static Result<Map<String, Map<String, String>>> publishMultiple(Map<String, String> dataMap) {

        return updateConfigMapPair(Config.DEPLOY_APP, Config.DEPLOY_NAMESPACE, dataMap);

    }

    private static Result<Map<String, Map<String, String>>> updateConfigMapPair(String appName, String namespace, Map<String, String> dataMap) {
        for (String key : dataMap.keySet()) {
            if (key.startsWith("cake.")) {
                return Result.fail("\"cake.*\"配置为系统保留，请勿使用", 400);
            }
        }
        ConfigMapUtils.setDateItemFromConfigMap(namespace, appName, dataMap);
        return getConfigMapWithEnv(appName, namespace);
    }

    private static Map<String, String> getConfigMap(String appName, String namespace) {
        return ConfigMapUtils.readAppConfigMap(namespace, appName);
    }

    public static Result<Map<String, Map<String, String>>> getConfigMapWithEnv(String appName, String namespace) {
        Map<String, Map<String, String>> envConfigMap = Maps.newHashMap();
        setConfigMapWithEnv(appName, namespace, envConfigMap);
        return Result.success(envConfigMap);
    }

    private static void setConfigMapWithEnv(String appName, String namespace, Map<String, Map<String, String>> envConfigMap) {
        Map<String, String> map = getConfigMap(appName, namespace);
        envConfigMap.put(appName, map);
    }

    /**
     * 获取ak
     *
     * @return ak
     */
    public static String getAk() {
        return getConfigValue(AK_SK_CONFIG_KEY, s -> s.split(","))[0];
    }

    /**
     * 获取sk
     *
     * @return sk
     */
    public static String getSk() {
        return getConfigValue(AK_SK_CONFIG_KEY, s -> s.split(","))[1];
    }


    /**
     * 获取指定configKey的value值并自定义解析规则
     * 自定义解析规则
     *
     * @param configKey            configKey
     * @param valueResolveFunction configValue的解析规则
     * @param <R>                  解析结果的类型
     * @return 解析结果
     */
    public static <R> R getConfigValue(String configKey, Function<String, R> valueResolveFunction) {
        String configValue = Config.getData(configKey);
        return valueResolveFunction.apply(configValue);
    }

    /**
     * 第一次被调用时或者aksk变化时会回调function
     *
     * @param function function需要处理aksk变化：如生成新的OssClient实例，将原有的ossClient实例下线
     */
    public static void processAkSkChange(Consumer<String> function) {
        Config.addListener(AK_SK_CONFIG_KEY, akSk -> {
            LOG.info("检测到ak,sk变化");
            function.accept(akSk);
        });
    }
}
