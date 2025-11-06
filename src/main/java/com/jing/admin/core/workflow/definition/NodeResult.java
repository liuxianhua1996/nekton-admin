package com.jing.admin.core.workflow.definition;

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
    private Object executeResult;
}
