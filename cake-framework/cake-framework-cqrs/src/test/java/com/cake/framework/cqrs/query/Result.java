package com.cake.framework.cqrs.query;

import lombok.Data;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 23:16
 * @email 18668485565163.com
 */
@Data
public class Result {

    private String value;

    public Result(String value) {
        this.value = value;
    }
}
