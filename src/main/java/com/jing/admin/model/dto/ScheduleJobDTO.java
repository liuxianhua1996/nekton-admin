package com.jing.admin.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调度任务DTO，用于分页查询（关联用户信息）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleJobDTO {
    private String id;
    private String name;
    private String workflowId;
    private String triggerType;
    private String triggerConfig;
    private String status;
    private String description;
    private String tenantId;
    private Long createTime;
    private Long updateTime;
    private String createUserId;
    private String createUserName;  // 创建用户名称
    private String updateUserId;
    private String updateUserName;  // 更新用户名称
}