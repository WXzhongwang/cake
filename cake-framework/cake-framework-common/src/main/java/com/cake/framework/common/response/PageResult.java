package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;

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
public class PageResult<T> extends Response {

    private Page<T> content;

    public PageResult() {
    }
}
