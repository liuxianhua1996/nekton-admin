package com.jing.admin.core.schedule.job;

import com.jing.admin.core.constant.ConstantEnum;
import com.jing.admin.core.schedule.AbstractJobTask;
import com.jing.admin.core.schedule.engine.TaskEngine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
            //创建任务
            //创建执行引擎
            new TaskEngine(task, runTimeout).run();
        } catch (Exception e) {
            log.info("[调度任务] name: {} 异常: {}", name, e.getMessage());
        } finally {
            log.info("[调度任务] name: {} 运行结果: {} 结束", name, task.getStatus());
            if (!ConstantEnum.SUCCESS.equals(task.getStatus())) {
            }
        }
    }
}
