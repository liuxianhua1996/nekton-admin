package com.jing.admin.service;

import com.alibaba.fastjson2.JSONObject;
import com.jing.admin.model.api.WorkflowRequest;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.mapping.WorkflowMapping;
import com.jing.admin.repository.WorkflowRepository;
import org.slf4j.MDC;
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
public class WorkflowService {
    
    @Autowired
    private WorkflowRepository workflowRepository;
    
    /**
     * 保存或更新工作流
     * @param workflow 工作流对象
     * @return 保存后的工作流对象
     */
    public Workflow saveOrUpdateWorkflow(WorkflowRequest workflowRequest) {
        // 如果是新增，设置ID和创建时间
        Workflow workflow = new Workflow();
        if (workflowRequest.getId() == null || workflowRequest.getId().isEmpty()) {
            JSONObject jsonData = new JSONObject().fluentPut("nodes",workflowRequest.getNodes()).fluentPut("edges",workflowRequest.getEdges());
            workflow.setName(workflowRequest.getName());
            workflow.setJsonData(jsonData.toJSONString());
            workflow.setCreateTime(System.currentTimeMillis());
            workflow.setUpdateTime(workflow.getCreateTime());
            workflow.setVersion(1);
            workflow.setStatus(1);
            workflow.setDescription("");
            workflow.setCreateUserId(MDC.get("userId"));
            workflow.setUpdateUserId(MDC.get("userId"));
            workflowRepository.save(workflow);
        }
        return workflow;
    }
    
    /**
     * 根据ID获取工作流
     * @param id 工作流ID
     * @return 工作流对象
     */
    public Workflow getWorkflowById(String id) {
        return workflowRepository.getById(id);
    }
    
    /**
     * 获取所有工作流
     * @return 工作流列表
     */
    public List<Workflow> getAllWorkflows() {
        return workflowRepository.list();
    }
    
    /**
     * 删除工作流
     * @param id 工作流ID
     * @return 是否删除成功
     */
    public boolean deleteWorkflow(String id) {
        return workflowRepository.removeById(id);
    }
}