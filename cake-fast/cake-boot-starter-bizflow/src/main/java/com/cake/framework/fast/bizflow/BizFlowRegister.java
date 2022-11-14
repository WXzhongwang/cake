package com.cake.framework.fast.bizflow;

import com.cake.framework.fast.bizflow.base.*;
import com.cake.framework.fast.bizflow.report.Reporter;
import com.rany.cake.framework.common.bizflow.App;
import com.rany.cake.framework.common.bizflow.BizFlow;
import com.rany.cake.framework.common.bizflow.BizModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 业务流程自动注册机制
 *
 * @author zhongshengwang
 * @description 业务流程自动注册机制
 * @date 2022/11/9 22:39
 * @email 18668485565163.com
 */

@Slf4j
@ConditionalOnProperty(value = "cake.bizflow.enabled", matchIfMissing = true)
@EnableConfigurationProperties(BizFlowConfigProperties.class)
@Configuration
public class BizFlowRegister implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private Reporter reporter;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public BizFlowHub buildHub() throws BizFlowException {
        final Map<String, Object> appBeanMap = applicationContext.getBeansWithAnnotation(App.class);
        if (appBeanMap.isEmpty()) {
            return new BizFlowHub();
        }
        final Map<String, Object> flowBeanMap = applicationContext.getBeansWithAnnotation(BizFlow.class);
        final Map<String, Object> flowModuleBeanMap = applicationContext.getBeansWithAnnotation(BizModule.class);
        final Map<String, String> flowCodeMap = getFlowCodeMap(appBeanMap, flowBeanMap);
        final Map<String, BizFlow> flowConfigMap = getFlowConfigMap(flowBeanMap);

        BizFlowHub hub = init(flowCodeMap, flowConfigMap, flowModuleBeanMap.entrySet());
        if (reporter != null) {
            reporter.report(hub, flowModuleBeanMap);
        }
        return hub;
    }

    private Map<String, String> getFlowCodeMap(Map<String, Object> appBeanMap, Map<String, Object> flowBeanMap) {
        final Map<String, String> flowCodeMap = new ConcurrentHashMap<>();
        appBeanMap.forEach((appBeanName, bean) -> {
            final App app = applicationContext.findAnnotationOnBean(appBeanName, App.class);
            Arrays.asList(app.flows()).forEach(aClass -> flowCodeMap.putIfAbsent(aClass.getName(), app.code()));
        });
        return flowCodeMap;
    }

    private Map<String, BizFlow> getFlowConfigMap(Map<String, Object> flowBeanMap) {
        final Map<String, BizFlow> flowConfigMap = new ConcurrentHashMap<>();
        flowBeanMap.forEach((flowBeanName, bean) -> {
            final BizFlow bizFlow = applicationContext.findAnnotationOnBean(flowBeanName, BizFlow.class);
            flowConfigMap.put(bean.getClass().getName(), bizFlow);
        });
        return flowConfigMap;
    }

    private BizFlowHub init(Map<String, String> flowCodeMap, Map<String, BizFlow> flowConfigMap, Set<Map.Entry<String, Object>> moduleBeans) throws BizFlowException {
        if (CollectionUtils.isEmpty(moduleBeans)) {
            throw new BizFlowException("500", "no flow found");
        }
        MultiValueMap<String, ModuleInfo> codeModulesMap = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> moduleBean : moduleBeans) {
            BizModule bizModule = applicationContext.findAnnotationOnBean(moduleBean.getKey(), BizModule.class);
            if (!flowConfigMap.containsKey(bizModule.flow().getName())) {
                throw new BizFlowException("500", String.format("flow was not been annotated with @BizFlow, class name: %s", bizModule.flow().getName()));
            }
            final BizFlow bizFlow = flowConfigMap.get(bizModule.flow().getName());
            final String code = flowCodeMap.get(bizModule.flow().getName());

            codeModulesMap.add(code, new ModuleInfo(code, bizFlow, bizModule, moduleBean.getValue().getClass()));
            log.info("BizFlow scanning..., {}:{}:{}", moduleBean.getValue().getClass(), bizFlow.version(), bizModule.flow().getName());
        }
        return initBizFlowHub(codeModulesMap);
    }

    private BizFlowHub initBizFlowHub(MultiValueMap<String, ModuleInfo> codeModulesMap) {
        final Set<String> bizSet = codeModulesMap.keySet();
        BizFlowHub hub = new BizFlowHub();
        bizSet.forEach(bizCode -> {
            List<Flow> flow = generateFlowByBizCode(codeModulesMap.get(bizCode));
            hub.putAll(flow);
        });
        return hub;
    }

    private List<Flow> generateFlowByBizCode(List<ModuleInfo> moduleInfoList) {
        final Map<String, List<ModuleInfo>> flowMap = moduleInfoList.stream().collect(Collectors.groupingBy(i -> i.getAnnotation().flow().getName()));
        List<Flow> flows = new ArrayList<>();
        flowMap.forEach((flowClass, flowModules) -> {
            final Optional<Flow> optional = buildFlow(flowClass, flowModules);
            optional.ifPresent(flows::add);
        });
        return flows;
    }

    private Optional<Flow> buildFlow(String flowClass, List<ModuleInfo> moduleInfoList) {
        if (CollectionUtils.isEmpty(moduleInfoList)) {
            return Optional.empty();
        }
        final ModuleInfo moduleInfo = moduleInfoList.get(0);
        final Graph graph = buildDag(moduleInfoList);
        return Optional.of(Flow.create(moduleInfo.getCode(), moduleInfo.getFlow(), flowClass, graph));
    }

    private Graph buildDag(List<ModuleInfo> moduleInfoList) {
        List<Node> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        List<String> roles = new ArrayList<>();
        for (ModuleInfo moduleInfo : moduleInfoList) {
            final BizModule flowModule = moduleInfo.getAnnotation();
            final String annotatedClassName = moduleInfo.getAnnotatedClass().getName();
            Node current = Node.create(annotatedClassName, flowModule.name(), flowModule.role());
            roles.add(flowModule.role());
            nodes.add(current);
            Arrays.asList(flowModule.parents()).forEach(parent -> {
                final Optional<Edge> edge = Edge.create(parent.getName(), annotatedClassName);
                edge.ifPresent(edges::add);
            });
        }
        final List<String> roleList = new ArrayList<>(roles);
        return new Graph(roleList, nodes, edges);
    }

    @Data
    @AllArgsConstructor
    public static class ModuleInfo {
        private String code;
        private BizFlow flow;
        private BizModule annotation;
        private Class annotatedClass;
    }
}
