package com.cake.framework.fast.cqrs.factory;

import com.cake.framework.cqrs.bus.CommandBus;
import org.springframework.beans.factory.FactoryBean;

/**
 * CommandBusFactoryBean
 *
 * @author zhongshengwang
 * @description CommandBusFactoryBean
 * @date 2022/11/2 22:09
 * @email 18668485565163.com
 */
public class CommandBusFactoryBean implements FactoryBean<CommandBus> {

    private CommandBus commandBus;

    @Override
    public CommandBus getObject() {
        if (commandBus == null) {
            synchronized (this) {
                if (commandBus == null) {
                    commandBus = new CommandBus();
                }
            }
        }
        return commandBus;
    }

    @Override
    public Class<?> getObjectType() {
        return CommandBus.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
