package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.repository.WorkflowRepository;
import com.jing.admin.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 工作流服务实现类
 * @author lxh
 * @date 2025/9/19
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {
    
    @Autowired
    private WorkflowRepository workflowRepository;
    
    /**
     * 保存或更新工作流
     * @param workflow 工作流对象
     * @return 保存后的工作流对象
     */
    @Override
    public Workflow saveOrUpdateWorkflow(Workflow workflow) {
        // 如果是新增，设置ID和创建时间
        if (workflow.getId() == null || workflow.getId().isEmpty()) {
            workflow.setId(UUID.randomUUID().toString().replace("-", ""));
            workflow.setCreateTime(System.currentTimeMillis());
            if (workflow.getVersion() == null) {
                workflow.setVersion(1);
            }
            if (workflow.getStatus() == null) {
                workflow.setStatus(0); // 默认草稿状态
            }
        }
        // 更新修改时间
        workflow.setUpdateTime(System.currentTimeMillis());
        workflowRepository.saveOrUpdate(workflow);
        return workflow;
    }
    
    /**
     * 根据ID获取工作流
     * @param id 工作流ID
     * @return 工作流对象
     */
    @Override
    public Workflow getWorkflowById(String id) {
        return workflowRepository.getById(id);
    }
    
    /**
     * 获取所有工作流
     * @return 工作流列表
     */
    @Override
    public List<Workflow> getAllWorkflows() {
        return workflowRepository.list();
    }
    
    /**
     * 删除工作流
     * @param id 工作流ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteWorkflow(String id) {
        return workflowRepository.removeById(id);
    }
}