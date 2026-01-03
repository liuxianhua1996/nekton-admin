package com.jing.admin.core.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jing.admin.core.tenant.TenantContextHolder;
import com.jing.admin.core.workflow.core.context.WorkflowContext;
import com.jing.admin.core.workflow.core.conversion.WorkflowJsonConverter;
import com.jing.admin.core.workflow.core.engine.WorkflowEngine;
import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import com.jing.admin.core.workflow.model.GlobalParams;
import com.jing.admin.core.workflow.model.WorkflowDefinition;

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


    private final ObjectMapper objectMapper = new ObjectMapper();
    


    /**
     * 从JSON字符串执行工作流
     *
     * @param workflowJson 工作流JSON数据
     * @param globalParams 全局参数
     * @param startParams  启动参数
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJsonByWorkflowData(@NonNull String workflowJson, Map<String, GlobalParams> globalParams, Map startParams) {
        try {
            // 直接执行工作流（租户上下文已由TaskEngine传播）
            return executeFromJson(workflowJson, globalParams, startParams, null);
        } catch (Exception e) {
            // 如果在Callable包装中发生异常，重新抛出
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 从JSON字符串执行工作流（支持指定工作流实例ID）
     *
     * @param workflowJson       工作流JSON数据
     * @param globalParams       全局参数
     * @param startParams        启动参数
     * @param workflowInstanceId 工作流实例ID，如果为null则自动生成
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJsonByWorkflowData(@NonNull String workflowJson, Map<String, GlobalParams> globalParams, Map startParams, String workflowInstanceId) {
        return executeFromJson(workflowJson, globalParams, startParams, workflowInstanceId);
    }

    /**
     * 从JSON字符串执行工作流（支持回调）
     *
     * @param workflowJson 工作流JSON数据
     * @param globalParams 全局参数
     * @param startParams  启动参数
     * @param callback     执行回调接口
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJsonByWorkflowData(@NonNull String workflowJson, Map<String, GlobalParams> globalParams, Map startParams, WorkflowExecutionCallback callback) {
        return executeFromJsonByWorkflowData(workflowJson, globalParams, startParams, null, callback);
    }

    /**
     * 从JSON字符串执行工作流（支持指定工作流实例ID和回调）
     *
     * @param workflowJson       工作流JSON数据
     * @param globalParams       全局参数
     * @param startParams        启动参数
     * @param workflowInstanceId 工作流实例ID，如果为null则自动生成
     * @param callback           执行回调接口
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJsonByWorkflowData(@NonNull String workflowJson, Map<String, GlobalParams> globalParams, Map startParams, String workflowInstanceId, WorkflowExecutionCallback callback) {
        return executeFromJson(workflowJson, globalParams, startParams, workflowInstanceId, callback);
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
     * 从JSON字符串执行工作流（支持回调）
     *
     * @param workflowJson 工作流JSON字符串
     * @param callback     执行回调接口
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJson(@NonNull String workflowJson, WorkflowExecutionCallback callback) {
        return this.executeFromJson(workflowJson, new HashMap<>(), new HashMap(), null, callback);
    }

    /**
     * 从JSON字符串执行工作流（支持回调和指定工作流实例ID）
     *
     * @param workflowJson       工作流JSON字符串
     * @param callback           执行回调接口
     * @param workflowInstanceId 工作流实例ID，如果为null则自动生成
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJson(@NonNull String workflowJson, WorkflowExecutionCallback callback, String workflowInstanceId) {
        return this.executeFromJson(workflowJson, new HashMap<>(), new HashMap(), workflowInstanceId, callback);
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

            // 直接执行工作流（租户上下文已由TaskEngine传播）
            return workflowEngine.execute(workflowDefinition, context);
        } catch (Exception e) {
            WorkflowContext context = new WorkflowContext();
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setErrorMessage("工作流执行失败: " + e.getMessage());
            return new WorkflowExecutionResult(false, context, "工作流执行失败: " + e.getMessage());
        }
    }

    /**
     * 从JSON字符串执行工作流（支持指定工作流实例ID）
     *
     * @param workflowJson       工作流JSON字符串
     * @param globalParams       全局参数列表
     * @param startParams        启动参数
     * @param workflowInstanceId 工作流实例ID，如果为null则自动生成
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJson(@NonNull String workflowJson, @NonNull Map<String, GlobalParams> globalParams, Map startParams, String workflowInstanceId) {
        try {
            /** (重要) 转换JSON为工作流定义 **/
            WorkflowDefinition workflowDefinition = WorkflowJsonConverter.convertFromJson(workflowJson);
            workflowDefinition.setGlobalParams(globalParams);
            workflowDefinition.setStartParams(startParams);

            // 创建工作流执行上下文
            WorkflowContext context = new WorkflowContext();
            context.setGlobalParams(globalParams);

            // 设置工作流实例ID（如果提供了的话）
            if (workflowInstanceId != null && !workflowInstanceId.trim().isEmpty()) {
                context.setInstanceId(workflowInstanceId);
            }

            // 直接执行工作流（租户上下文已由TaskEngine传播）
            return workflowEngine.execute(workflowDefinition, context);
        } catch (Exception e) {
            WorkflowContext context = new WorkflowContext();
            if (workflowInstanceId != null) {
                context.setInstanceId(workflowInstanceId);
            }
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setErrorMessage("工作流JSON解析失败: " + e.getMessage());
            return new WorkflowExecutionResult(false, context, "工作流JSON解析失败: " + e.getMessage());
        }
    }

    /**
     * 从JSON字符串执行工作流（支持指定工作流实例ID和回调）
     *
     * @param workflowJson       工作流JSON字符串
     * @param globalParams       全局参数列表
     * @param startParams        启动参数
     * @param workflowInstanceId 工作流实例ID，如果为null则自动生成
     * @param callback           执行回调接口
     * @return 执行结果
     */
    public WorkflowExecutionResult executeFromJson(@NonNull String workflowJson, @NonNull Map<String, GlobalParams> globalParams, Map startParams, String workflowInstanceId, WorkflowExecutionCallback callback) {
        try {
            // 转换JSON为工作流定义
            WorkflowDefinition workflowDefinition = WorkflowJsonConverter.convertFromJson(workflowJson);
            workflowDefinition.setGlobalParams(globalParams);
            workflowDefinition.setStartParams(startParams);

            // 创建工作流执行上下文
            WorkflowContext context = new WorkflowContext();
            context.setGlobalParams(globalParams);

            // 设置工作流实例ID（如果提供了的话）
            if (workflowInstanceId != null && !workflowInstanceId.trim().isEmpty()) {
                context.setInstanceId(workflowInstanceId);
            }

            // 直接执行工作流（租户上下文已由TaskEngine传播）
            WorkflowExecutionResult result = workflowEngine.execute(workflowDefinition, context, callback);

            // 调用回调方法
            if (callback != null) {
                callback.onExecutionComplete(result);
            }

            return result;
        } catch (Exception e) {
            WorkflowContext context = new WorkflowContext();
            if (workflowInstanceId != null) {
                context.setInstanceId(workflowInstanceId);
            }
            context.setStatus(WorkflowContext.WorkflowStatus.FAILED);
            context.setErrorMessage("工作流JSON解析失败: " + e.getMessage());
            WorkflowExecutionResult result = new WorkflowExecutionResult(false, context, "工作流JSON解析失败: " + e.getMessage());

            // 调用回调方法
            if (callback != null) {
                callback.onExecutionComplete(result);
            }

            return result;
        }
    }
}