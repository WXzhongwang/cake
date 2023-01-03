package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2023/1/3 22:05
 * @email 18668485565163.com
 */
@Getter
@Setter
public class CollectionResult<T> extends Response {

    private Collection<T> content = new ArrayList<>(0);

    private Integer totalCount;

    public CollectionResult() {
    }
}
