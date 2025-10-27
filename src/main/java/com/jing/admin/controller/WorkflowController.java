package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
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
 * @author lxh
 * @date 2025/9/19
 */
@RestController
@RequestMapping("/workflow")
public class WorkflowController {
    
    @Autowired
    private WorkflowService workflowService;
    
    /**
     * 保存或更新工作流 (实体类方式)
     * @param workflow 工作流对象
     * @return 保存结果
     */
    @PostMapping("/save")
    public HttpResult<Workflow> saveOrUpdateWorkflow(@RequestBody WorkflowRequest workflow) {
        try {
            Workflow savedWorkflow = workflowService.saveOrUpdateWorkflow(workflow);
            return HttpResult.success(savedWorkflow);
        } catch (Exception e) {
            return HttpResult.fail("保存失败: " + e.getMessage());
        }
    }
}