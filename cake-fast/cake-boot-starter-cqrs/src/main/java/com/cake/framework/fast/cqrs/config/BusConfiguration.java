package com.cake.framework.fast.cqrs.config;

import com.cake.framework.cqrs.bus.CommandBus;
import com.cake.framework.cqrs.bus.EventBus;
import com.cake.framework.cqrs.bus.QueryBus;
import com.cake.framework.fast.cqrs.factory.CommandBusFactoryBean;
import com.cake.framework.fast.cqrs.factory.EventBusFactoryBean;
import com.cake.framework.fast.cqrs.factory.QueryBusFactoryBean;
import com.cake.framework.fast.cqrs.processor.BusRegisterProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动装配
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/2 22:12
 * @email 18668485565163.com
 */
@Configuration
public class BusConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EventBusFactoryBean eventBusFactoryBean() {
        return new EventBusFactoryBean();
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryBusFactoryBean queryBusFactoryBean() {
        return new QueryBusFactoryBean();
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandBusFactoryBean commandBusFactoryBean() {
        return new CommandBusFactoryBean();
    }


    @Bean
    @ConditionalOnMissingBean
    public EventBus eventBuBean(EventBusFactoryBean eventBusFactoryBean) {
        return eventBusFactoryBean.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryBus queryBusBean(QueryBusFactoryBean queryBusFactoryBean) {
        return queryBusFactoryBean.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandBus commandBusBean(CommandBusFactoryBean commandBusFactoryBean) {
        return commandBusFactoryBean.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(value = {EventBus.class, QueryBus.class, CommandBus.class})
    public BusRegisterProcessor busRegisterProcessor(EventBus eventBus, QueryBus queryBus, CommandBus commandBus) {
        return new BusRegisterProcessor(eventBus, queryBus, commandBus);
    }
}
