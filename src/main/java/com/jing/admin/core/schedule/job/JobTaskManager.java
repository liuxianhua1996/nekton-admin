package com.jing.admin.core.schedule.job;

import com.alibaba.fastjson2.JSON;
import com.jing.admin.core.schedule.AbstractJobTask;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务管理器，用于管理基于 schedule/job 包的定时任务
 */
@Slf4j
@Component
public class JobTaskManager {

    @Autowired
    private Scheduler scheduler;

    // 存储任务ID和任务的映射
    private final Map<String, JobTask> jobTaskMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("初始化任务管理器");
    }

    /**
     * 添加并启动一个 cron 类型的定时任务
     * @param taskId 任务ID
     * @param taskName 任务名称
     * @param cronExpression cron表达式
     * @param jobTask 具体的作业任务
     */
    public void addCronJob(String taskId, String taskName, String cronExpression, AbstractJobTask jobTask) {
        addCronJob(taskId, taskName, cronExpression, jobTask, null);
    }

    /**
     * 添加并启动一个 cron 类型的定时任务（支持租户）
     * @param taskId 任务ID
     * @param taskName 任务名称
     * @param cronExpression cron表达式
     * @param jobTask 具体的作业任务
     * @param tenantId 租户ID
     */
    public void addCronJob(String taskId, String taskName, String cronExpression, AbstractJobTask jobTask, String tenantId) {
        try {
            // 创建 JobTask
            JobTask jobTaskObj = new JobTask();
            jobTaskObj.setId(taskId);
            jobTaskObj.setName(taskName);
            jobTaskObj.setCoron(cronExpression);
            jobTaskObj.setTask(jobTask);
            jobTaskObj.setTenantId(tenantId);

            // 创建 Quartz 任务
            Quartz quartz = new Quartz(jobTaskObj);
            // 检查是否已存在相同任务，如果存在则先删除
            if (scheduler.checkExists(JobKey.jobKey(taskId))) {
                scheduler.deleteJob(JobKey.jobKey(taskId));
            }
            // 调度任务
            scheduler.scheduleJob(quartz.restartJob(), quartz.restartTrigger());
            log.info("成功添加 cron 任务: {}, 租户ID: {}, cron表达式: {}", taskId, tenantId, cronExpression);

            // 保存到映射中
            jobTaskMap.put(taskId, jobTaskObj);
        } catch (Exception e) {
            log.error("添加 cron 任务失败: {}", e.getMessage(), e);
            throw new RuntimeException("添加 cron 任务失败", e);
        }
    }

    /**
     * 暂停任务
     * @param taskId 任务ID
     */
    public void pauseJob(String taskId) {
        try {
            scheduler.pauseJob(JobKey.jobKey(taskId));
            log.info("暂停任务: {}", taskId);
        } catch (SchedulerException e) {
            log.error("暂停任务失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 恢复任务
     * @param taskId 任务ID
     */
    public void resumeJob(String taskId) {
        try {
            scheduler.resumeJob(JobKey.jobKey(taskId));
            log.info("恢复任务: {}", taskId);
        } catch (SchedulerException e) {
            log.error("恢复任务失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 删除任务
     * @param taskId 任务ID
     */
    public void deleteJob(String taskId) {
        try {
            scheduler.deleteJob(JobKey.jobKey(taskId));
            jobTaskMap.remove(taskId);
            log.info("删除任务: {}", taskId);
        } catch (SchedulerException e) {
            log.error("删除任务失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 立即执行任务
     * @param taskId 任务ID
     */
    public void executeJobNow(String taskId) {
        try {
            scheduler.triggerJob(JobKey.jobKey(taskId));
            log.info("立即执行任务: {}", taskId);
        } catch (SchedulerException e) {
            log.error("立即执行任务失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取任务
     * @param taskId 任务ID
     * @return 任务对象
     */
    public JobTask getJobTask(String taskId) {
        return jobTaskMap.get(taskId);
    }
}