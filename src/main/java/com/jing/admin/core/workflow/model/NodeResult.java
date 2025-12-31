package com.jing.admin.core.workflow.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author lxh
 * @date 2025/11/6
 **/
@Data
@Builder
public class NodeResult {
    private String nodeId;
    private String nodeName;
    private int sort;
    private Object executeResult;
    private boolean success;
    private String errorMessage;
}
