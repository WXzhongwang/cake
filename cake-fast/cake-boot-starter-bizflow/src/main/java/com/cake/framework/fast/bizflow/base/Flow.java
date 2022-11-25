package com.cake.framework.fast.bizflow.base;

import com.cake.framework.common.bizflow.App;
import com.cake.framework.common.bizflow.BizFlow;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 工作流
 *
 * @author zhongshengwang
 * @description 工作流
 * @date 2022/11/9 22:55
 * @email 18668485565163.com
 */
@Data
@AllArgsConstructor
public class Flow implements Serializable {

    /**
     * 业务标识
     *
     * @see App
     */
    private String bizCode;


    /**
     * 被BizFlow注解标识的类的全限定名
     */
    private String className;


    /**
     * flow名称
     */
    private String name;


    /**
     * version
     */
    private String version;


    /**
     * graph
     */
    private Graph graph;


    /**
     * 构建flow
     *
     * @param bizCode
     * @param bizFlow
     * @param className
     * @param graph
     * @return
     */
    public static Flow create(String bizCode, BizFlow bizFlow, String className, Graph graph) {
        return new Flow(bizCode, className, bizFlow.name(), bizFlow.version(), graph);
    }
}
