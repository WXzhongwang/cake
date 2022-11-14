package com.cake.framework.cqrs.base;

import java.lang.annotation.*;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 22:57
 * @email 18668485565163.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandHandler {

    /**
     * 命令描述
     *
     * @return
     */
    String name();
}
