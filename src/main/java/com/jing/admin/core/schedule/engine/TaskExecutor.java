package com.jing.admin.core.schedule.engine;

import com.jing.admin.core.schedule.AbstractJobTask;
import com.jing.admin.core.schedule.ThreadPoolConfig;
import com.jing.admin.core.tenant.TenantContextWrapper;
import com.jing.admin.core.tenant.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 任务执行器 - 提供更优雅的任务执行方式
 * 解决了原TaskEngine和TaskThreadPoolEngine中的问题
 */
@Slf4j
public class TaskExecutor {
    
    /**
     * 任务执行结果
     */
    public static class ExecutionResult {
        private final boolean success;
        private final String message;
        private final long executionTime;
        private final Throwable error;
        
        public ExecutionResult(boolean success, String message, long executionTime, Throwable error) {
            this.success = success;
            this.message = message;
            this.executionTime = executionTime;
            this.error = error;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public long getExecutionTime() {
            return executionTime;
        }
        
        public Throwable getError() {
            return error;
        }
    }
    
    /**
     * 执行任务并返回结果
     * 
     * @param task 任务
     * @param timeout 超时时间
     * @param unit 超时时间单位
     * @return 任务执行结果
     */
    public static ExecutionResult execute(AbstractJobTask<?, ?> task, long timeout, TimeUnit unit) {
        String currentTenantId = TenantContextHolder.getTenantId();
        
        // 包装任务以确保在执行线程中保持租户上下文
        Runnable wrappedTask = TenantContextWrapper.wrap(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("执行任务时发生异常: ", e);
                throw e;
            }
        }, currentTenantId);
        
        Future<String> future = ThreadPoolConfig.JOB_THREAD_POOL.submit(wrappedTask, "success");
        
        long startTime = System.currentTimeMillis();
        try {
            String result = future.get(timeout, unit);
            long executionTime = System.currentTimeMillis() - startTime;
            return new ExecutionResult(true, result, executionTime, null);
        } catch (TimeoutException timeoutException) {
            log.warn("任务执行超时: {}", task.getTaskName());
            future.cancel(true); // 取消线程执行
            long executionTime = System.currentTimeMillis() - startTime;
            return new ExecutionResult(false, 
                String.format("任务执行超时超过 %d %s", timeout, unit.name()), 
                executionTime, 
                timeoutException);
        } catch (ExecutionException executionException) {
            log.error("任务执行异常: ", executionException.getCause());
            future.cancel(true); // 取消线程执行
            long executionTime = System.currentTimeMillis() - startTime;
            return new ExecutionResult(false, 
                executionException.getCause().getMessage(), 
                executionTime, 
                executionException.getCause());
        } catch (InterruptedException interruptedException) {
            log.warn("任务执行被中断: {}", task.getTaskName());
            Thread.currentThread().interrupt(); // 恢复中断状态
            future.cancel(true); // 取消线程执行
            long executionTime = System.currentTimeMillis() - startTime;
            return new ExecutionResult(false, 
                "任务执行被中断", 
                executionTime, 
                interruptedException);
        } finally {
            // 如果任务尚未完成，则取消它
            if (!future.isDone()) {
                future.cancel(true);
            }
        }
    }
    
    /**
     * 执行任务，使用默认超时时间（15分钟）
     * 
     * @param task 任务
     * @return 任务执行结果
     */
    public static ExecutionResult execute(AbstractJobTask<?, ?> task) {
        return execute(task, 15, TimeUnit.MINUTES);
    }
    
    /**
     * 在指定线程池中异步执行任务
     * 
     * @param task 任务
     * @param executorService 线程池
     * @param timeout 超时时间
     * @param unit 超时时间单位
     * @return Future对象，可用于获取执行结果
     */
    public static Future<ExecutionResult> executeAsync(AbstractJobTask<?, ?> task, 
                                                      ExecutorService executorService, 
                                                      long timeout, 
                                                      TimeUnit unit) {
        return executorService.submit(() -> execute(task, timeout, unit));
    }
    
    /**
     * 在指定线程池中异步执行任务，使用默认超时时间（15分钟）
     * 
     * @param task 任务
     * @param executorService 线程池
     * @return Future对象，可用于获取执行结果
     */
    public static Future<ExecutionResult> executeAsync(AbstractJobTask<?, ?> task, 
                                                      ExecutorService executorService) {
        return executeAsync(task, executorService, 15, TimeUnit.MINUTES);
    }
}