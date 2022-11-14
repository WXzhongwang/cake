package com.cake.framework.log.core;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 节点信息
 *
 * @author zhongshengwang
 * @description 节点信息
 * @date 2022/10/29 00:02
 * @email 18668485565163.com
 */
@Data
public class Node {

    private String path;
    private Node parent;
    private String bizType;
    private AtomicLong nextIndex = new AtomicLong(0);

    public long inc() {
        return nextIndex.incrementAndGet();
    }

    public void resetNextIndex(long index) {
        nextIndex.set(index);
    }
}
