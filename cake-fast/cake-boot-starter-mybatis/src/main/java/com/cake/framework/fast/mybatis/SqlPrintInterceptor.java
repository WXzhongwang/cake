package com.cake.framework.fast.mybatis;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 打印SQL
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/21 22:27
 * @email 18668485565163.com
 */
@Slf4j
@ConditionalOnBean({DataSource.class})
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@Component
@ConditionalOnProperty(name = "enable.sql.print.log", havingValue = "true")
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update",
                args = {MappedStatement.class, Object.class})}
)
public class SqlPrintInterceptor implements Interceptor {

    @Value("${warn.sql.time:500}")
    private long warnSql;
    @Value("${enable.sql.print.log:false}")
    private boolean enablePrint;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        String sqlId = mappedStatement.getId();
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        Configuration configuration = mappedStatement.getConfiguration();
        long start = System.currentTimeMillis();
        Object result = null;
        try {
            result = invocation.proceed();
        } finally {
            try {
                if (enablePrint) {
                    long sqlCost = System.currentTimeMillis() - start;
                    String sql = this.getSql(configuration, boundSql);
                    this.formatLog(mappedStatement.getSqlCommandType(), sqlId, sql, sqlCost, result);
                }
            } catch (Exception ex) {
                log.error("sql格式化输出异常", ex);
            }
        }
        return result;
    }

    private void formatLog(SqlCommandType sqlCommandType, String sqlId, String sql, long sqlCost, Object result) {
        String sqlResult = String.format("DAO [%s]\n[%dms] ===> %s\n", sqlId, sqlCost, sql);
        if (sqlCommandType == SqlCommandType.UPDATE || sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.DELETE) {
            sqlResult += "Count===>" + result;
        }
        if (sqlCost > warnSql) {
            log.warn(sqlResult);
        } else {
            log.info(sql);
        }
    }

    private String getSql(Configuration configuration, BoundSql boundSql) {
        String sql = boundSql.getSql();
        if (StringUtils.isEmpty(sql)) {
            return "";
        }
        sql = beautifySql(sql);

        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = this.replacePlaceHolder(sql, parameterObject);
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = replacePlaceHolder(sql, obj);
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object addObj = boundSql.getAdditionalParameter(propertyName);
                        sql = replacePlaceHolder(sql, addObj);
                    }
                }
            }
        }
        return sql;
    }

    private String beautifySql(String sql) {
        return sql.replaceAll("[\\s\n ]+", " ");
    }

    private String replacePlaceHolder(String sql, Object parameterObject) {
        String result;
        if (sql == null) {
            return null;
        }
        if (parameterObject == null) {
            result = "null";
        } else if (parameterObject instanceof String) {
            result = "'" + parameterObject.toString() + "'";
        } else if (parameterObject instanceof Date) {
            Date date = (Date) parameterObject;
            result = "'" + DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss") + "'";
        } else {
            result = parameterObject.toString();
        }
        return sql.replaceFirst("\\?", result);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

}
