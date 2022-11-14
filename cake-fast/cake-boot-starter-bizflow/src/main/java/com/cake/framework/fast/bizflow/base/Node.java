package com.cake.framework.fast.bizflow.base;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 节点
 *
 * @author zhongshengwang
 * @description 节点
 * @date 2022/11/9 22:47
 * @email 18668485565163.com
 */
@Data
@Builder
public class Node implements Serializable {

    /**
     * 节点ID
     */
    private String id;


    /**
     * 节点名
     */
    private String name;

    /**
     * 所属角色
     */
    private String role;

    /**
     * 构造节点方法
     *
     * @param id
     * @param name
     * @param role
     * @return
     */
    public static Node create(String id, String name, String role) {
        return new Node(id, name, role);
    }
}
