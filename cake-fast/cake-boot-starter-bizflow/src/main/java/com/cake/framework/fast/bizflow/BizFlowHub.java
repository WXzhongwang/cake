package com.cake.framework.fast.bizflow;

import com.cake.framework.fast.bizflow.base.BizFlowException;
import com.cake.framework.fast.bizflow.base.Flow;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 全业务流程集中存储的数据结构
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/11/9 23:03
 * @email 18668485565163.com
 */
@Data
public class BizFlowHub implements Serializable {

    /**
     * key bizCode
     * value Flow
     */
    private MultiValueMap<String, Flow> flowMap;

    public void put(Flow flow) {
        if (Objects.isNull(flow) || StringUtils.isEmpty(flow.getBizCode()) || Objects.isNull(flow.getGraph())) {
            return;
        }
        if (Objects.isNull(flowMap)) {
            flowMap = new LinkedMultiValueMap<>();
        }
        flowMap.add(flow.getBizCode(), flow);
    }

    public void putAll(List<Flow> flows) {
        if (CollectionUtils.isEmpty(flows)) {
            return;
        }
        for (Flow flow : flows) {
            put(flow);
        }
    }

    public List<Flow> getFlow(String bizCode) throws BizFlowException {
        if (!flowMap.containsKey(bizCode)) {
            throw new BizFlowException("500", "no mapping flow: " + bizCode);
        }
        return flowMap.get(bizCode);
    }
}
