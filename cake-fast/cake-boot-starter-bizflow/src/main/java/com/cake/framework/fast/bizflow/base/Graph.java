package com.cake.framework.fast.bizflow.base;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/9 22:46
 * @email 18668485565163.com
 */
@Data
@AllArgsConstructor
public class Graph implements Serializable {

    private List<String> roles;

    private List<Node> nodes;

    private List<Edge> edges;
}
