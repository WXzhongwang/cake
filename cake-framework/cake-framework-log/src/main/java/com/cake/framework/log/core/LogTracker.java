package com.cake.framework.log.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/29 00:39
 * @email 18668485565163.com
 */
@Data
public class LogTracker {

    private boolean isTrack;
    private List<LogDO> logDOList = Collections.synchronizedList(new ArrayList<>());

    public void add(LogDO logDO) {
        logDOList.add(logDO);
    }
}
