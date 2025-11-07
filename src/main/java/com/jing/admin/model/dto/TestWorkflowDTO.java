package com.jing.admin.model.dto;

import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lxh
 * @date 2025/11/7
 **/
@Data
public class TestWorkflowDTO {
    private List<NodeTestResult> nodeTestResults;
    private String status;

    @Data
    @Builder
    public static class  NodeTestResult {
        private String nodeId;
        private String nodeName;
        private int sort;
        private String status;
        private Object executeResult;

        private String errorMessage;

    }
}
