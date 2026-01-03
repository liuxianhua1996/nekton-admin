package com.jing.admin.core.schedule.job;

import lombok.Getter;
import org.quartz.*;

/**
 * @author zhicheng
 * @date 2024/4/15
 **/
@Getter
public class Quartz {
    private JobTask jobTask;
    private JobDataMap jobData;

    public Quartz(JobTask jobTask) {
        this.jobTask = jobTask;
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("taskData", jobTask);
        
        // Add tenant ID to job data if available
        if (jobTask.getTenantId() != null) {
            jobDataMap.put("tenantId", jobTask.getTenantId());
        }
        
        jobData = jobDataMap;
    }
    public JobDetail restartJob() throws ClassNotFoundException {
        return JobBuilder.newJob(JobScheduler.class).withIdentity(this.jobTask.getId()).setJobData(this.jobData).storeDurably().build();
    }

    public Trigger restartTrigger() throws ClassNotFoundException {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule((this.jobTask.getCoron()));
        return TriggerBuilder.newTrigger().forJob(restartJob()).withIdentity(this.jobTask.getId()).withSchedule(scheduleBuilder).build();
    }
}
