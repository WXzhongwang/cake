package com.rany.framework.config;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rany.framework.config.utils.CakePlaceholderHelper;
import com.rany.framework.config.utils.ConfigMapUtils;
import com.rany.framework.config.utils.HttpUtils;
import com.rany.framework.config.utils.XorUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 16:39
 * @slogon 找到银弹
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final List<String> STATIC_YAML_NAMES = Lists.newArrayList("static.yaml", "static.yml");
    private static final String STATIC_FILENAME = "static.properties";
    private static final String DATA_DIR = "..data";
    private static final String CAKE_SECRET_PREFIX = "_cake.";
    private static final String CAKE_SECRET_NAME = "cake-secret";
    private static final Map<String, List<ConfigListener>> listeners = Maps.newConcurrentMap();
    private static final Properties staticPros = new Properties();
    private static final ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("cake-config-%d").daemon(true).build());
    private static final Map<String, String> HEADERS = new HashMap<>();
    private static final Map<String, String> fileContentMD5Map = Maps.newConcurrentMap();
    /**
     * 部署应用名，通过环境变量 'DEPLOY_APP' 获取
     */
    public static String DEPLOY_APP;
    /**
     * 部署应用的namespace，通过环境变量 'DEPLOY_NAMESPACE' 获取
     */
    public static String DEPLOY_NAMESPACE;
    private static File FILE_PATH;
    private static volatile JSONObject remoteConfigJSONObject;
    private static volatile JSONObject remoteSecretJSONObject;

    public static void init(String deployApp) {
        HEADERS.put("x-api-version", "1");
        // 先获取环境变量 DEPLOY_APP，获取不到抛异常
        DEPLOY_APP = StringUtils.isBlank(deployApp) ? System.getenv("DEPLOY_APP") : deployApp;
        DEPLOY_NAMESPACE = System.getenv("DEPLOY_NAMESPACE");
        if (null == DEPLOY_APP) {
            throw new RuntimeException("environment variable 'DEPLOY_APP' missing!");
        }

        if (null == DEPLOY_NAMESPACE) {
            logger.info("environment variable 'DEPLOY_NAMESPACE' missing! Please refer to http://test.devops.rany.com/");
        }


        if (isLocalEnv()) {
            FILE_PATH = new File("cake-config/");
            if (!FILE_PATH.exists()) {
                try {
                    FILE_PATH = new ClassPathResource("cake-config").getFile();
                } catch (IOException ignored) {

                }
            }
            // fixed 10 seconds
            fixedReloadCakeConfigRemoteInLocalEnv();
            schedule.scheduleAtFixedRate(Config::fixedReloadCakeConfigRemoteInLocalEnv, 10, 10, TimeUnit.SECONDS);
        } else {
            FILE_PATH = new File("/etc/cake/");
        }

        // 还在static.properties中读取配置
        String data = loadConfigItem(STATIC_FILENAME);
        parseProperties(STATIC_FILENAME, data);
        // 同时增加yaml
        for (String yamlFileName : STATIC_YAML_NAMES) {
            data = loadConfigItem(yamlFileName);
            parseProperties(yamlFileName, data);
        }

        for (Map.Entry<Object, Object> p : staticPros.entrySet()) {
            String value = CakePlaceholderHelper.resolvePlaceholder((String) p.getValue(), null);
            System.setProperty((String) p.getKey(), value);
        }
        try {
            reloadCakeConfigMap();
        } catch (NoSuchAlgorithmException | SecretNotFoundException e) {
            throw new RuntimeException(e);
        }

        // watch file event key, thread poll loop
        start();
    }
    private static void parseProperties(String fileName, String data) {
        if (StringUtils.isNotBlank(data)) {
            Properties properties = new Properties();
            if (StringUtils.isNotBlank(data)) {
                if (fileName.endsWith(".properties")) {
                    try {
                        properties.load(new StringReader(data));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Yaml yaml = new Yaml();
                    Map<String, String> map = getFlattenMap(yaml.loadAs(new StringReader(data), Map.class), "");
                    properties.putAll(map);
                }
            }
            staticPros.putAll(properties);
        }
    }
    private static Map<String, String> getFlattenMap(Map<Object, Object> properties, String prefix) {
        Map<String, String> fProc = new HashMap<>();
        for (Map.Entry<Object, Object> p : properties.entrySet()) {
            String k = "".equals(prefix) ? p.getKey().toString() : prefix + "." + p.getKey().toString();
            if (p.getValue() instanceof Map) {
                Map<String, String> valueMap = getFlattenMap((Map) p.getValue(), k);
                for (Map.Entry<String, String> e : valueMap.entrySet()) {
                    fProc.put(e.getKey(), e.getValue());
                }
            } else {
                fProc.put(k, p.getValue().toString());
            }
        }
        return fProc;
    }
    private static void fixedReloadCakeConfigRemoteInLocalEnv() {
        remoteConfigJSONObject = loadConfigItemsFromRemote(false);
        remoteSecretJSONObject = loadConfigItemsFromRemote(true);
    }

    private static boolean isLocalEnv() {
        return SystemUtils.IS_OS_MAC_OSX || SystemUtils.IS_OS_WINDOWS || "LOCAL".equals(System.getenv("DEPLOY_CONFIG_ENV"));
    }

    private static Map<String, String> loadSecretItemMap() {
        Map<String, String> itemMap = new HashMap<>();
        // 如果是本地的，则从remote获取config secret
        if (isLocalEnv()) {
            if (remoteSecretJSONObject == null) {
                return itemMap;
            }
            for (Map.Entry<String, Object> entry : remoteSecretJSONObject.entrySet()) {
                itemMap.put(entry.getKey(), ((String) entry.getValue()).trim());
            }
        } else {
            // 如果是K8S集群的，则从指定容器目录获取config secret
            File secretDir = new File("/etc/" + CAKE_SECRET_NAME);
            if (!secretDir.isDirectory() || secretDir.listFiles() == null) {
                return itemMap;
            }
            for (File c : Objects.requireNonNull(secretDir.listFiles())) {
                if (!c.isFile()) {
                    continue;
                }
                itemMap.put(c.getName(), loadFileFromLocal(c));
            }
        }
        return itemMap;
    }

    private static String loadSecretItem(String secretKey) {
        // 如果是本地的，则从remote获取config secret
        if (isLocalEnv()) {
            if (remoteSecretJSONObject == null) {
                return null;
            }
            return remoteSecretJSONObject.getString(secretKey);
        } else {
            // 如果是K8S集群的，则从指定容器目录获取config secret
            File secretFile = new File("/etc/" + CAKE_SECRET_NAME, secretKey);
            return loadFileFromLocal(secretFile);
        }
    }

    private static Map<String, String> loadConfigItemMap() {
        JSONObject remote = null;
        Map<String, String> configItemMap = new HashMap<>();
        if (isLocalEnv()) {
            remote = loadConfigItemsFromRemote(false);
            if (remote != null) {
                for (Map.Entry<String, Object> entry : remote.entrySet()) {
                    configItemMap.put(entry.getKey(), (String) entry.getValue());
                }
            }
        }

        assert FILE_PATH != null;
        File[] configFiles = FILE_PATH.listFiles();
        if (!FILE_PATH.isDirectory() || configFiles == null) {
            // remote不存在，FILE_PATH也没有，不执行
            if (remote == null) {
                return null;
            }
        } else {
            // configFileMap和remote合并
            for (File c : configFiles) {
                if (!c.isFile()) {
                    continue;
                }
                String content = loadFileFromLocal(c);
                if (content != null) {
                    configItemMap.put(c.getName(), content);
                } else {
                    logger.info(c.getName() + ":[MISSING]");
                }
            }
        }

        return configItemMap;
    }

    private static void reloadCakeConfigMap() throws NoSuchAlgorithmException, SecretNotFoundException {

        logger.info("====configMapLoad====");

        Map<String, String> configItemMap = loadConfigItemMap();
        if (configItemMap == null) {
            return;
        } else {
            // 先删除 静态配置文件
            configItemMap.remove(STATIC_FILENAME);
            for (String y : STATIC_YAML_NAMES) {
                configItemMap.remove(y);
            }
        }

        // 先加载cake.c
        List<Map.Entry<String, String>> secretDataIdList = new ArrayList<>(4);
        for (Map.Entry<String, String> entry : configItemMap.entrySet()) {
            String dataId = entry.getKey();
            String content = entry.getValue();
            if (content.startsWith(CAKE_SECRET_PREFIX)) {
                // 对于cake.s的dataId，放到下一步notify
                secretDataIdList.add(entry);
            } else {
                notifyListener(dataId, content, content);
            }
        }

        // 再加载cake.s
        Map<String, String> secretItemMap = loadSecretItemMap();
        for (Map.Entry<String, String> entry : secretDataIdList) {
            String dataId = entry.getKey();
            String content = entry.getValue();
            String secret = secretItemMap.get(content);
            int waitSecond = 4;
            int timeout = 600;
            // secret一直从/etc/cake-secret获取内容，一直等到超时，累计等600秒
            while (null == secret && timeout > 0) {
                try {
                    TimeUnit.SECONDS.sleep(waitSecond);
                } catch (InterruptedException ignored) {
                }
                secret = loadSecretItem(content);
                if (null != secret) {
                    secretItemMap.put(content, secret);
                }
                timeout -= waitSecond;
                waitSecond = Math.min(waitSecond << 1, 16);
            }
            if (secret == null) {
                // 直到超时也没有获取到
                logger.error(content + ":[NOT FOUND]");
                throw new SecretNotFoundException();
            } else {
                content = new String(Base64.getDecoder().decode(secret), StandardCharsets.UTF_8);
            }
            int length = content.length();
            String masked = "****";
            if (content.length() > 4) {
                masked = content.substring(0, length / 3) + "****" + content.substring(length * 2 / 3);
            }
            notifyListener(dataId, content, masked);
        }
    }

    private static void notifyListener(String dataId, String content, String masked) throws NoSuchAlgorithmException {
        String md5 = MD5Helper.md5(content);
        if (!md5.equals(fileContentMD5Map.get(dataId))) {
            logger.info(dataId + ":" + masked);
            fileContentMD5Map.put(dataId, md5);

            List<ConfigListener> configListeners = listeners.get(dataId);
            if (configListeners != null) {
                for (ConfigListener configListener : configListeners) {
                    configListener.process(resolveContent(content));
                }
            }
        }
    }

    /**
     * 先从本地loadFile，如果本地load不到Config，则再从remote读取(本地环境)
     * 如果以 _cake. 开头的，则从secret上去加载
     */
    private static String loadConfigItem(String fileName) {
        File c = FileUtils.getFile(FILE_PATH, fileName);
        String fileContent = loadFileFromLocal(c);

        if (null == fileContent) {
            if (isLocalEnv()) {
                fileContent = remoteConfigJSONObject == null ? null : remoteConfigJSONObject.getString(fileName);
            }
            // placeholder maybe fileName in properties
            if (null == fileContent && fileName.startsWith(CAKE_SECRET_PREFIX)) {
                String secret = loadSecretItem(fileName);
                if (secret != null) {
                    fileContent = new String(Base64.getDecoder().decode(secret), StandardCharsets.UTF_8);
                }
            }
        }
        return fileContent;
    }

    /**
     * 从remote读取配置
     */
    private static JSONObject loadConfigItemsFromRemote(Boolean isSecret) {

//        String endpoint = "http://test.devops.rany.com";
        String endpoint = "http://127.0.0.1:8300";
        String path;
        if (isSecret) {
            path = "/api/config/secret/items?env=TEST&appName=" + DEPLOY_APP;
        } else {
            path = "/api/config/items?env=TEST&appName=" + DEPLOY_APP;
        }

        String response = HttpUtils.doGet(endpoint + path, HEADERS);
        if (response == null) {
            return null;
        }

        JSONObject result = JSONObject.parseObject(XorUtil.decrypt(response));
        if (result == null) {
            return null;
        }
        if (!result.getBoolean("success")) {
            logger.error(result.getString("content"));
            return null;
        }
        return result.getJSONObject("content");
    }

    /**
     * 本地读取文件
     */
    private static String loadFileFromLocal(File file) {
        if (file == null || !file.isFile()) {
            return null;
        }
        try {
            return StringUtils.trim(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return null;
        }
    }

    private static void start() {
        new Thread(() -> {
            while (true) {
                try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
                    Path path = Paths.get(FILE_PATH.toURI());
                    path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_CREATE);
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            final Path changed = (Path) event.context();
                            String changeFileName = changed.toString();

                            if (changeFileName.equals(DATA_DIR) || isLocalEnv()) {
                                reloadCakeConfigMap();
                            }
                        }
                        key.reset();
                    }
                } catch (IOException | NoSuchAlgorithmException | InterruptedException | SecretNotFoundException e) {
                    // e.printStackTrace();
                    logger.warn("监听配置目录变更发生错误", e);
                } finally {
                    // sleep 5 seconds if it had exception
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException ignore) {

                    }
                }
            }

        }).start();
    }


    public static String getData(String configName) {

        String content = StringUtils.trim(loadConfigItem(configName));
        return resolveContent(content);
    }

    public static String resolveContent(String content) {
        if (null != content && content.startsWith(CAKE_SECRET_PREFIX)) {
            String secret = loadSecretItem(content);
            if (secret == null) {
                logger.error(content + ":[SECRET NOT FOUND]");
                return null;
            } else {
                return new String(Base64.getDecoder().decode(secret), StandardCharsets.UTF_8);
            }
        } else {
            return CakePlaceholderHelper.resolvePlaceholder(content, null);
        }
    }

    public static Properties getStaticMapData() {
        return staticPros;
    }

    public static synchronized void addListener(String configName, ConfigListener configListener) {
        List<ConfigListener> configListeners = listeners.computeIfAbsent(configName,
                k -> Lists.newCopyOnWriteArrayList());
        configListeners.add(configListener);
        // addListener should call process firstly
        configListener.process(getData(configName));
    }

    public static Result<Map<String, Map<String, String>>> publishSingle(String dataId, String content) {
        if (null == DEPLOY_NAMESPACE) {
            return Result.fail("DEPLOY_NAMESPACE NULL", 400);
        }
        if (dataId == null || content == null) {
            return Result.fail("params is null", 400);
        }
        Map<String, String> data = Maps.newHashMapWithExpectedSize(1);
        data.put(dataId, content);
        return ConfigMapUtils.publishMultiple(data);
    }

    public static Result<Map<String, Map<String, String>>> publishMultiple(Map<String, String> configPairs) {
        if (null == DEPLOY_NAMESPACE) {
            return Result.fail("DEPLOY_NAMESPACE NULL", 400);
        }

        if (configPairs == null || configPairs.isEmpty()) {
            return Result.fail("params is null", 400);
        }
        return ConfigMapUtils.publishMultiple(configPairs);
    }

    public static class MD5Helper {
        /**
         * get md5 digest
         */
        static String md5(String content) throws NoSuchAlgorithmException {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(content.getBytes(StandardCharsets.UTF_8));
            return byteToHex(bytes);
        }


        /**
         * Byte to Hex
         */
        private static String byteToHex(byte[] bytes) {
            StringBuilder md5str = new StringBuilder();
            int digital;
            for (byte aByte : bytes) {
                digital = aByte;
                if (digital < 0) {
                    digital += 256;
                }
                if (digital < 16) {
                    md5str.append("0");
                }
                md5str.append(Integer.toHexString(digital));
            }
            return md5str.toString().toUpperCase();
        }
    }
}
