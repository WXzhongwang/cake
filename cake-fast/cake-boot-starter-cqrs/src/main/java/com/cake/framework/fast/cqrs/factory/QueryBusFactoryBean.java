package com.cake.framework.fast.cqrs.factory;

import com.cake.framework.cqrs.bus.QueryBus;
import org.springframework.beans.factory.FactoryBean;

/**
 * EventBusFactoryBean
 *
 * @author zhongshengwang
 * @description EventBusFactoryBean
 * @date 2022/11/2 22:09
 * @email 18668485565163.com
 */
public class QueryBusFactoryBean implements FactoryBean<QueryBus> {

    private QueryBus queryBus;

    @Override
    public QueryBus getObject() {
        if (queryBus == null) {
            synchronized (this) {
                if (queryBus == null) {
                    queryBus = new QueryBus();
                }
            }
        }
        return queryBus;
    }

    @Override
    public Class<?> getObjectType() {
        return QueryBus.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
