package com.cake.framework.ddd.repository;

import com.cake.framework.ddd.base.IAggregate;
import com.cake.framework.ddd.base.Identifier;

import javax.validation.constraints.NotNull;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 22:00
 * @email 18668485565163.com
 */
public interface Repository<T extends IAggregate<ID>, ID extends Identifier> {


    /**
     * Find an aggregate through its ID
     *
     * @param id
     * @return
     */
    T find(@NotNull ID id);

    /**
     * Remove an entity from repo
     *
     * @param aggregate
     */
    void remove(@NotNull T aggregate);

    /**
     * Save an entity by repo
     *
     * @param aggregate
     */
    void save(@NotNull T aggregate);

}
