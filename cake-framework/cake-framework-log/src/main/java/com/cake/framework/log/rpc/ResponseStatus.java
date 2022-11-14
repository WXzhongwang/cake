package com.cake.framework.log.rpc;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/11 22:26
 * @email 18668485565163.com
 */
public interface ResponseStatus {

    /**
     * 响应是否成功
     *
     * @param value
     * @return
     */
    boolean isSuccess(Object value);
}
