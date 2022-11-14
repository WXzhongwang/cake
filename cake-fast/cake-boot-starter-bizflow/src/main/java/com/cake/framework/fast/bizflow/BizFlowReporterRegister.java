package com.cake.framework.fast.bizflow;

import com.cake.framework.fast.bizflow.report.Reporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.*;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 业务流程自动注册机制
 *
 * @author zhongshengwang
 * @description 业务流程自动注册机制
 * @date 2022/11/9 22:39
 * @email 18668485565163.com
 */

@Slf4j
@ConditionalOnProperty(value = "cake.report.enabled")
@Configuration
public class BizFlowReporterRegister implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnProperty(value = "cake.report.enabled")
    @ConfigurationProperties(prefix = "cake.report.config")
    public ReporterProperties reporterProperties() {
        return new ReporterProperties();
    }


    @Bean(name = "cakeBizFlowReporter")
    @ConditionalOnMissingBean
    @ConditionalOnClass({ReporterProperties.class})
    public Producer producer(ReporterProperties reporterProperties) throws ClientException {
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints(reporterProperties.getEndpoint());
        builder.setCredentialProvider(new StaticSessionCredentialsProvider(reporterProperties.getAccessKey(), reporterProperties.getAccessSecret()));
        ClientConfiguration configuration = builder.build();
        /*
          初始化Producer时直接配置需要使用的Topic列表（这个参数可以配置多个Topic），实现提前检查错误配置、拦截非法配置启动。
          针对非事务消息 Topic，也可以不配置，服务端会动态检查消息的Topic是否合法。
          注意！！！事务消息Topic必须提前配置，以免事务消息回查接口失败，具体原理请参见事务消息。
          */
        return provider.newProducerBuilder()
                // .setTopics(reporterProperties.getTopic())
                .setClientConfiguration(configuration)
                .build();
    }

    @Bean
    @ConditionalOnClass({ReporterProperties.class})
    @ConditionalOnBean(name = "cakeBizFlowReporter")
    public Reporter reporter(@Qualifier("cakeBizFlowReporter") Producer producer, ReporterProperties reporterProperties) {
        return new Reporter(producer, reporterProperties);
    }
}
