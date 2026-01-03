package com.jing.admin.controller;

import com.jing.admin.model.api.WorkflowNodeLogQueryRequest;
import com.jing.admin.model.dto.WorkflowNodeLogDTO;
import com.jing.admin.service.WorkflowNodeLogService;
import com.jing.admin.core.HttpResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流节点执行日志控制器
 */
@RestController
@RequestMapping("/workflow/node/log")
@Tag(name = "工作流节点执行日志", description = "工作流节点执行日志管理")
public class WorkflowNodeLogController {

    @Autowired
    private WorkflowNodeLogService workflowNodeLogService;

    /**
     * 根据日志ID获取节点执行日志
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据日志ID获取节点执行日志")
    public HttpResult<List<WorkflowNodeLogDTO>> getNodeLogById(@PathVariable String id) {
        List<WorkflowNodeLogDTO> nodeLogs = workflowNodeLogService.getNodeLogById(id);
        return HttpResult.success(nodeLogs);
    }

}