package com.rany.cake.framework.common.bizflow;

import java.lang.annotation.*;

/**
 * App
 *
 * @author zhongshengwang
 * @description App
 * @date 2022/11/9 22:57
 * @email 18668485565163.com
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface BizModule {

    /**
     * 业务模块名称
     *
     * @return 业务模块名称
     */
    String name();

    Class[] parents();

    String role();

    Class flow();
}
