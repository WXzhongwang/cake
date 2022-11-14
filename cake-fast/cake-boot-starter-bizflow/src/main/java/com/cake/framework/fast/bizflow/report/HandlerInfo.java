package com.cake.framework.fast.bizflow.report;

import com.cake.framework.cqrs.base.CommandHandler;
import lombok.Data;

import java.io.Serializable;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/10 23:10
 * @email 18668485565163.com
 */
@Data
public class HandlerInfo implements Serializable {

    /**
     * handler所在的flow className
     */
    private String flow;

    /**
     * handler所在的module bean的className, 被
     *
     * @see com.rany.cake.framework.common.bizflow.BizModule 注解的类
     */
    private String module;

    /**
     * handler方法的参数类型
     */
    private String paramType;

    /**
     * handler方法的返回类型
     */
    private String returnType;

    /**
     * 方法名
     */
    private String method;

    /**
     * Handler注解的name值
     * {@link CommandHandler#name()}
     */
    private String handlerName;

    /**
     * Handler注解的name值
     * {@link CommandHandler} {@link com.cake.framework.cqrs.base.QueryHandler} {@link com.cake.framework.cqrs.base.EventHandler}
     */
    private String handlerType;
}
