package com.cake.framework.common.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2023/1/3 22:11
 * @email 18668485565163.com
 */
@Getter
@Setter
public class Page<T> implements Serializable {

    private Collection<T> items = new ArrayList<>(0);
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Integer totalPage = 0;
    private Integer total = 0;
}
