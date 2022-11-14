package com.cake.framework.log.annotation;

import java.lang.annotation.*;

/**
 * 是否开启trace
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/28 22:51
 * @email 18668485565163.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface EnableTrace {
}
