package com.cake.framework.cqrs.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author zhongshengwang
 * @description TODO
 * @date 2022/10/26 23:02
 * @email 18668485565163.com
 */
public class ExecutorFactory {

    private static final ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(10, 100, 0,
            TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>(2000), new ThreadPoolExecutor.DiscardOldestPolicy());


    public static DirectExecutor getDirectExecutor() {
        return new DirectExecutor();
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return POOL_EXECUTOR;
    }

    static class DirectExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

}
