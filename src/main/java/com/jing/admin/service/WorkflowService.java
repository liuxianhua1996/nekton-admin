package com.jing.admin.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.core.PageResult;
import com.jing.admin.mapper.WorkflowCustomMapper;
import com.jing.admin.model.api.WorkflowQueryRequest;
import com.jing.admin.model.api.WorkflowRequest;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.dto.WorkflowDTO;
import com.jing.admin.repository.WorkflowRepository;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 工作流服务实现类
 *
 * @author lxh
 * @date 2025/9/19
 */
@Service
public class WorkflowService {

    @Autowired
    private WorkflowRepository workflowRepository;
    
    @Autowired
    private WorkflowCustomMapper workflowCustomMapper;

    /**
     * 保存或更新工作流
     *
     * @param workflowRequest 工作流对象
     * @return 保存后的工作流对象
     */
    public Workflow createWorkflow(WorkflowRequest workflowRequest) {
        // 如果是新增，设置ID和创建时间
        Workflow workflow = new Workflow();
        JSONObject jsonData = new JSONObject();
        workflow.setName(workflowRequest.getName());
        workflow.setJsonData(jsonData.toJSONString());
        workflow.setCreateTime(System.currentTimeMillis());
        workflow.setUpdateTime(workflow.getCreateTime());
        workflow.setVersion(1);
        workflow.setStatus("1");
        workflow.setDescription(workflowRequest.getDescription());
        workflow.setCreateUserId(MDC.get("userId"));
        workflow.setUpdateUserId(MDC.get("userId"));
        workflowRepository.save(workflow);
        return workflow;
    }

    /**
     * 保存或更新工作流
     *
     * @param workflowRequest 工作流对象
     * @return 保存后的工作流对象
     */
    public Workflow updateWorkflow(WorkflowRequest workflowRequest) {
        // 如果是新增，设置ID和创建时间
        Workflow workflow = new Workflow();
        JSONObject jsonData = new JSONObject().fluentPut("nodes", workflowRequest.getNodes()).fluentPut("edges", workflowRequest.getEdges());
        workflow.setName(workflowRequest.getName());
        workflow.setJsonData(jsonData.toJSONString());
        workflow.setCreateTime(System.currentTimeMillis());
        workflow.setUpdateTime(workflow.getCreateTime());
        workflow.setVersion(workflowRequest.getVersion());
        workflow.setUpdateUserId(MDC.get("userId"));
        int success = workflowRepository.updateWorkflow(workflow);
        if(success == 0){
            throw new RuntimeException("版本可能不一致更新失败");
        }
        return workflow;
    }

    /**
     * 根据ID获取工作流
     *
     * @param id 工作流ID
     * @return 工作流对象
     */
    public Workflow getWorkflowById(String id) {
        return workflowRepository.getById(id);
    }

    /**
     * 获取所有工作流
     *
     * @return 工作流列表
     */
    public List<Workflow> getAllWorkflows() {
        return workflowRepository.list();
    }

    /**
     * 分页查询工作流
     *
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    public PageResult<WorkflowDTO> getWorkflowPage(WorkflowQueryRequest queryRequest) {
        // 创建分页对象
        Page<WorkflowDTO> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());
        
        // 执行分页查询（关联用户表）
        IPage<WorkflowDTO> workflowPage = workflowCustomMapper.selectWorkflowPageWithUser(
                page,
                queryRequest
        );

        // 构建分页结果
        PageResult<WorkflowDTO> pageResult = PageResult.of(
                workflowPage.getRecords(),
                workflowPage.getTotal(),
                workflowPage.getCurrent(),
                workflowPage.getSize()
        );

        return pageResult;
    }

    public Workflow getWorkflowInfo(WorkflowQueryRequest workflowQueryRequest){
        return workflowRepository.getById(workflowQueryRequest.getId());
    }

    /**
     * 删除工作流
     *
     * @param id 工作流ID
     * @return 是否删除成功
     */
    public boolean deleteWorkflow(String id) {
        return workflowRepository.removeById(id);
    }
}