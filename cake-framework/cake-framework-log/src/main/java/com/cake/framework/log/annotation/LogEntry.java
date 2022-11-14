package com.cake.framework.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * LogPoint
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/28 22:51
 * @email 18668485565163.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LogEntry {

    String bizType() default "";
}
