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


    @TableField("name")
    private String name;

    private String workflowId;

    private String triggerType;

    private String triggerConfig;

    private String status;
    private String description;

}