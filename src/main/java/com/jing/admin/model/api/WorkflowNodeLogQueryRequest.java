package com.jing.admin.model.api;

import lombok.Data;

/**
 * 工作流节点执行日志查询请求参数
 */
@Data
public class WorkflowNodeLogQueryRequest {
    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 每页显示条数
     */
    private Long size = 10L;

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
     * 状态
     */
    private String status;
}