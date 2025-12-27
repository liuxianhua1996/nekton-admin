package com.jing.admin.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jing.admin.core.PageResult;
import com.jing.admin.core.exception.BusinessException;
import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import com.jing.admin.core.workflow.WorkflowExecutor;
import com.jing.admin.core.workflow.model.NodeResult;
import com.jing.admin.model.api.WorkflowQueryRequest;
import com.jing.admin.model.api.WorkflowRequest;
import com.jing.admin.model.api.WorkflowTestRequest;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.dto.TestWorkflowDTO;
import com.jing.admin.model.dto.WorkflowDTO;
import com.jing.admin.repository.WorkflowRepository;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private WorkflowExecutor workflowExecutor;

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
        JSONObject jsonData = workflowRequest.getJsonData();
        workflow.setId(workflowRequest.getId());
        workflow.setJsonData(jsonData.toJSONString());
        workflow.setCreateTime(System.currentTimeMillis());
        workflow.setUpdateTime(workflow.getCreateTime());
        workflow.setVersion(workflowRequest.getVersion()+1);
        workflow.setUpdateUserId(MDC.get("userId"));
        int success = workflowRepository.updateWorkflow(workflow,workflowRequest.getVersion());
        if(success == 0){
            Workflow lastWorkflow = workflowRepository.getById(workflow.getId());
            if(lastWorkflow == null){
                throw new BusinessException("工作流不存在");
            } else {
                throw new BusinessException("版本不一致更新失败");
            }

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
        IPage<WorkflowDTO> workflowPage = workflowRepository.selectWorkflowPageWithUser(
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
     * 测试执行工作流
     *
     * @param workflowTestRequest 工作流测试请求
     * @return 工作流执行结果
     */
    public TestWorkflowDTO testWorkflow(WorkflowTestRequest workflowTestRequest) {
        // 获取工作流信息
        Workflow workflow = workflowRepository.getById(workflowTestRequest.getId());
        if (workflow == null) {
            throw new BusinessException("工作流不存在");
        }
        // 获取测试参数
        Map<String, Object> params = new HashMap<>();
        if (workflowTestRequest.getParams() != null) {
            params = workflowTestRequest.getParams();
        }
        TestWorkflowDTO testWorkflowDTO = new TestWorkflowDTO();
        List<TestWorkflowDTO.NodeTestResult> nodeTestResults = new ArrayList();
        WorkflowExecutionResult workflowExecutionResult = workflowExecutor.executeFromJsonByWorkflowId(workflowTestRequest.getId(), params);
        Map<String, NodeResult> nodeResultMap = workflowExecutionResult.getContext().getNodeResults();
        nodeResultMap.forEach((id,nodeTestResult)->{
            nodeTestResults.add(TestWorkflowDTO.NodeTestResult.builder()
                            .nodeId(nodeTestResult.getNodeId())
                            .nodeName(nodeTestResult.getNodeName())
                            .status("COMPLETED")
                            .sort(nodeTestResult.getSort())
                            .executeResult(nodeTestResult.getExecuteResult())
                    .build());
        });
        testWorkflowDTO.setNodeTestResults(nodeTestResults);
        // 执行工作流
        return testWorkflowDTO;
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