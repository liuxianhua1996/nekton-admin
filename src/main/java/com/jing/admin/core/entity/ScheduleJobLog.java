package com.jing.admin.core.entity;

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
public class ScheduleJobLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("job_id")
    private String jobId;

    @TableField("workflow_id")
    private String workflowId;

    @TableField("trigger_type")
    private String triggerType;

    @TableField("status")
    private String status;

    @TableField("result")
    private String result;

    @TableField("start_time")
    private Long startTime;

    @TableField("end_time")
    private Long endTime;

    @TableField("execution_time")
    private Long executionTime;

    @TableField("error_message")
    private String errorMessage;

    @TableField("tenant_id")
    private String tenantId;

    @TableField("create_time")
    private Long createTime;

    @TableField("update_time")
    private Long updateTime;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("update_user_id")
    private String updateUserId;
}