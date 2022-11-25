package com.cake.fast.bizflow.module;

import com.cake.fast.bizflow.flow.CreateWorkOrderFlow;
import com.cake.framework.common.bizflow.BizModule;
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
@BizModule(name = "step03", parents = {M2.class}, role = "ROLE_CCC", flow = CreateWorkOrderFlow.class)
public class M3 {
}
