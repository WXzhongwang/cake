package com.rany.cake.framework.common.base;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 值对象
 *
 * @author zhongshengwang
 * @description 值对象
 * @date 2022/10/31 23:44
 * @email 18668485565163.com
 */
@Getter
@Setter
public abstract class BaseValueObject<T> {

    protected T id;

    protected Date gmtCreate;

    protected Date gmtModified;

    protected String isDeleted;
}
