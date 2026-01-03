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

    /**
     * 调度任务ID
     */
    private String jobId;

    /**
     * 工作流ID
     */
    private String workflowId;

    private String workflowInstanceId;

    /**
     * 触发方式
     */
    private String triggerType;

    /**
     * 执行状态：SUCCESS-成功，FAILED-失败
     */
    private String status;

    /**
     * 执行结果
     */
    private String result;

    /**
     * 开始执行时间
     */
    private Long startTime;

    /**
     * 结束执行时间
     */
    private Long endTime;

    /**
     * 执行耗时(毫秒)
     */
    private Long executionTime;

    /**
     * 错误信息
     */
    private String errorMessage;
}