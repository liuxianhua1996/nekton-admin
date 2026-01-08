package com.jing.admin.core.schedule.engine;

import com.jing.admin.core.schedule.AbstractJobTask;
import com.jing.admin.core.schedule.ThreadPoolConfig;
import com.jing.admin.core.tenant.TenantContextWrapper;
import com.jing.admin.core.tenant.TenantContextHolder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 任务引擎 - 提供任务执行功能
 * 
 * @deprecated 建议使用 {@link TaskExecutor}，它提供了更优雅和安全的任务执行方式
 */
@Data
@Slf4j
@Deprecated
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
        this(abstractTask, 1 * 60 * 1000); // 修正：默认1小时，单位为毫秒
    }

    public void run() {
        String result = "fail";
        // 获取当前线程的租户ID，用于传递给执行线程
        String currentTenantId = TenantContextHolder.getTenantId();
        
        // 包装任务以确保在执行线程中保持租户上下文
        Runnable wrappedTask = TenantContextWrapper.wrap(() -> {
            try {
                abstractTask.run();
            } catch (Exception e) {
                log.error("执行任务时发生异常: ", e);
                throw e;
            }
        }, currentTenantId);
        
        Future<String> future = ThreadPoolConfig.JOB_THREAD_POOL.submit(wrappedTask, "success");
        try {
            // 修正：使用毫秒作为时间单位
            result = future.get(timeOut, TimeUnit.MILLISECONDS);
        } catch (TimeoutException timeoutException) {
            throw new RuntimeException(String.format("run for more than %d ms", timeOut));
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s", e.getMessage()));
        } finally {
            // 仅在任务未完成时才取消
            if (!future.isDone()) {
                future.cancel(true); // 取消线程执行
            }
        }
    }
}
