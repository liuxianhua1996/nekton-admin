package com.jing.admin.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 调度工作流实体类
 */
@Data
@TableName("tb_schedule_job")
public class ScheduleJob implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("name")
    private String name;

    @TableField("workflow_id")
    private String workflowId;

    @TableField("trigger_type")
    private String triggerType;

    @TableField("trigger_config")
    private String triggerConfig;

    @TableField("status")
    private String status;

    @TableField("description")
    private String description;

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