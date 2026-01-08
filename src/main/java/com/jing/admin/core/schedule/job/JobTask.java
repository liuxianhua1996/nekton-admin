package com.jing.admin.core.schedule.job;

import com.jing.admin.core.constant.ConstantEnum;
import com.jing.admin.core.schedule.AbstractJobTask;
import com.jing.admin.core.schedule.engine.TaskExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author zhicheng
 * @date 2024/6/7
 **/
@Slf4j
@Data
public class JobTask  {
    private String id;
    private String name;
    /**
     * 定时时间
     */
    private String coron;

    private AbstractJobTask task;
    
    /**
     * 租户ID，用于多租户环境下的任务执行
     */
    private String tenantId;
    
    /**
     * 执行超时时间
     */
    private long runTimeout = 15 * 60 * 1000;


    /**
     * 设置运行超时时间
     *
     * @param runTimeout
     */
    public void setRunTimeout(long runTimeout) {
        this.runTimeout = runTimeout;
    }

    public void run() {
        log.info("[调度任务] 任务名称: {}, 租户ID: {}", name, tenantId);
        try {
            // 使用新的TaskExecutor执行任务
            TaskExecutor.ExecutionResult result = TaskExecutor.execute(task, runTimeout, TimeUnit.MILLISECONDS);
            
            if (result.isSuccess()) {
                log.info("[调度任务] name: {} 执行成功，耗时: {} ms", name, result.getExecutionTime());
            } else {
                log.warn("[调度任务] name: {} 执行失败: {}", name, result.getMessage());
            }
        } catch (Exception e) {
            log.error("[调度任务] name: {} 发生异常: {}", name, e.getMessage(), e);
        } finally {
            log.info("[调度任务] name: {} 运行结果: {} 结束", name, task.getStatus());
            if (!ConstantEnum.SUCCESS.equals(task.getStatus())) {
            }
        }
    }
}
