package com.jing.admin.core.schedule.job;

import com.alibaba.fastjson2.JSONObject;
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

    public Quartz(String jobId, String jobName, String cron, JSONObject job) {
        this.jobId = jobId;
        this.cron = cron;
        this.jobData = new JobDataMap();
        jobData.put("jobName", jobName);
        jobData.put("job", job);
        jobData.put("workflow", job.getJSONObject("workflow"));
    }

    public Quartz(JSONObject job) {
        this(job.getString("jobId"), job.getString("jobName"), job.getString("jobCron"), job);
    }

    public JobDetail restartJob() throws ClassNotFoundException {
        return JobBuilder.newJob(JobScheduler.class).withIdentity(this.jobId).setJobData(this.jobData).storeDurably().build();
    }

    public Trigger restartTrigger() throws ClassNotFoundException {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(this.cron);
        return TriggerBuilder.newTrigger().forJob(restartJob()).withIdentity(this.jobId).withSchedule(scheduleBuilder).build();
    }
}
