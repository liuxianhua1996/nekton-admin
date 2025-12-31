package com.jing.admin.core.schedule.job;


import com.alibaba.fastjson2.JSONObject;
import com.jing.admin.core.schedule.AbstractJobTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;


/**
 * @author zhicheng
 * @date 2024/4/15
 **/
@Slf4j
@DisallowConcurrentExecution//避免执行过慢,并发执行
public class JobScheduler extends QuartzJobBean {
    public JobScheduler() {
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        JobDataMap jobDataMap = context.getMergedJobDataMap();
        JobTask jobTask = (JobTask) jobDataMap.get("taskData");
        log.info("执行定时任务：{}", jobTask.getName());
        //创建任务
        jobTask.run();
    }
}
