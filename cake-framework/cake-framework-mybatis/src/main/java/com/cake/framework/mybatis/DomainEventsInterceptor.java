package com.cake.framework.mybatis;

import com.rany.cake.framework.common.base.BaseAggregateRoot;
import com.rany.cake.framework.common.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.data.util.AnnotationDetectionMethodCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 拦截事务进行领域事件自动化发布
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/21 22:27
 * @email 18668485565163.com
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})}
)
public class DomainEventsInterceptor implements Interceptor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        Object object = invocation.getArgs()[1];
        if (object instanceof BaseAggregateRoot) {
            EventPublishingMethod eventPublishingMethod = EventPublishingMethod.of(object.getClass());
            if (eventPublishingMethod != null) {
                eventPublishingMethod.publishEvents(object, applicationContext);
            }
        }
        return result;
    }
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    @RequiredArgsConstructor
    static class EventPublishingMethod {
        private static Map<Class<?>, EventPublishingMethod> CACHE = new ConcurrentReferenceHashMap<>();
        private static EventPublishingMethod NONE = new EventPublishingMethod(null, null);

        private final Method pushingMethod;
        private final Method clearingMethod;

        public static EventPublishingMethod of(Class<?> aggregateRootType) {
            Assert.notNull(aggregateRootType, "AggregateRoot Type can't be null");
            EventPublishingMethod eventPublishingMethod = CACHE.get(aggregateRootType);
            if (eventPublishingMethod != null) {
                return eventPublishingMethod.orNull();
            }

            AnnotationDetectionMethodCallback<DomainEvents> publishing = new AnnotationDetectionMethodCallback<>(DomainEvents.class);
            ReflectionUtils.doWithMethods(aggregateRootType, publishing);

            AnnotationDetectionMethodCallback<AfterDomainEventPublication> clearing = new AnnotationDetectionMethodCallback<>(AfterDomainEventPublication.class);
            ReflectionUtils.doWithMethods(aggregateRootType, clearing);

            EventPublishingMethod result = from(publishing, clearing);

            CACHE.put(aggregateRootType, result);
            return result.orNull();
        }

        private static EventPublishingMethod from(AnnotationDetectionMethodCallback<?> publishing, AnnotationDetectionMethodCallback<?> clearing) {
            if (!publishing.hasFoundAnnotation()) {
                return EventPublishingMethod.NONE;
            }

            Method method = publishing.getMethod();
            assert method != null;
            ReflectionUtils.makeAccessible(method);
            return new EventPublishingMethod(method, getClearingMethod(clearing));
        }

        private static Method getClearingMethod(AnnotationDetectionMethodCallback<?> clearing) {
            if (!clearing.hasFoundAnnotation()) {
                return null;
            }
            Method method = clearing.getMethod();
            assert method != null;
            ReflectionUtils.makeAccessible(method);
            return method;
        }

        private static Collection<Object> asCollection(Object source) {
            if (source == null) {
                return Collections.emptyList();
            }
            if (source instanceof Collection) {
                return (Collection<Object>) source;
            }

            return Collections.singletonList(source);
        }


        private EventPublishingMethod orNull() {
            return this == EventPublishingMethod.NONE ? null : this;
        }

        public void publishEvents(Object object, ApplicationEventPublisher applicationContext) {
            if (object == null) {
                log.warn("publishEvents error, object is null");
                return;
            }

            for (Object aggregateRoot : asCollection(object)) {
                for (Object event : asCollection(ReflectionUtils.invokeMethod(pushingMethod, aggregateRoot))) {
                    applicationContext.publishEvent(event);
                }

                if (clearingMethod !=  null) {
                    ReflectionUtils.invokeMethod(clearingMethod, aggregateRoot);
                }
            }
        }
    }
}
