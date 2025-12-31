package com.jing.admin.core.schedule.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jing.admin.core.constant.ConstantEnum;
import com.jing.admin.core.schedule.AbstractJobTask;
import com.jing.admin.core.schedule.engine.TaskEngine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
        log.info("[调度任务] 任务名称: {}", name);
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
