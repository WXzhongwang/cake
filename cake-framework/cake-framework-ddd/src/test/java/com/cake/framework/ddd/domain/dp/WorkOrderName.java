package com.cake.framework.ddd.domain.dp;

import lombok.Value;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/31 23:42
 * @email 18668485565163.com
 */
@Value
public class WorkOrderName {

    String name;

    public WorkOrderName(String name) {
        if (name == null || name.length() < 6) {
            throw new IllegalArgumentException("名称参数校验失败");
        }
        this.name = name;
    }
}
