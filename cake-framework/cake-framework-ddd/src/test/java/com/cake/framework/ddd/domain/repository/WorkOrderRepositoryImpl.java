package com.cake.framework.ddd.domain.repository;

import com.cake.framework.ddd.domain.aggregate.WorkOrder;
import com.cake.framework.ddd.domain.pk.WorkOrderId;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/1 22:08
 * @email 18668485565163.com
 */
public class WorkOrderRepositoryImpl implements WorkOrderRepository {

    // DAO Constructor Inject
    // DATA Convertor MapStruct

    @Override
    public WorkOrder find(WorkOrderId workOrderId) {
        return null;
    }

    @Override
    public void remove(WorkOrder aggregate) {

    }

    @Override
    public void save(WorkOrder aggregate) {

    }
}
