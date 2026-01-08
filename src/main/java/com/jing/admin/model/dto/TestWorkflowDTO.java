package com.jing.admin.model.dto;

import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author lxh
 * @date 2025/11/7
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestWorkflowDTO {
    private List<NodeTestResult> nodeTestResults;
    private String status;

    @Data
    @Builder
    public static class  NodeTestResult {
        private String nodeId;
        private String nodeName;
        private long startTime;
        private long endTime;

        private int sort;
        private String status;
        private Object executeResult;

        private String errorMessage;

    }
}
