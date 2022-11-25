package com.cake.framework.ddd.domain.pk;

import com.rany.cake.framework.common.base.Identifier;
import lombok.Value;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/31 22:30
 * @email 18668485565163.com
 */
@Value
public class WorkOrderId implements Identifier {

    Long Id;

    public WorkOrderId(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException();
        }
        Id = id;
    }
}
