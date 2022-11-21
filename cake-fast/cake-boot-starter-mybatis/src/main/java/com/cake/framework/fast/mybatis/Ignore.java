package com.cake.framework.fast.mybatis;

import java.lang.annotation.*;

/**
 * 忽略检查
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/21 22:26
 * @email 18668485565163.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore {

    boolean value() default true;
}
