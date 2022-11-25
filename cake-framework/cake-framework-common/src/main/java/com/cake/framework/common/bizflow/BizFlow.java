package com.cake.framework.common.bizflow;

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
public @interface BizFlow {

    /**
     * 业务流名称
     *
     * @return 业务流名称
     */
    String name();

    /**
     * 版本
     *
     * @return 版本信息
     */
    String version();
}
