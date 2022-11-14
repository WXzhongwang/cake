package com.cake.framework.cqrs.dispatcher;

import com.cake.framework.cqrs.base.Command;
import com.cake.framework.cqrs.base.Invoker;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 23:00
 * @email 18668485565163.com
 */
public class CommandDispatcher {


    public Object dispatch(Command command, Invoker invoker) {
        if (command == null || invoker == null) {
            throw new IllegalArgumentException("CommandDispatcher argument is null");
        }
        return invoker.handleMethod(command);
    }
}
