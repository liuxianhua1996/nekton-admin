package com.jing.admin.service;

import com.jing.admin.model.domain.Workflow;

import java.util.List;

/**
 * 工作流服务接口
 * @author lxh
 * @date 2025/9/19
 */
public interface WorkflowService {
    /**
     * 保存或更新工作流
     * @param workflow 工作流对象
     * @return 保存后的工作流对象
     */
    Workflow saveOrUpdateWorkflow(Workflow workflow);
    
    /**
     * 根据ID获取工作流
     * @param id 工作流ID
     * @return 工作流对象
     */
    Workflow getWorkflowById(String id);
    
    /**
     * 获取所有工作流
     * @return 工作流列表
     */
    List<Workflow> getAllWorkflows();
    
    /**
     * 删除工作流
     * @param id 工作流ID
     * @return 是否删除成功
     */
    boolean deleteWorkflow(String id);
}