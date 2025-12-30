package com.ylys.datacenter.scheduling.job;

import com.ylys.datacenter.entity.scheduling.JobEntity;
import com.ylys.datacenter.entity.workflow.WorkflowEntity;
import lombok.Getter;
import org.quartz.*;

/**
 * @author zhicheng
 * @date 2024/4/15
 **/
@Getter
public class Quartz {
    private String jobId;
    private String cron;
    private JobDataMap jobData;

    public Quartz(String jobId, String jobName, String cron, JobEntity job, WorkflowEntity workflow) {
        this.jobId = jobId;
        this.cron = cron;
        this.jobData = new JobDataMap();
        jobData.put("jobName", jobName);
        jobData.put("job", job);
        jobData.put("workflow", workflow);
    }

    public Quartz(JobEntity job, WorkflowEntity workflow) {
        this(job.getId(), job.getJobName(), job.getJobCron(), job, workflow);
    }

    public JobDetail restartJob() throws ClassNotFoundException {
        return JobBuilder.newJob(JobScheduler.class).withIdentity(this.jobId).setJobData(this.jobData).storeDurably().build();
    }

    public Trigger restartTrigger() throws ClassNotFoundException {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(this.cron);
        return TriggerBuilder.newTrigger().forJob(restartJob()).withIdentity(this.jobId).withSchedule(scheduleBuilder).build();
    }
}
