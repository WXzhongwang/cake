package com.cake.framework.ddd.domain.repository;

import com.cake.framework.ddd.domain.aggregate.WorkOrder;
import com.cake.framework.ddd.domain.pk.WorkOrderId;
import com.cake.framework.ddd.repository.Repository;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 22:07
 * @email 18668485565163.com
 */
public interface WorkOrderRepository extends Repository<WorkOrder, WorkOrderId> {
}
