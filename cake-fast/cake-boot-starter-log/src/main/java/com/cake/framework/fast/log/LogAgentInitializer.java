package com.cake.framework.fast.log;

import com.cake.framework.log.CakeLogInitializer;
import com.cake.framework.log.agent.CakeLogAgentProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;

import java.util.Objects;

/**
 * LogAgentInitializer
 *
 * @author zhongshengwang
 * @description 初始化
 * @date 2022/11/8 22:11
 * @email 18668485565163.com
 */
@Slf4j
public class LogAgentInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String AGENT_PROPERTY_NAME = "cake.log.scanPackages";

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        PropertySource<?> propertySource = configurableApplicationContext.getEnvironment()
                .getPropertySources().get("applicationConfigurationProperties");
        if (null != propertySource
                && propertySource.containsProperty(AGENT_PROPERTY_NAME)
                && null != propertySource.getProperty(AGENT_PROPERTY_NAME)) {
            System.out.println("\n" +
                    "  .oooooo.         .o.       oooo    oooo oooooooooooo \n" +
                    " d8P'  `Y8b       .888.      `888   .8P'  `888'     `8 \n" +
                    "888              .8\"888.      888  d8'     888         \n" +
                    "888             .8' `888.     88888[       888oooo8    \n" +
                    "888            .88ooo8888.    888`88b.     888    \"    \n" +
                    "`88b    ooo   .8'     `888.   888  `88b.   888       o \n" +
                    " `Y8bood8P'  o88o     o8888o o888o  o888o o888ooooood8 \n" +
                    "                                                       \n" +
                    "                                                       \n");
            System.setProperty(CakeLogAgentProperties.AGENT_LOG_SCAN_PACKAGE,
                    Objects.requireNonNull(propertySource.getProperty(AGENT_PROPERTY_NAME)).toString());

        }
        CakeLogInitializer.init();
    }
}
