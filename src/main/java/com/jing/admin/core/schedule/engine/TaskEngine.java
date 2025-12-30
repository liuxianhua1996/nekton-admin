package com.ylys.datacenter.scheduling.engine;

import com.ylys.datacenter.common.abs.AbstractJobTask;
import com.ylys.datacenter.scheduling.common.ThreadPoolConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Data
@Slf4j
public class TaskEngine {
    private AbstractJobTask abstractTask;
    private long timeOut;

    /**
     * @param abstractTask
     * @param timeOut      unit:ms
     */
    public TaskEngine(AbstractJobTask abstractTask, long timeOut) {
        Assert.notNull(abstractTask, "task cannot be NULL");
        Assert.isTrue(timeOut > 0L, "timeOut cannot be negative or less than 0");
        this.abstractTask = abstractTask;
        this.timeOut = timeOut;
    }

    public TaskEngine(AbstractJobTask abstractTask) {
        this(abstractTask, 1 * 60 * 60);
    }

    public void run() {
        String result = "fail";
        Future<String> future = ThreadPoolConfig.JOB_THREAD_POOL.submit(abstractTask, "success");
        try {
            result = future.get(timeOut, TimeUnit.SECONDS);
        } catch (TimeoutException timeoutException) {
            throw new RuntimeException(String.format("run for more than %d seconds", timeOut));
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s", e.getMessage()));
        } finally {
            future.cancel(true);//取消线程执行
        }
    }
}
