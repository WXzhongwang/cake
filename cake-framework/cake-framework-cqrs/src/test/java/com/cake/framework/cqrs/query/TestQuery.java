package com.cake.framework.cqrs.query;

import com.cake.framework.cqrs.base.Query;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 23:14
 * @email 18668485565163.com
 */
public class TestQuery<Result> implements Query<Result> {

    private final String name;

    public TestQuery(String name) {
        this.name = name;
    }
}
