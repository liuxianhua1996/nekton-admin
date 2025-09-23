package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
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
    public HttpResult<Workflow> saveOrUpdateWorkflow(@RequestBody Workflow workflow) {
        try {
            Workflow savedWorkflow = workflowService.saveOrUpdateWorkflow(workflow);
            return HttpResult.success(savedWorkflow);
        } catch (Exception e) {
            return HttpResult.fail("保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存或更新工作流 (DTO方式)
     * @param workflowDTO 工作流DTO对象
     * @return 保存结果
     */
    @PostMapping("/save/dto")
    public HttpResult<WorkflowDTO> saveOrUpdateWorkflowDTO(@RequestBody WorkflowDTO workflowDTO) {
        try {
            Workflow workflow = WorkflowMapping.toEntity(workflowDTO);
            Workflow savedWorkflow = workflowService.saveOrUpdateWorkflow(workflow);
            WorkflowDTO savedDTO = WorkflowMapping.toDTO(savedWorkflow);
            return HttpResult.success(savedDTO);
        } catch (Exception e) {
            return HttpResult.fail("保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取工作流
     * @param id 工作流ID
     * @return 工作流对象
     */
    @GetMapping("/get/{id}")
    public HttpResult<Workflow> getWorkflowById(@PathVariable String id) {
        try {
            Workflow workflow = workflowService.getWorkflowById(id);
            if (workflow != null) {
                return HttpResult.success(workflow);
            } else {
                return HttpResult.error("未找到指定工作流");
            }
        } catch (Exception e) {
            return HttpResult.fail("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有工作流 (实体类方式)
     * @return 工作流列表
     */
    @GetMapping("/list")
    public HttpResult<List<Workflow>> getAllWorkflows() {
        try {
            List<Workflow> workflows = workflowService.getAllWorkflows();
            return HttpResult.success(workflows);
        } catch (Exception e) {
            return HttpResult.fail("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有工作流 (DTO方式)
     * @return 工作流DTO列表
     */
    @GetMapping("/list/dto")
    public HttpResult<List<WorkflowDTO>> getAllWorkflowsDTO() {
        try {
            List<Workflow> workflows = workflowService.getAllWorkflows();
            List<WorkflowDTO> dtos = workflows.stream()
                    .map(WorkflowMapping::toDTO)
                    .collect(Collectors.toList());
            return HttpResult.success(dtos);
        } catch (Exception e) {
            return HttpResult.fail("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除工作流
     * @param id 工作流ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public HttpResult<Boolean> deleteWorkflow(@PathVariable String id) {
        try {
            boolean result = workflowService.deleteWorkflow(id);
            if (result) {
                return HttpResult.success(true);
            } else {
                return HttpResult.error("删除失败，未找到指定工作流");
            }
        } catch (Exception e) {
            return HttpResult.error("删除失败: " + e.getMessage());
        }
    }
}