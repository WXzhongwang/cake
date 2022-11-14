package com.cake.fast.bizflow.app;

import com.cake.fast.bizflow.flow.CreateWorkOrderFlow;
import com.rany.cake.framework.common.bizflow.App;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/10 00:02
 * @email 18668485565163.com
 */
@Component
@App(code = "WorkOrderBiz", flows = {CreateWorkOrderFlow.class})
public class WorkOrderBiz {

}
