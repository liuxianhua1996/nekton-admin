package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jing.admin.model.domain.Base;
import lombok.Data;

import java.io.Serializable;

/**
 * 调度工作流实体类
 */
@Data
@TableName("tb_schedule_job")
public class ScheduleJob extends Base implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 调度名称
     */
    @TableField("name")
    private String name;

    /**
     * 工作流ID
     */
    private String workflowId;

    /**
     * 触发方式：cron-定时，webhook-Webhook，mqtt-MQTT
     */
    private String triggerType;

    /**
     * 触发配置(json格式，如cron表达式、webhook地址、MQTT配置等)
     */
    private String triggerConfig;

    /**
     * 状态：DISABLED-停用，ENABLED-启用
     */
    private String status;
    
    /**
     * 描述
     */
    private String description;

}