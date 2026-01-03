package com.jing.admin.core.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author lxh
 * @date 2025/11/6
 **/
@Data
@Builder
public class NodeResult {
    private String nodeId;
    private String nodeName;
    private String nodeType;
    private int sort;
    private Object executeResult;
    private boolean success;
    private String errorMessage;

    private long startTime;
    private long endTime;
    private Map<String, Object> inputData;
}
