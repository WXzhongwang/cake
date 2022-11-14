package com.cake.framework.log.core;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/29 00:38
 * @email 18668485565163.com
 */
public class LogTrackHolder {

    public static final ThreadLocal<LogTracker> threadLocal = ThreadLocal.withInitial(LogTracker::new);

    public static void setTrack(boolean track) {
        threadLocal.get().setTrack(track);
    }
}
