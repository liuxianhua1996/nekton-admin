package com.jing.admin.service;

import com.jing.admin.model.domain.WorkflowNodeLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 工作流节点执行日志服务接口
 */
public interface WorkflowNodeLogService extends IService<WorkflowNodeLog> {
    
    /**
     * 保存节点执行日志
     */
    void saveNodeLog(WorkflowNodeLog nodeLog);

    /**
     * 根据工作流实例ID获取节点日志列表
     */
    List<WorkflowNodeLog> getNodeLogsByInstanceId(String workflowInstanceId);

    /**
     * 根据工作流ID获取节点日志列表
     */
    List<WorkflowNodeLog> getNodeLogsByWorkflowId(String workflowId);

    /**
     * 清除指定工作流实例的节点日志
     */
    void clearLogsByInstanceId(String workflowInstanceId);
}