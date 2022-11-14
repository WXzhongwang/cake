package com.cake.framework.fast.cqrs.factory;

import com.cake.framework.cqrs.bus.EventBus;
import org.springframework.beans.factory.FactoryBean;

/**
 * EventBusFactoryBean
 *
 * @author zhongshengwang
 * @description EventBusFactoryBean
 * @date 2022/11/2 22:09
 * @email 18668485565163.com
 */
public class EventBusFactoryBean implements FactoryBean<EventBus> {

    private EventBus eventBus;

    @Override
    public EventBus getObject() {
        if (eventBus == null) {
            synchronized (this) {
                if (eventBus == null) {
                    eventBus = new EventBus();
                }
            }
        }
        return eventBus;
    }

    @Override
    public Class<?> getObjectType() {
        return EventBus.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
