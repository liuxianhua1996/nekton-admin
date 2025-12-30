package com.jing.admin.core.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.core.conversion.WorkflowJsonConverter;
import com.jing.admin.core.workflow.core.engine.WorkflowEngine;
import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import com.jing.admin.core.workflow.model.GlobalParams;
import com.jing.admin.core.workflow.model.WorkflowDefinition;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.domain.WorkflowGlobalParam;
import com.jing.admin.repository.WorkflowRepository;
import com.jing.admin.service.WorkflowGlobalParamService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流执行器
 * 提供工作流执行的高级接口
 */
@Component
public class WorkflowExecutor {
    
    @Autowired
    private WorkflowEngine workflowEngine;
    
    @Autowired
    private WorkflowGlobalParamService workflowGlobalParamService;
    @Autowired
    private WorkflowRepository workflowRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, GlobalParams> handleGlobal(List<WorkflowGlobalParam> globalParams){
        Map<String, GlobalParams> globalParamsMap = new HashMap<>();
        globalParams.stream().forEach(workflowGlobalParam -> {
            globalParamsMap.put(workflowGlobalParam.getId(), GlobalParams.builder()
                            .apiKeyId(workflowGlobalParam.getId())
                            .paramValue(workflowGlobalParam.getParamValue())
                            .valueType(workflowGlobalParam.getValueType())
                            .paramType(workflowGlobalParam.getParamType())
                    .build());
        });
        return globalParamsMap;
    }
    
    /**
     * 从JSON字符串执行工作流
     *
     * @param workflowId 工作流ID，用于获取全局参数
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJsonByWorkflowId(@NonNull String workflowId, Map startParams) {
        // 获取工作流的全局参数
        Workflow workflow = workflowRepository.getById(workflowId);
        List<WorkflowGlobalParam> globalParams = workflowGlobalParamService.getAll(workflowId, null, null, null);
        Map<String, GlobalParams> globalParamsMap = this.handleGlobal(globalParams);
        return executeFromJson(workflow.getJsonData(), globalParamsMap,startParams);
    }
    /**
     * 从JSON字符串执行工作流
     *
     * @param workflowJson 工作流JSON字符串
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJson(@NonNull String workflowJson) {
        return this.executeFromJson(workflowJson, new HashMap<>(), new HashMap());
    }
    
    /**
     * 从JSON字符串执行工作流
     * 
     * @param workflowJson 工作流JSON字符串
     * @param globalParams 全局参数列表
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJson(@NonNull String workflowJson, @NonNull Map<String, GlobalParams> globalParams, Map startParams) {
        try {
            // 转换JSON为工作流定义
            WorkflowDefinition workflowDefinition = WorkflowJsonConverter.convertFromJson(workflowJson);
            workflowDefinition.setGlobalParams(globalParams);
            workflowDefinition.setStartParams(startParams);
            
            // 创建工作流执行上下文
            WorkflowContext context = new WorkflowContext();
            context.setGlobalParams(globalParams);
            // 执行工作流
            return workflowEngine.execute(workflowDefinition, context);
        } catch (IOException e) {
            WorkflowContext context = new WorkflowContext();
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setErrorMessage("工作流JSON解析失败: " + e.getMessage());
            
            return new WorkflowExecutionResult(false, context, "工作流JSON解析失败: " + e.getMessage());
        }
    }

    /**
     * 从JSON文件执行工作流
     * 
     * @param jsonFilePath JSON文件路径
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJsonFile(String jsonFilePath) {
        return executeFromJsonFile(jsonFilePath, null);
    }
    
    /**
     * 从JSON文件执行工作流
     * 
     * @param jsonFilePath JSON文件路径
     * @param workflowId 工作流ID，用于获取全局参数
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJsonFile(String jsonFilePath, String workflowId) {
        try {
            // 读取JSON文件
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath);
            if (inputStream == null) {
                throw new IOException("无法找到工作流JSON文件: " + jsonFilePath);
            }
            
            String workflowJson = objectMapper.readValue(inputStream, String.class);
            
            // 执行工作流
            return executeFromJson(workflowJson,new HashMap<>(), new HashMap());
        } catch (IOException e) {
            WorkflowContext context = new WorkflowContext();
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setErrorMessage("读取工作流JSON文件失败: " + e.getMessage());
            
            return new WorkflowExecutionResult(false, context, "读取工作流JSON文件失败: " + e.getMessage());
        }
    }
}