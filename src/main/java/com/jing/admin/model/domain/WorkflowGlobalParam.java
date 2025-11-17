package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工作流全局参数实体类
 * 用于存储工作流的全局参数配置，包括参数类型和具体的配置信息
 * 
 * @author lxh
 * @date 2025/9/19
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_workflow_global_param")
public class WorkflowGlobalParam extends Base {
    
    /**
     * 参数类型
     * 用于区分不同类型的全局参数，如：数据库连接、API配置、邮件配置等
     */
    @TableField("param_type")
    private String paramType;
    
    /**
     * 参数配置（JSON字符串）
     * 存储具体的参数配置信息，以JSON格式保存
     * 例如：{"host":"localhost","port":3306,"username":"root"}
     */
    @TableField("param_config")
    private String paramConfig;
}