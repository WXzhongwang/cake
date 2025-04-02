package com.cake.framework.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 参数拦截插件
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/21 22:27
 * @email 18668485565163.com
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "cake.mybatis.tenant-limit", matchIfMissing = true)
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update",
                args = {MappedStatement.class, Object.class})}
)
public class ParamLimitInterceptor implements Interceptor {
    /**
     * SQL安全参数
     */
    private static final List<String> NECESSARY_PARAM_NAMES = Collections.singletonList("tenantId");
    private static final String INSERT_METHOD = "insert";
    private static final String PAGE_COUNT_METHOD_SUFFIX = "_COUNT";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
        BoundSql boundSql = statement.getBoundSql(invocation.getArgs()[1]);
        if(ignore(boundSql.getSql(), statement)) {
            return invocation.proceed();
        }
        List<ParameterMapping> sqlParameters = boundSql.getParameterMappings();
        if (CollectionUtils.isEmpty(sqlParameters)) {
            throw new RuntimeException(String.format("基于SQL安全要求，SQL参数必须传入[%s]", StringUtils.join(NECESSARY_PARAM_NAMES, ",")));
        }
        boolean containsNecessaryParam = false;
        for (ParameterMapping sqlParameter : sqlParameters) {
            String property = sqlParameter.getProperty();
            if (property.contains(".")) {
                property = property.substring(property.lastIndexOf(".") + 1);
            }
            containsNecessaryParam = NECESSARY_PARAM_NAMES.contains(property);
            if (containsNecessaryParam) {
                break;
            }
        }

        if (!containsNecessaryParam) {
            throw new RuntimeException(String.format("基于SQL安全要求，SQL参数必须传入[%s]", StringUtils.join(NECESSARY_PARAM_NAMES, ",")));
        }
        return invocation.proceed();
    }

    private boolean ignore(String sql, MappedStatement statement) throws ClassNotFoundException {
        String start = sql.substring(0, sql.indexOf(" "));
        if (INSERT_METHOD.equals(start)) {
            return true;
        }

        String statementId = statement.getId();
        String methodName = statementId.substring(statementId.lastIndexOf(".") + 1);
        String className = statementId.substring(0, statementId.lastIndexOf("."));
        if (methodName.endsWith(PAGE_COUNT_METHOD_SUFFIX)) {
            return true;
        }
        boolean ignore =false;
        Method[] methods = Class.forName(className).getMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                Ignore annotation = method.getAnnotation(Ignore.class);
                if (annotation != null && annotation.value()) {
                    ignore =true;
                }
                break;
            }
        }
        return ignore;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

}
