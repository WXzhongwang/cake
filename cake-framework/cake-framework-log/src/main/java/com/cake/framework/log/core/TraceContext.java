package com.cake.framework.log.core;

import lombok.Data;

import java.util.LinkedList;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/29 00:49
 * @email 18668485565163.com
 */
@Data
public class TraceContext {

    private LinkedList<Node> nodes;
    private LogTracker logTracker;

    public TraceContext(LinkedList<Node> nodes, LogTracker logTracker) {
        this.nodes = nodes;
        this.logTracker = logTracker;
    }
}
