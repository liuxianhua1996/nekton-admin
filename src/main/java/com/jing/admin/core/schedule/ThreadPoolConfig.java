package com.jing.admin.core.schedule;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhicheng
 * @date 2024/6/7
 **/
public class ThreadPoolConfig {
    /**
     * JOB任务线程池
     */
    public final static ExecutorService JOB_THREAD_POOL = new ThreadPoolExecutor(30,30, 60, TimeUnit.MINUTES,
            new LinkedTransferQueue <>(),new ThreadFactory() {
        private final AtomicInteger threadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "scheduler-job-" + threadNum.getAndIncrement());
        }
    });

    /**
     * webHook任务线程池
     */
    public final static ExecutorService WEBHOOK_THREAD_POOL = new ThreadPoolExecutor(30,30, 60, TimeUnit.MINUTES,
            new LinkedTransferQueue <>(),new ThreadFactory() {
        private final AtomicInteger threadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "scheduler-job-" + threadNum.getAndIncrement());
        }
    });
}
