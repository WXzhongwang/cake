package com.cake.framework.common.base;

import com.cake.framework.common.event.DomainEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/31 23:04
 * @email 18668485565163.com
 */
@Getter
@Setter
public abstract class BaseAggregateRoot extends BaseEntity {

    @Transient
    private transient final List<Object> domainEvents = new ArrayList<>();

    protected DomainEvent registerEvent(DomainEvent domainEvent) {
        Assert.notNull(domainEvent, "domain event must not be null");
        this.domainEvents.add(domainEvent);
        return domainEvent;
    }

    @AfterDomainEventPublication
    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }

    @DomainEvents
    protected Collection<Object> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}
