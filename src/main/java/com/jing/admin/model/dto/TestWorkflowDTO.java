package com.jing.admin.model.dto;

import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lxh
 * @date 2025/11/7
 **/
@Data
public class TestWorkflowDTO {
    private WorkflowExecutionResult runResult;
    private List<Map> runLogs;
}
