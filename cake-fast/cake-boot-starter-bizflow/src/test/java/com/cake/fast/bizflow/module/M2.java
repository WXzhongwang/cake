package com.cake.fast.bizflow.module;

import com.cake.fast.bizflow.flow.CreateWorkOrderFlow;
import com.rany.cake.framework.common.bizflow.BizModule;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/10 00:03
 * @email 18668485565163.com
 */
@Component
@BizModule(name = "step02", parents = {M1.class}, role = "ROLE_BBB", flow = CreateWorkOrderFlow.class)
public class M2 {
}
