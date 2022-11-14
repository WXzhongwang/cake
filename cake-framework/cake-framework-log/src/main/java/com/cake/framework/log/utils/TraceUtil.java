package com.cake.framework.log.utils;

import com.cake.framework.log.core.LogDO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/29 23:12
 * @email 18668485565163.com
 */
public class TraceUtil {

    private static final String AGENT_LOG_INDEX = "__agent_log_index__";
    private static final String SPACE = "    ";
    private static final String JOIN = "|_";

    public static String getTraceTree(String traceId, List<LogDO> traceLogDOList) {
        sortByPrefix(traceLogDOList);
        LogDO root = traceLogDOList.get(0);
        StringBuffer sb = new StringBuffer(traceId).append("\n");
        int rootDotLength = root.getIndex().split("\\.").length;

        for (LogDO traceLogDO : traceLogDOList) {
            int dotLength = traceLogDO.getIndex().split("\\.").length;
            if (dotLength == rootDotLength) {
                sb.append(SPACE).append(JOIN).append(getDisplay(traceLogDO)).append(SPACE).append("\n");
            } else {
                int diff = dotLength - rootDotLength;
                String msg = "";
                for (int i = 0; i < diff + 1; i++) {
                    msg += SPACE;
                }
                msg += JOIN;
                msg += getDisplay(traceLogDO);
                sb.append(msg).append("\n");
            }
        }
        return sb.toString();
    }

    public static String getDisplay(LogDO traceDO) {
        StringBuffer sb = new StringBuffer();
        sb.append(traceDO.getClassName()).append("#").append(traceDO.getMethodName());
        sb.append(SPACE).append(traceDO);
        return sb.toString();
    }

    public static void sortByPrefix(List<LogDO> traceLogDOList) {
        Collections.sort(traceLogDOList, new Comparator<LogDO>() {
            @Override
            public int compare(LogDO o1, LogDO o2) {
                return compare(o1.getIndex(), o2.getIndex());
            }

            public int compare(String id1, String id2) {
                String[] localId1 = id1.split("\\.");
                String[] localId2 = id2.split("\\.");
                int compareLen = Math.min(localId1.length, localId2.length);
                for (int i = 0; i < compareLen; i++) {
                    long num1 = Long.parseLong(localId1[i]);
                    long num2 = Long.parseLong(localId2[i]);
                    if (num1 < num2) {
                        return -1;
                    } else if (num1 > num2) {
                        return 1;
                    }
                }
                return localId1.length - localId2.length;
            }
        });
    }

    public static void setTraceIndexFromTracing(String index) {
        // 分布式MDC tracing
        MDC.put(AGENT_LOG_INDEX, index);
    }

    public static String getTraceIndexFromTracing() {
        // 分布式MDC tracing
        String index = MDC.get(AGENT_LOG_INDEX);
        if (index == null || StringUtils.isEmpty(index)) {
            // 获取不到index时候
            return "";
        }
        return index;
    }
}
