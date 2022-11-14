package com.cake.framework.ddd.base;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 实体
 *
 * @author zhongshengwang
 * @description 实体
 * @date 2022/10/31 23:44
 * @email 18668485565163.com
 */
@Getter
@Setter
public abstract class BaseEntity {

    protected Date gmtCreate;

    protected Date gmtModified;

    protected String isDeleted;
}
