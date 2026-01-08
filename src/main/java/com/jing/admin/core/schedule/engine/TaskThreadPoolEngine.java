package com.jing.admin.core.schedule.engine;

import com.jing.admin.core.schedule.AbstractJobTask;
import lombok.Data;

import java.util.concurrent.ExecutorService;

/**
 * 任务线程池引擎 - 提供在线程池中执行任务的功能
 * 
 * @deprecated 建议使用 {@link TaskExecutor}，它提供了更优雅和安全的任务执行方式
 */
@Data
@Deprecated
public class TaskThreadPoolEngine {
    private AbstractJobTask abstractJobTask;
    private long timeOut = 15 * 60 * 1000;
    private ExecutorService executorService;

    public TaskThreadPoolEngine(AbstractJobTask abstractTask, ExecutorService executorService, long timeOut) {
        this.abstractJobTask = abstractTask;
        this.executorService = executorService;
        this.timeOut = timeOut < 0L ? this.timeOut : timeOut;
    }

    public TaskThreadPoolEngine(AbstractJobTask abstractTask, ExecutorService executorService) {
        this.abstractJobTask = abstractTask;
        this.executorService = executorService;
    }

    public void run() {
        executorService.execute(() -> new TaskEngine(abstractJobTask, timeOut).run());
    }
}
