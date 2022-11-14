package com.cake.framework.log.rpc;

import com.alibaba.dubbo.rpc.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 22:19
 * @email 18668485565163.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RpcResponseContext extends RpcContext {

    private Result rpcResult;

    private Object responseValue;
}
