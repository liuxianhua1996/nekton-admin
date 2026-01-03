package com.jing.admin.model.dto;

import lombok.Data;

/**
 * 工作流节点执行日志DTO
 */
@Data
public class WorkflowNodeLogDTO {
    /**
     * 日志ID
     */
    private String id;
    
    /**
     * 工作流实例ID
     */
    private String workflowInstanceId;
    
    /**
     * 工作流ID
     */
    private String workflowId;
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 节点名称
     */
    private String nodeName;
    
    /**
     * 节点类型
     */
    private String nodeType;
    
    /**
     * 执行状态：SUCCESS-成功，FAILED-失败，RUNNING-执行中
     */
    private String status;
    
    /**
     * 节点输入数据
     */
    private String inputData;
    
    /**
     * 节点输出数据
     */
    private String outputData;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 执行耗时(毫秒)
     */
    private Long executionTime;
    
    /**
     * 节点开始执行时间
     */
    private Long startTime;
    
    /**
     * 节点结束执行时间
     */
    private Long endTime;
    
    /**
     * 执行顺序
     */
    private Integer sortOrder;
    
    /**
     * 租户ID
     */
    private String tenantId;
    
    /**
     * 创建时间
     */
    private Long createTime;
    
    /**
     * 更新时间
     */
    private Long updateTime;
    
    /**
     * 创建用户ID
     */
    private String createUserId;
    
    /**
     * 更新用户ID
     */
    private String updateUserId;
    
    /**
     * 创建用户名称
     */
    private String createUserName;
    
    /**
     * 更新用户名称
     */
    private String updateUserName;
}