package com.jing.admin.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工作流全局参数实体类
 * @author 
 * @date 
 */
@Data
@TableName("tb_workflow_global_param")
public class WorkflowGlobalParam extends Base {
    /**
     * 工作流ID，可为空表示全局参数
     */
    private String workflowId;
    
    /**
     * 参数键
     */
    private String paramKey;
    
    /**
     * 参数值
     */
    private String paramValue;
    
    /**
     * 参数用途类型 (global_variable, db_config, api_config等)
     */
    private String paramType;
    
    /**
     * 值的数据类型 (string, number, boolean, json等)
     */
    private String valueType;
    
    /**
     * 参数描述
     */
    private String description;
}