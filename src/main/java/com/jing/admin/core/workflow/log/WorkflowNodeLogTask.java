package com.jing.admin.core.workflow.log;

import com.jing.admin.model.domain.WorkflowNodeLog;

/**
 * 工作流节点日志任务类
 * 用于异步处理工作流节点执行日志的保存
 */
public class WorkflowNodeLogTask {
    private WorkflowNodeLog nodeLog;
    private LogOperationType operationType;

    public enum LogOperationType {
        INSERT,     // 插入新日志
        UPDATE     // 更新日志
    }

    public WorkflowNodeLogTask(WorkflowNodeLog nodeLog, LogOperationType operationType) {
        this.nodeLog = nodeLog;
        this.operationType = operationType;
    }

    public WorkflowNodeLog getNodeLog() {
        return nodeLog;
    }

    public LogOperationType getOperationType() {
        return operationType;
    }
}