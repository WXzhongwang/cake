package com.cake.framework.ddd.domain.aggregate;


import com.cake.framework.ddd.domain.dp.WorkOrderName;
import com.cake.framework.ddd.domain.pk.WorkOrderId;
import com.cake.framework.common.base.BaseAggregateRoot;
import com.cake.framework.common.base.IAggregate;
import lombok.Data;

import java.util.Date;

/**
 * 聚合根
 *
 * @author zhongshengwang
 * @description 聚合根
 * @date 2022/10/31 22:32
 * @email 18668485565163.com
 */
@Data
public class WorkOrder extends BaseAggregateRoot implements IAggregate<WorkOrderId> {

    private WorkOrderId workOrderId;
    
    /**
     * 这是一个典型的DP
     */
    private WorkOrderName workOrderName;

    public WorkOrder(WorkOrderId id, WorkOrderName workOrderName) {
        this.workOrderId = id;
        this.workOrderName = workOrderName;
        this.gmtCreate = new Date();
        this.gmtModified = new Date();
        this.isDeleted = "0";
    }

    @Override
    public WorkOrderId getBizID() {
        return workOrderId;
    }
}
