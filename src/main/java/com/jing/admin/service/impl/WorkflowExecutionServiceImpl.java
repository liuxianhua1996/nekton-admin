package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jing.admin.core.workflow.WorkflowExecutor;
import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import com.jing.admin.core.workflow.model.GlobalParams;
import com.jing.admin.model.dto.WorkflowExecution;
import com.jing.admin.model.domain.ScheduleJobLog;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.domain.WorkflowGlobalParam;
import com.jing.admin.repository.WorkflowRepository;
import com.jing.admin.service.ScheduleJobLogService;
import com.jing.admin.service.WorkflowExecutionService;
import com.jing.admin.service.WorkflowGlobalParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 工作流执行服务实现类
 * 提供带日志和不带日志的工作流执行方法
 */
@Service
public class WorkflowExecutionServiceImpl implements WorkflowExecutionService {

    @Autowired
    private WorkflowExecutor workflowExecutor;
    
    @Autowired
    private WorkflowRepository workflowRepository;
    
    @Autowired
    private WorkflowGlobalParamService workflowGlobalParamService;
    
    @Autowired
    private ScheduleJobLogService scheduleJobLogService;
    
    @Override
    public WorkflowExecutionResult executeWorkflowWithLog(WorkflowExecution workflowExecution) {
        String workflowId = workflowExecution.getWorkflowId();
        String jobId = workflowExecution.getJobId();
        Map<String, Object> startParams = workflowExecution.getStartParams();
        String workflowInstanceId = workflowExecution.getWorkflowInstanceId();
        String triggerType = workflowExecution.getTriggerType();
        Map<String, Object> extraLogInfo = workflowExecution.getExtraLogInfo();
        
        // 如果没有提供工作流实例ID，则生成一个新的
        if (workflowInstanceId == null || workflowInstanceId.trim().isEmpty()) {
            workflowInstanceId = UUID.randomUUID().toString();
        }
        
        // 记录开始执行时间
        long startTime = System.currentTimeMillis();
        
        // 创建执行日志记录
        ScheduleJobLog log = new ScheduleJobLog();
        log.setJobId(jobId); // 在调度场景下，jobId是调度任务ID；在其他场景下，可以是工作流ID
        log.setWorkflowId(workflowId);
        log.setWorkflowInstanceId(workflowInstanceId);
        log.setTriggerType(triggerType != null ? triggerType : "MANUAL");
        log.setStartTime(startTime);
        log.setStatus("RUNNING");
        
        // 设置额外的日志信息（如果有的话）
        if (extraLogInfo != null && !extraLogInfo.isEmpty()) {
            // 可以将额外信息序列化为JSON存储到result或errorMessage字段中，或者添加到扩展字段
            log.setResult("Extra info: " + extraLogInfo.toString());
        }
        
        // 保存初始日志记录
        scheduleJobLogService.save(log);
        
        try {
            // 获取工作流和全局参数
            Workflow workflow = workflowRepository.getById(workflowId);
            List<WorkflowGlobalParam> globalParams = workflowGlobalParamService.getAll(workflowId, null, null, null);
            
            // 处理全局参数
            Map<String, GlobalParams> globalParamsMap = handleGlobal(globalParams);
            
            // 使用回调方式执行工作流，实现解耦
            WorkflowExecutionResult workflowExecutionResult = workflowExecutor.executeFromJsonByWorkflowData(
                workflow.getJsonData(),
                globalParamsMap,
                startParams != null ? startParams : new HashMap<>(),
                workflowInstanceId,
                new com.jing.admin.core.workflow.WorkflowExecutionCallback() {
                    @Override
                    public void onExecutionComplete(WorkflowExecutionResult result) {
                        // 在执行完成后，更新日志记录
                        updateScheduleJobLog(log, result, startTime);
                    }

                    @Override
                    public void onExecutionProgress(com.jing.admin.core.workflow.model.NodeResult nodeResult, com.jing.admin.core.workflow.WorkflowExecutionCallback.ExecutionStatus status) {
                        // 在执行过程中可以处理节点状态变化
                        // 目前暂时不处理，但可以扩展
                    }
                }
            );
            
            return workflowExecutionResult;
        } catch (Exception e) {
            // 执行异常时更新日志记录
            updateScheduleJobLogOnError(log, e, startTime);
            
            // 重新抛出异常
            throw new RuntimeException("执行工作流失败: " + e.getMessage(), e);
        }
    }

    @Override
    public WorkflowExecutionResult executeWorkflowWithoutLog(WorkflowExecution request) {
        String workflowId = request.getWorkflowId();
        Map<String, Object> startParams = request.getStartParams();
        String workflowInstanceId = request.getWorkflowInstanceId();
        
        try {
            // 获取工作流和全局参数
            Workflow workflow = workflowRepository.getById(workflowId);
            List<WorkflowGlobalParam> globalParams = workflowGlobalParamService.getAll(workflowId, null, null, null);
            
            // 处理全局参数
            Map<String, GlobalParams> globalParamsMap = handleGlobal(globalParams);
            
            // 直接执行工作流，不记录日志
            return workflowExecutor.executeFromJsonByWorkflowData(
                workflow.getJsonData(),
                globalParamsMap,
                startParams != null ? startParams : new HashMap<>(),
                workflowInstanceId,
                null // 不使用回调，因为不需要记录日志
            );
        } catch (Exception e) {
            throw new RuntimeException("执行工作流失败: " + e.getMessage(), e);
        }
    }
    @Override
    public WorkflowExecutionResult executeWorkflowWithoutLogByData(String workflowJson, Map<String, Object> globalParams, Map<String, Object> startParams) {
        try {
            // 直接执行工作流，不记录日志
            return workflowExecutor.executeFromJsonByWorkflowData(
                workflowJson,
                convertGlobalParams(globalParams),
                startParams != null ? startParams : new HashMap<>(),
                null, // 不指定工作流实例ID
                null // 不使用回调，因为不需要记录日志
            );
        } catch (Exception e) {
            throw new RuntimeException("执行工作流失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 处理全局参数
     */
    private Map<String, GlobalParams> handleGlobal(List<WorkflowGlobalParam> globalParams) {
        Map<String, GlobalParams> globalParamsMap = new HashMap<>();
        if (globalParams != null) {
            globalParams.stream().forEach(workflowGlobalParam -> {
                globalParamsMap.put(workflowGlobalParam.getId(), GlobalParams.builder()
                                .apiKeyId(workflowGlobalParam.getId())
                                .paramValue(workflowGlobalParam.getParamValue())
                                .valueType(workflowGlobalParam.getValueType())
                                .paramType(workflowGlobalParam.getParamType())
                        .build());
            });
        }
        return globalParamsMap;
    }
    
    /**
     * 转换全局参数格式
     */
    private Map<String, GlobalParams> convertGlobalParams(Map<String, Object> globalParams) {
        Map<String, GlobalParams> globalParamsMap = new HashMap<>();
        if (globalParams != null) {
            globalParams.forEach((key, value) -> {
                globalParamsMap.put(key, GlobalParams.builder()
                                .apiKeyId(key)
                                .paramValue(String.valueOf(value))
                                .valueType(value != null ? value.getClass().getSimpleName() : "String")
                                .paramType("DYNAMIC")
                        .build());
            });
        }
        return globalParamsMap;
    }
    
    /**
     * 更新调度任务日志
     */
    private void updateScheduleJobLog(ScheduleJobLog log, WorkflowExecutionResult result, long startTime) {
        long endTime = System.currentTimeMillis();
        log.setEndTime(endTime);
        log.setExecutionTime(endTime - startTime);
        
        // 根据执行结果设置状态
        if (result.isSuccess()) {
            log.setStatus("SUCCESS");
            log.setResult("执行成功");
        } else {
            log.setStatus("FAILED");
            log.setResult("执行失败: " + result.getMessage());
            log.setErrorMessage(result.getMessage());
        }
        
        // 更新日志记录
        scheduleJobLogService.update(log, new QueryWrapper<ScheduleJobLog>().eq("id", UUID.fromString(log.getId())));
    }
    
    /**
     * 执行异常时更新调度任务日志
     */
    private void updateScheduleJobLogOnError(ScheduleJobLog log, Exception e, long startTime) {
        long endTime = System.currentTimeMillis();
        log.setEndTime(endTime);
        log.setExecutionTime(endTime - startTime);
        log.setStatus("FAILED");
        log.setErrorMessage("执行异常: " + e.getMessage());
        log.setResult("执行失败");
        
        // 更新日志记录
        scheduleJobLogService.update(log, new QueryWrapper<ScheduleJobLog>().eq("id", UUID.fromString(log.getId())));
    }
}