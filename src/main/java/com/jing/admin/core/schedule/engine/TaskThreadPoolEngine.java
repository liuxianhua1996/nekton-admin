package com.ylys.datacenter.scheduling.engine;

import com.ylys.datacenter.common.abs.AbstractJobTask;
import lombok.Data;

import java.util.concurrent.ExecutorService;

@Data
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
