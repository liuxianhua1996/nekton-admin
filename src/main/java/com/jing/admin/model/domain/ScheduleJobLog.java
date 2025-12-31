package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 调度工作流执行记录实体类
 */
@Data
@TableName("tb_schedule_job_log")
public class ScheduleJobLog extends Base implements Serializable {
    private static final long serialVersionUID = 1L;

    private String jobId;

    private String workflowId;

    private String workflowInstanceId;

    private String triggerType;

    private String status;

    private String result;

    private Long startTime;

    private Long endTime;

    private Long executionTime;

    private String errorMessage;
}