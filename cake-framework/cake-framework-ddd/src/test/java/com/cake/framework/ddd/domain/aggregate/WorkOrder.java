package com.cake.framework.ddd.domain.aggregate;

import com.cake.framework.ddd.base.BaseAggregateRoot;
import com.cake.framework.ddd.base.IAggregate;
import com.cake.framework.ddd.domain.dp.WorkOrderName;
import com.cake.framework.ddd.domain.pk.WorkOrderId;
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

    private WorkOrderId id;
    
    /**
     * 这是一个典型的DP
     */
    private WorkOrderName workOrderName;

    public WorkOrder(WorkOrderId id, WorkOrderName workOrderName) {
        this.id = id;
        this.workOrderName = workOrderName;
        this.gmtCreate = new Date();
        this.gmtModified = new Date();
        this.isDeleted = "0";
    }
}
