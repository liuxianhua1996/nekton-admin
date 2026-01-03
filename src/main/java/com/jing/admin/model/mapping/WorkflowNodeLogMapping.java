package com.jing.admin.model.mapping;

import com.jing.admin.model.domain.WorkflowNodeLog;
import com.jing.admin.model.dto.WorkflowNodeLogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 工作流节点执行日志映射器
 */
@Mapper
public interface WorkflowNodeLogMapping {
    WorkflowNodeLogMapping INSTANCE = Mappers.getMapper(WorkflowNodeLogMapping.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "workflowInstanceId", target = "workflowInstanceId")
    @Mapping(source = "workflowId", target = "workflowId")
    @Mapping(source = "nodeId", target = "nodeId")
    @Mapping(source = "nodeName", target = "nodeName")
    @Mapping(source = "nodeType", target = "nodeType")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "inputData", target = "inputData")
    @Mapping(source = "outputData", target = "outputData")
    @Mapping(source = "errorMessage", target = "errorMessage")
    @Mapping(source = "executionTime", target = "executionTime")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    @Mapping(source = "sortOrder", target = "sortOrder")
    @Mapping(source = "tenantId", target = "tenantId")
    @Mapping(source = "createTime", target = "createTime")
    @Mapping(source = "updateTime", target = "updateTime")
    @Mapping(source = "createUserId", target = "createUserId")
    @Mapping(source = "updateUserId", target = "updateUserId")
    WorkflowNodeLog toEntity(WorkflowNodeLogDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "workflowInstanceId", target = "workflowInstanceId")
    @Mapping(source = "workflowId", target = "workflowId")
    @Mapping(source = "nodeId", target = "nodeId")
    @Mapping(source = "nodeName", target = "nodeName")
    @Mapping(source = "nodeType", target = "nodeType")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "inputData", target = "inputData")
    @Mapping(source = "outputData", target = "outputData")
    @Mapping(source = "errorMessage", target = "errorMessage")
    @Mapping(source = "executionTime", target = "executionTime")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "endTime", target = "endTime")
    @Mapping(source = "sortOrder", target = "sortOrder")
    @Mapping(source = "tenantId", target = "tenantId")
    @Mapping(source = "createTime", target = "createTime")
    @Mapping(source = "updateTime", target = "updateTime")
    @Mapping(source = "createUserId", target = "createUserId")
    @Mapping(source = "updateUserId", target = "updateUserId")
    WorkflowNodeLogDTO toDTO(WorkflowNodeLog entity);
}