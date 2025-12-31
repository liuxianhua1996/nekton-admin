package com.jing.admin.service;

import com.jing.admin.model.domain.WorkflowNodeLog;
import com.jing.admin.repository.WorkflowNodeLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作流节点执行日志服务类
 */
@Service
public class WorkflowNodeLogService {

    @Autowired
    private WorkflowNodeLogRepository workflowNodeLogRepository;

    /**
     * 保存节点执行日志
     */
    public void saveNodeLog(WorkflowNodeLog nodeLog) {
        workflowNodeLogRepository.save(nodeLog);
    }

    /**
     * 根据工作流实例ID获取节点日志列表
     */
    public List<WorkflowNodeLog> getNodeLogsByInstanceId(String workflowInstanceId) {
        return workflowNodeLogRepository.lambdaQuery()
                .eq(WorkflowNodeLog::getWorkflowInstanceId, workflowInstanceId)
                .orderByAsc(WorkflowNodeLog::getSortOrder)
                .list();
    }

    /**
     * 根据工作流ID获取节点日志列表
     */
    public List<WorkflowNodeLog> getNodeLogsByWorkflowId(String workflowId) {
        return workflowNodeLogRepository.lambdaQuery()
                .eq(WorkflowNodeLog::getWorkflowId, workflowId)
                .orderByAsc(WorkflowNodeLog::getSortOrder)
                .list();
    }

    /**
     * 清除指定工作流实例的节点日志
     */
    public void clearLogsByInstanceId(String workflowInstanceId) {
    }
}