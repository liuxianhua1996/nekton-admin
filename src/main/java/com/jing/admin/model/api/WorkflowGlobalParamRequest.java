package com.jing.admin.model.api;

import com.jing.admin.model.domain.WorkflowGlobalParam;
import lombok.Data;

import java.util.List;

/**
 * 工作流全局参数请求类
 * @author 
 * @date 
 */
@Data
public class WorkflowGlobalParamRequest {
    
    /**
     * 参数ID
     */
    private String id;
    
    /**
     * 参数键
     */
    private String paramKey;
    
    /**
     * 参数值
     */
    private String paramValue;
    
    /**
     * 参数用途类型 (global_variable, db_config, api_config)
     */
    private String paramType;
    
    /**
     * 值的数据类型 (string, number, boolean, json)
     */
    private String valueType;
    
    /**
     * 工作流ID
     */
    private String workflowId;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 批量操作参数列表
     */
    private List<WorkflowGlobalParam> params;
    
    /**
     * 页码
     */
    private int pageNum = 1;
    
    /**
     * 每页数量
     */
    private int pageSize = 10;
}