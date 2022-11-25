package com.cake.framework.fast.bizflow.report;

import com.alibaba.fastjson.JSON;
import com.cake.framework.cqrs.base.CommandHandler;
import com.cake.framework.cqrs.base.EventHandler;
import com.cake.framework.cqrs.base.QueryHandler;
import com.cake.framework.cqrs.utis.ReflectionUtils;
import com.cake.framework.fast.bizflow.BizFlowHub;
import com.cake.framework.fast.bizflow.ReporterProperties;
import com.cake.framework.fast.bizflow.base.Flow;
import com.cake.framework.fast.bizflow.base.Node;
import com.cake.framework.common.utils.AppNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.java.message.MessageBuilderImpl;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Reporter
 *
 * @author zhongshengwang
 * @description Reporter
 * @date 2022/11/10 22:27
 * @email 18668485565163.com
 */
@Slf4j
public class Reporter {
    private final Producer producer;
    private final ReporterProperties reporterProperties;

    public Reporter(Producer producer, ReporterProperties reporterProperties) {
        this.producer = producer;
        this.reporterProperties = reporterProperties;
    }

    private static final String _BIZ_FLOW_ = "bizflow";
    private static final String _HANDLER_ = "handler";

    /**
     * 上报
     *
     * @param hub
     * @param beanWithFlowModule
     */
    public void report(BizFlowHub hub, Map<String, Object> beanWithFlowModule) {
        final String appName = AppNameUtils.getAppName();
        if (Objects.isNull(hub) || Objects.isNull(hub.getFlowMap())) {
            return;
        }
        Map<String, List<Flow>> appFlowMap = convert(hub);
        List<HandlerInfo> handlers = parseHandlers(beanWithFlowModule);
        long current = System.currentTimeMillis();
        for (Map.Entry<String, List<Flow>> appFlows : appFlowMap.entrySet()) {
            // 当前app下的所有flow逐个上报
            for (Flow flow : appFlows.getValue()) {
                // 上报flow
                sendMessage(appName, flow.getClassName(), appFlows.getKey(), flow, flow.getVersion(), _BIZ_FLOW_, current);
                // 上报handler
                reportHandler(appName, handlers, flow, current);
            }
        }
    }

    private Map<String, List<Flow>> convert(BizFlowHub hub) {
        Map<String, List<Flow>> map = new ConcurrentHashMap<>();
        for (Map.Entry<String, List<Flow>> entry : hub.getFlowMap().entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private List<HandlerInfo> parseHandlers(Map<String, Object> beanWithFlowModule) {
        List<HandlerInfo> handlerInfos = new ArrayList<>();
        beanWithFlowModule.forEach((beanName, bean) -> {
            List<Method> methodList = ReflectionUtils.getMethodList(bean);
            if (methodList.isEmpty()) {
                return;
            }
            for (Method method : methodList) {
                final String handlerName = getHandlerValue(method);
                final String handlerType = getHandlerType(method);
                if (StringUtils.isEmpty(handlerName)) {
                    continue;
                }
                HandlerInfo handlerInfo = new HandlerInfo();
                handlerInfo.setModule(bean.getClass().getName());
                handlerInfo.setHandlerName(handlerName);
                final String typeName = method.getGenericReturnType().getTypeName();
                handlerInfo.setReturnType(typeName);
                handlerInfo.setMethod(method.getName());
                handlerInfo.setParamType(method.getParameterTypes()[0].getName());
                handlerInfo.setHandlerType(handlerType);
                handlerInfos.add(handlerInfo);
            }
        });
        return handlerInfos;
    }

    private String getHandlerType(Method method) {
        if (method.isAnnotationPresent(CommandHandler.class)) {
            CommandHandler commandHandler = method.getAnnotation(CommandHandler.class);
            return commandHandler.annotationType().getSimpleName();
        }
        if (method.isAnnotationPresent(EventHandler.class)) {
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            return eventHandler.annotationType().getSimpleName();
        }
        if (method.isAnnotationPresent(QueryHandler.class)) {
            QueryHandler queryHandler = method.getAnnotation(QueryHandler.class);
            return queryHandler.annotationType().getSimpleName();
        }
        return null;
    }

    private String getHandlerValue(Method method) {
        if (method.isAnnotationPresent(CommandHandler.class)) {
            CommandHandler commandHandler = method.getAnnotation(CommandHandler.class);
            return commandHandler.name();
        }
        if (method.isAnnotationPresent(EventHandler.class)) {
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            return eventHandler.name();
        }
        if (method.isAnnotationPresent(QueryHandler.class)) {
            QueryHandler queryHandler = method.getAnnotation(QueryHandler.class);
            return queryHandler.name();
        }
        return null;
    }

    private void sendMessage(String appName, String flow, String bizCode, Object data, String version, String type, long timestamp) {
        ReportSendMessage content = new ReportSendMessage()
                .setAppName(appName)
                .setFlow(flow)
                .setBizCode(bizCode)
                .setData(data)
                .setVersion(version)
                .setType(type)
                .setUpdateVersion(appName + "_" + version + "_" + timestamp);
        final int hash = Objects.hash(content);
        Message message = new MessageBuilderImpl()
                .setTopic(reporterProperties.getTopic())
                .setKeys(String.valueOf(hash))
                .setBody(JSON.toJSONString(content).getBytes(StandardCharsets.UTF_8)).build();
        try {
            producer.send(message);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    private void reportHandler(String appName, List<HandlerInfo> handlerInfoList, Flow flow, long timestamp) {
        final List<Node> nodes = flow.getGraph().getNodes();
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }

        final String bizCode = flow.getBizCode();
        List<HandlerInfo> matchHandler = new ArrayList<>();
        Set<String> moduleSet = nodes.stream().map(Node::getId).collect(Collectors.toSet());
        List<HandlerInfo> moduleHandlers = handlerInfoList.stream().filter(h -> moduleSet.contains(h.getModule())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(moduleHandlers)) {
            moduleHandlers.forEach(h -> h.setFlow(flow.getClassName()));
            matchHandler.addAll(moduleHandlers);
        }

        if (CollectionUtils.isNotEmpty(matchHandler)) {
            sendMessage(appName, flow.getClassName(), bizCode, matchHandler, flow.getVersion(), _HANDLER_, timestamp);
        }
    }

}
