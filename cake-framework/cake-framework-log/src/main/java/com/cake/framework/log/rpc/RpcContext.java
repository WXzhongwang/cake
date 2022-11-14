package com.cake.framework.log.rpc;

import com.alibaba.dubbo.rpc.Invocation;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 22:19
 * @email 18668485565163.com
 */
@Data
public class RpcContext {

    private String interfaceName;
    private String methodName;
    private Object[] methodArgs;
    private Class<?>[] paramClasses;
    private Invocation invocation;
    private Method method;
    private Class returnType;

}
