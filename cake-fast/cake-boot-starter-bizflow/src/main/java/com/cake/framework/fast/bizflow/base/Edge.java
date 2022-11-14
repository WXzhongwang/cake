package com.cake.framework.fast.bizflow.base;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * 边
 *
 * @author zhongshengwang
 * @description 边
 * @date 2022/11/9 22:47
 * @email 18668485565163.com
 */
@Data
public class Edge implements Serializable {

    /**
     * 边的ID
     */
    private String id;

    /**
     * 源节点ID
     */
    private String source;

    /**
     * 目标节点ID
     */
    private String target;


    /**
     * 构建边的静态方法
     *
     * @param source
     * @param target
     * @return
     */
    public static Optional<Edge> create(String source, String target) {
        if (StringUtils.isEmpty(source) || StringUtils.isEmpty(target)) {
            return Optional.empty();
        }
        Edge edge = new Edge();
        edge.setSource(source);
        edge.setTarget(target);
        edge.setId(String.valueOf(Objects.hash(source, target)));
        return Optional.of(edge);
    }
}
