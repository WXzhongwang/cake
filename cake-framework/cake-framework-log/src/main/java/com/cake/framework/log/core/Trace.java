package com.cake.framework.log.core;

import com.cake.framework.log.annotation.LogEntry;
import com.cake.framework.log.appender.CakeLogbackFactory;
import com.cake.framework.log.utils.TraceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Trace
 *
 * @author zhongshengwang
 * @description Trace
 * @date 2022/10/29 00:01
 * @email 18668485565163.com
 */
public class Trace {

    private static final Logger logger = CakeLogbackFactory.getInstance().getLogAgentLogger();
    private static boolean isPrintConsole;

    public static void enablePrintConsole() {
        isPrintConsole = true;
    }

    public static void disabledPrintConsole() {
        isPrintConsole = false;
    }

    public static void enableTrack() {
        LogTrackHolder.setTrack(true);
    }

    public static void disabledTrack() {
        LogTrackHolder.setTrack(false);
    }

    public static List<LogDO> getTrackLog() {
        return LogTrackHolder.threadLocal.get().getLogDOList();
    }

    private static final ThreadLocal<LinkedList<Node>> threadLocal = ThreadLocal.withInitial(() -> {
        LinkedList<Node> linkedList = new LinkedList<>();
        // 从全链路日志上下文中获取信息
        String path = TraceUtil.getTraceIndexFromTracing();
        if (StringUtils.isNotEmpty(path)) {
            Node root = new Node();
            long index = Long.parseLong(path.substring(path.lastIndexOf(".") + 1));
            root.resetNextIndex(--index);
            root.setPath(path.substring(0, path.lastIndexOf(".")));
            linkedList.add(root);
            return linkedList;
        }
        Node root = new Node();
        root.setPath("0");
        linkedList.add(root);
        return linkedList;
    });


    public static void startTrace(LogContext logContext) {
        LinkedList<Node> nodes = threadLocal.get();
        Node parent = nodes.peek();

        Node current = new Node();
        long index = parent.inc();
        current.setPath(parent.getPath() + "." + index);
        current.setParent(parent);

        if (current.getBizType() != null) {
            current.setBizType(parent.getBizType());
        } else {
            LogEntry logEntry = logContext.getLogEntry();
            if (logEntry != null) {
                current.setBizType(logContext.getLogEntry().bizType());
            }
        }
        nodes.push(current);
    }

    public static void startRpcTrace() {
        LinkedList<Node> nodes = threadLocal.get();
        Node parent = nodes.peek();
        long index = parent.inc();
        TraceUtil.setTraceIndexFromTracing(parent.getPath() + "." + index);
    }


    public static TraceContext getTraceContext() {
        LinkedList<Node> copyNodes = new LinkedList<>();
        LinkedList<Node> nodes = threadLocal.get();
        copyNodes(nodes, copyNodes);
        return new TraceContext(copyNodes, LogTrackHolder.threadLocal.get());
    }

    private static void copyNodes(LinkedList<Node> source, LinkedList<Node> target) {
        if (source == null || target == null) {
            return;
        }
        target.addAll(source);
    }


    /**
     * 适用于线程场景
     *
     * @param traceContext
     */
    public static void startTrace(TraceContext traceContext) {
        threadLocal.set(traceContext.getNodes());
        LogTrackHolder.threadLocal.set(traceContext.getLogTracker());
    }

    public static void endTrace(LogContext logContext) {
        LinkedList<Node> nodes = threadLocal.get();
        Node current = nodes.pop();
        LogDO logDO = LogBuilder.build(logContext, current);
        LogTracker logTracker = LogTrackHolder.threadLocal.get();
        if (logTracker != null && logTracker.isTrack()) {
            logTracker.add(logDO);
        }
        log(logDO, logContext);
    }

    private static void log(LogDO logDO, LogContext logContext) {
        Map<String, Object> kv = new HashMap<>(2);
        kv.put("params", logDO.getParams());
        kv.put("biType", logDO.getBizType());
        if (isPrintConsole) {
            System.out.println(logDO);
        }
        // 输出全链路日志
        try {
            logger.info(logDO.toString());
        } catch (Throwable e) {

        }
    }
}
