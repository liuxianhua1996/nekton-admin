package com.jing.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调度任务执行记录DTO，用于分页查询（关联用户信息）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleJobLogDTO {
    private String id;
    private String jobId;
    private String workflowId;
    private String triggerType;
    private String status;
    private String result;
    private Long startTime;
    private Long endTime;
    private Long executionTime;
    private String errorMessage;
    private String tenantId;
    private Long createTime;
    private Long updateTime;
    private String createUserId;
    private String createUserName;  // 创建用户名称
    private String updateUserId;
    private String updateUserName;  // 更新用户名称
}