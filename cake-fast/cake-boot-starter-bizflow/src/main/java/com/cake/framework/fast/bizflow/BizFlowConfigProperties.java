package com.cake.framework.fast.bizflow;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BizFlowConfigProperties
 *
 * @author zhongshengwang
 * @description BizFlowConfigProperties
 * @date 2022/11/9 22:39
 * @email 18668485565163.com
 */
@Data
@ConfigurationProperties(prefix = "cake.bizflow")
public class BizFlowConfigProperties {
    private boolean enabled = true;
}
