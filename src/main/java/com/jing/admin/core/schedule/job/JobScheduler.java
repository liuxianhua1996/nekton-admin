package com.ylys.datacenter.scheduling.job;


import com.alibaba.fastjson2.JSONObject;
import com.ylys.datacenter.entity.scheduling.JobEntity;
import com.ylys.datacenter.entity.workflow.WorkflowEntity;
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
        JobEntity job = (JobEntity) jobDataMap.get("job");
        WorkflowEntity workflow = (WorkflowEntity) jobDataMap.get("workflow");
        //创建任务
        if(job.getStartParams() != null || !StringUtils.equals("",job.getStartParams())){
            new JobTask(job, workflow, JSONObject.parse(job.getStartParams())).run();
        } else {
            new JobTask(job, workflow).run();
        }

    }
}
