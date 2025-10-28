package com.jing.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jing.admin.core.HttpResult;
import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.WorkflowQueryRequest;
import com.jing.admin.model.api.WorkflowRequest;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.dto.WorkflowDTO;
import com.jing.admin.model.mapping.WorkflowMapping;
import com.jing.admin.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流控制器
 *
 * @author lxh
 * @date 2025/9/19
 */
@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    /**
     * 创建工作流 (实体类方式)
     *
     * @param workflow 工作流对象
     * @return 保存结果
     */
    @PostMapping("/create")
    public HttpResult<Workflow> createWorkflow(@RequestBody WorkflowRequest workflow) {
        Workflow savedWorkflow = workflowService.createWorkflow(workflow);
        return HttpResult.success(savedWorkflow);
    }

    /**
     * 更新工作流 (实体类方式)
     *
     * @param workflow 工作流对象
     * @return 保存结果
     */
    @PostMapping("/save")
    public HttpResult<Workflow> updateWorkflow(@RequestBody WorkflowRequest workflow) {
        Workflow savedWorkflow = workflowService.updateWorkflow(workflow);
        return HttpResult.success(savedWorkflow);
    }

    /**
     * 获取工作流列表（分页）
     *
     * @param queryRequest 查询请求参数
     * @return 分页的工作流列表
     */
    @GetMapping("/page")
    public HttpResult<PageResult<WorkflowDTO>> getWorkflowPage(WorkflowQueryRequest queryRequest) {
        return HttpResult.success(workflowService.getWorkflowPage(queryRequest));
    }
    /**
     * 获取工作流列表（分页）
     *
     * @param queryRequest 查询请求参数
     * @return 分页的工作流列表
     */
    @GetMapping("/getWorkflowInfo")
    public HttpResult<Workflow> getWorkflowInfo(WorkflowQueryRequest queryRequest) {
        return HttpResult.success(workflowService.getWorkflowInfo(queryRequest));
    }
}