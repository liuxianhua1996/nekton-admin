package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.model.domain.WorkflowNodeLog;
import com.jing.admin.mapper.WorkflowNodeLogMapper;
import com.jing.admin.repository.WorkflowNodeLogRepository;
import com.jing.admin.service.WorkflowNodeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作流节点执行日志服务实现类
 */
@Service
public class WorkflowNodeLogServiceImpl extends ServiceImpl<WorkflowNodeLogMapper, WorkflowNodeLog> implements WorkflowNodeLogService {

    @Autowired
    private WorkflowNodeLogRepository workflowNodeLogRepository;

    /**
     * 保存节点执行日志
     */
    @Override
    public void saveNodeLog(WorkflowNodeLog nodeLog) {
        workflowNodeLogRepository.save(nodeLog);
    }

    /**
     * 根据工作流实例ID获取节点日志列表
     */
    @Override
    public List<WorkflowNodeLog> getNodeLogsByInstanceId(String workflowInstanceId) {
        return workflowNodeLogRepository.lambdaQuery()
                .eq(WorkflowNodeLog::getWorkflowInstanceId, workflowInstanceId)
                .orderByAsc(WorkflowNodeLog::getSortOrder)
                .list();
    }

    /**
     * 根据工作流ID获取节点日志列表
     */
    @Override
    public List<WorkflowNodeLog> getNodeLogsByWorkflowId(String workflowId) {
        return workflowNodeLogRepository.lambdaQuery()
                .eq(WorkflowNodeLog::getWorkflowId, workflowId)
                .orderByAsc(WorkflowNodeLog::getSortOrder)
                .list();
    }

    @Override
    public void clearLogsByInstanceId(String workflowInstanceId) {

    }
}