package com.rany.cake.framework.common.base;

/**
 * Identifier 主键标识
 * 所有Entity主键建议以DP的模式组装
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/31 22:14
 * @email 18668485565163.com
 */
public interface Identifiable<ID extends Identifier> {

    /**
     * 获取主键ID
     *
     * @return
     */
    ID getId();

}
