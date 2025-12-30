package com.jing.admin.core.schedule.job;


import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
        JSONObject job = (JSONObject) jobDataMap.get("job");
        JSONObject workflow = (JSONObject) jobDataMap.get("workflow");
        //创建任务
        //new JobTask(job, workflow).run();

    }
}
