package com.jing.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jing.admin.core.tenant.TenantContextHolder;
import com.jing.admin.core.workflow.WorkflowExecutionCallback;
import com.jing.admin.core.workflow.WorkflowExecutor;
import com.jing.admin.core.workflow.core.engine.WorkflowExecutionResult;
import com.jing.admin.core.workflow.model.GlobalParams;
import com.jing.admin.core.workflow.model.NodeResult;
import com.jing.admin.model.dto.WorkflowExecution;
import com.jing.admin.model.domain.ScheduleJobLog;
import com.jing.admin.model.domain.Workflow;
import com.jing.admin.model.domain.WorkflowGlobalParam;
import com.jing.admin.model.domain.WorkflowNodeLog;
import com.jing.admin.repository.WorkflowRepository;
import com.jing.admin.service.ScheduleJobLogService;
import com.jing.admin.service.WorkflowExecutionService;
import com.jing.admin.service.WorkflowGlobalParamService;
import com.jing.admin.service.WorkflowNodeLogService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class WorkflowExecutionServiceImpl implements WorkflowExecutionService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WorkflowExecutionServiceImpl.class);

    @Autowired
    private WorkflowExecutor workflowExecutor;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowGlobalParamService workflowGlobalParamService;

    @Autowired
    private ScheduleJobLogService scheduleJobLogService;

    @Autowired
    private WorkflowNodeLogService workflowNodeLogService;

    @Override
    public WorkflowExecutionResult executeWorkflowWithLog(WorkflowExecution workflowExecution) {
        String workflowId = workflowExecution.getWorkflowId();
        String jobId = workflowExecution.getJobId();
        Map<String, Object> startParams = workflowExecution.getStartParams();
        String workflowInstanceId = workflowExecution.getWorkflowInstanceId();
        String triggerType = workflowExecution.getTriggerType();
        Map<String, Object> extraLogInfo = workflowExecution.getExtraLogInfo();

        // 获取当前租户ID
        String currentTenantId = TenantContextHolder.getTenantId();

        // 记录租户信息用于调试
        log.info("执行工作流 {}，租户ID: {}", workflowId, currentTenantId);
        if (currentTenantId == null) {
            log.warn("在WorkflowExecutionServiceImpl.executeWorkflowWithLog中租户ID为null，线程: {}", Thread.currentThread().getName());
        }

        // 如果没有提供工作流实例ID，则生成一个新的
        if (workflowInstanceId == null || workflowInstanceId.trim().isEmpty()) {
            workflowInstanceId = UUID.randomUUID().toString();
        }

        // 记录开始执行时间
        long startTime = System.currentTimeMillis();

        // 创建执行日志记录
        ScheduleJobLog scheduleJobLoglog = new ScheduleJobLog();
        scheduleJobLoglog.setJobId(jobId); // 在调度场景下，jobId是调度任务ID；在其他场景下，可以是工作流ID
        scheduleJobLoglog.setWorkflowId(workflowId);
        scheduleJobLoglog.setWorkflowInstanceId(workflowInstanceId);
        scheduleJobLoglog.setTriggerType(triggerType != null ? triggerType : "MANUAL");
        scheduleJobLoglog.setStartTime(startTime);
        scheduleJobLoglog.setStatus("RUNNING");

        // 设置额外的日志信息（如果有的话）
        if (extraLogInfo != null && !extraLogInfo.isEmpty()) {
            // 可以将额外信息序列化为JSON存储到result或errorMessage字段中，或者添加到扩展字段
            scheduleJobLoglog.setResult("Extra info: " + extraLogInfo.toString());
        }

        // 保存初始日志记录
        scheduleJobLogService.save(scheduleJobLoglog);

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
                    new WorkflowExecutionCallback() {
                        @Override
                        public void onExecutionComplete(WorkflowExecutionResult result) {
                            // 在执行完成后，更新日志记录
                            try {
                                updateScheduleJobLog(scheduleJobLoglog, result, startTime);
                            } catch (Exception e) {
                                log.error("保存执行日志失败： ", e.getMessage());
                            }

                        }

                        @Override
                        public void onExecutionProgress(NodeResult nodeResult, ExecutionStatus status) {
                            // 记录节点执行进度日志
                            try {
                                String nodeStatus = status.name();
                                String nodeLogMessage = String.format(
                                        "节点[%s-%s]状态: %s, 执行结果: %s, 成功: %s",
                                        nodeResult.getNodeId(),
                                        nodeResult.getNodeName(),
                                        nodeStatus,
                                        nodeResult.getExecuteResult() != null ? nodeResult.getExecuteResult().toString() : "无结果",
                                        nodeResult.isSuccess()
                                );

                                if (status == com.jing.admin.core.workflow.WorkflowExecutionCallback.ExecutionStatus.ERROR && nodeResult.getErrorMessage() != null) {
                                    nodeLogMessage += ", 错误信息: " + nodeResult.getErrorMessage();
                                }

                                // 更新日志记录中的节点执行信息
                                updateNodeExecutionLog(scheduleJobLoglog, nodeLogMessage, nodeResult, status);
                            } catch (Exception e) {
                                log.error("保存节点日志失败： ", e.getMessage());
                            }

                        }
                    }
            );

            return workflowExecutionResult;
        } catch (Exception e) {
            // 执行异常时更新日志记录
            updateScheduleJobLogOnError(scheduleJobLoglog, e, startTime);
            // 重新抛出异常
            throw new RuntimeException("执行工作流失败: " + e.getMessage(), e);
        }
    }

    @Override
    public WorkflowExecutionResult executeWorkflowWithoutLog(WorkflowExecution request) {
        String workflowId = request.getWorkflowId();
        Map<String, Object> startParams = request.getStartParams();
        String workflowInstanceId = request.getWorkflowInstanceId();

        // 获取当前租户ID
        String currentTenantId = TenantContextHolder.getTenantId();

        // 记录租户信息用于调试
        log.debug("执行工作流(无日志) {}，租户ID: {}", workflowId, currentTenantId);

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
    public WorkflowExecutionResult executeWorkflowWithoutLogByData(String workflowJson, Map<String, GlobalParams> globalParams, Map<String, Object> startParams) {
        // 获取当前租户ID
        String currentTenantId = TenantContextHolder.getTenantId();

        // 记录租户信息用于调试
        log.debug("执行工作流(无日志) by data，租户ID: {}", currentTenantId);

        try {
            // 直接执行工作流，不记录日志
            return workflowExecutor.executeFromJsonByWorkflowData(
                    workflowJson,
                    globalParams,
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
     * 更新节点执行日志
     */
    private void updateNodeExecutionLog(ScheduleJobLog log, String nodeLogMessage, NodeResult nodeResult, WorkflowExecutionCallback.ExecutionStatus status) {
        // 首先尝试查找已存在的节点日志记录
        WorkflowNodeLog existingNodeLog = workflowNodeLogService.getNodeLogByInstanceIdAndNodeId(log.getId(), nodeResult.getNodeId());

        WorkflowNodeLog nodeLog;
        boolean isUpdate;

        if (existingNodeLog != null) {
            // 如果已存在记录，则更新现有记录
            nodeLog = existingNodeLog;
            isUpdate = true;
        } else {
            // 如果不存在记录，则创建新记录
            nodeLog = new WorkflowNodeLog();
            nodeLog.setWorkflowInstanceId(log.getId());
            nodeLog.setWorkflowId(log.getWorkflowId());
            nodeLog.setNodeId(nodeResult.getNodeId());
            nodeLog.setNodeName(nodeResult.getNodeName());
            nodeLog.setSortOrder(nodeResult.getSort());
            nodeLog.setNodeType(nodeResult.getNodeType());
            nodeLog.setInputData(JSON.toJSONString(nodeResult.getExecuteResult()));
            nodeLog.setStartTime(nodeResult.getStartTime());
            isUpdate = false;
        }

        long currentTime = System.currentTimeMillis();

        switch (status) {
            case BEFORE_EXECUTION:
                // 节点执行前：记录开始执行
                nodeLog.setStatus("RUNNING");
                nodeLog.setStartTime(currentTime);
                nodeLog.setInputData(nodeResult.getExecuteResult() != null ?
                        nodeResult.getExecuteResult().toString() : null);
                break;

            case AFTER_EXECUTION:
                // 节点执行后：记录执行结果
                nodeLog.setStatus(nodeResult.isSuccess() ? "SUCCESS" : "FAILED");
                nodeLog.setEndTime(currentTime);
                nodeLog.setOutputData(nodeResult.getExecuteResult() != null ?
                        nodeResult.getExecuteResult().toString() : null);

                // 计算执行时间
                if (nodeLog.getStartTime() != null) {
                    nodeLog.setExecutionTime(currentTime - nodeLog.getStartTime());
                }

                // 如果执行失败，记录错误信息
                if (!nodeResult.isSuccess() && nodeResult.getErrorMessage() != null) {
                    nodeLog.setErrorMessage(nodeResult.getErrorMessage());
                }
                break;

            case ERROR:
                // 节点执行错误：记录错误信息
                nodeLog.setStatus("FAILED");
                nodeLog.setEndTime(currentTime);

                // 计算执行时间（如果已记录开始时间）
                if (nodeLog.getStartTime() != null) {
                    nodeLog.setExecutionTime(currentTime - nodeLog.getStartTime());
                }

                // 设置错误信息
                if (nodeResult.getErrorMessage() != null) {
                    nodeLog.setErrorMessage(nodeResult.getErrorMessage());
                } else {
                    nodeLog.setErrorMessage(nodeLogMessage);
                }
                break;
        }

        // 保存或更新节点日志
        if (isUpdate) {
            nodeLog.setEndTime(nodeResult.getEndTime());
            nodeLog.setInputData(JSON.toJSONString(nodeResult.getInputData()));
            nodeLog.setOutputData(JSON.toJSONString(nodeResult.getExecuteResult()));
            workflowNodeLogService.updateNodeLog(nodeLog);
        } else {
            workflowNodeLogService.saveNodeLog(nodeLog);
        }
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