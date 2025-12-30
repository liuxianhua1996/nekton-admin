package com.ylys.datacenter.scheduling.job;

import com.alibaba.fastjson2.JSONObject;
import com.ylys.datacenter.common.enums.ConstantEnum;
import com.ylys.datacenter.common.enums.TriggerType;
import com.ylys.datacenter.common.utils.SpringUtil;
import com.ylys.datacenter.scheduling.service.JobService;
import com.ylys.datacenter.workflow.etl.EtlWorkflowTask;
import com.ylys.datacenter.workflow.link.LinkWorkflowTask;
import com.ylys.datacenter.common.abs.AbstractJobTask;
import com.ylys.datacenter.common.enums.workflow.WorkflowTypeEnum;
import com.ylys.datacenter.common.utils.AnnotationUtil;
import com.ylys.datacenter.entity.scheduling.JobEntity;
import com.ylys.datacenter.entity.workflow.WorkflowEntity;
import com.ylys.datacenter.entity.workflow.subsidiary.WorkflowNode;
import com.ylys.datacenter.scheduling.engine.TaskEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhicheng
 * @date 2024/6/7
 **/
@Slf4j
public class JobTask {
    /**
     * 执行超时时间
     */
    private long runTimeout = 15 * 60 * 1000;
    /**
     * 作业实体
     */
    private JobEntity job;
    /**
     * 节点实体
     */
    private WorkflowEntity workflow;
    /**
     * 启动参数
     */
    private JSONObject startParams;
    private JobService jobService;

    /**
     * 设置运行超时时间
     * @param runTimeout
     */
    public void setRunTimeout(long runTimeout) {
        this.runTimeout = runTimeout;
    }

    public JobTask(JobEntity job, WorkflowEntity workflow) {
        this.job = job;
        this.workflow = workflow;
        this.jobService = SpringUtil.getBean(JobService.class);
    }
    public JobTask(JobEntity job, WorkflowEntity workflow, JSONObject startParams) {
        this.job = job;
        this.workflow = workflow;
        this.startParams = startParams;
        this.jobService = SpringUtil.getBean(JobService.class);
    }
    public void run() {
        log.info("[调度任务] 任务名称: {}", job.getJobName());
        AbstractJobTask task = null;
        try {
            //创建任务
            switch (WorkflowTypeEnum.toEnum(workflow.getWorkflowType())) {
                case ETL -> {
                    task = new EtlWorkflowTask(job, (List<WorkflowNode>) AnnotationUtil.getJsonMapping(workflow, "nodes"),this.startParams);
                }
                case LINK -> {
                    task = new LinkWorkflowTask(job, (List<WorkflowNode>) AnnotationUtil.getJsonMapping(workflow, "nodes"),this.startParams);
                }
            }
            //创建执行引擎
            new TaskEngine(task, runTimeout).run();
        } catch (Exception e) {
            log.info("[调度任务] name: {} 异常: {}", job.getJobName(), e.getMessage());
        } finally {
            log.info("[调度任务] name: {} 运行结果: {} 结束", job.getJobName(), task.getStatus());
            if(!ConstantEnum.SUCCESS.equals(task.getStatus())){
                this.jobService.sendMessage(task, TriggerType.ERROR);
            }
        }
    }
}
