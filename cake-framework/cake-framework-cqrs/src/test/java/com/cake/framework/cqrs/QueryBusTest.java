package com.cake.framework.cqrs;

import com.cake.framework.cqrs.base.QueryHandler;
import com.cake.framework.cqrs.bus.QueryBus;
import com.cake.framework.cqrs.query.Result;
import com.cake.framework.cqrs.query.TestQuery;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 23:14
 * @email 18668485565163.com
 */
public class QueryBusTest {

    public static void main(String[] args) {
        QueryBus queryBus = new QueryBus();
        queryBus.register(new QueryBusTest());

        Result queryResult = queryBus.dispatch(new TestQuery<>("QUERR AAAA"));
        System.out.println(queryResult.getValue());
    }

    @QueryHandler(name = "handleQueryRequest")
    public Result handleEvent1(TestQuery query) {
        System.out.println("handleQueryRequest: " + query.toString());
        return new Result("value1");
    }
}
