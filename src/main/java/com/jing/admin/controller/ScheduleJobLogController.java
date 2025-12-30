package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.ScheduleJobLogRequest;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import com.jing.admin.model.dto.ScheduleJobLogDTO;
import com.jing.admin.service.ScheduleJobLogService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度任务执行记录Controller
 */
@RestController
@RequestMapping("/schedule/job/log")
public class ScheduleJobLogController {

    @Autowired
    private ScheduleJobLogService scheduleJobLogService;

    /**
     * 创建调度任务执行记录
     */
    @PostMapping
    @Operation(summary = "创建调度任务执行记录", description = "创建调度任务执行记录")
    public HttpResult<ScheduleJobLogDTO> createScheduleJobLog(@RequestBody ScheduleJobLogRequest request) {
        try {
            ScheduleJobLogDTO response = scheduleJobLogService.createScheduleJobLog(request);
            return HttpResult.success(response);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }

    /**
     * 更新调度任务执行记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新调度任务执行记录", description = "根据ID更新调度任务执行记录")
    public HttpResult<ScheduleJobLogDTO> updateScheduleJobLog(@PathVariable String id, @RequestBody ScheduleJobLogRequest request) {
        try {
            ScheduleJobLogDTO response = scheduleJobLogService.updateScheduleJobLog(id, request);
            return HttpResult.success(response);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }

    /**
     * 删除调度任务执行记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除调度任务执行记录", description = "根据ID删除调度任务执行记录")
    public HttpResult<Boolean> deleteScheduleJobLog(@PathVariable String id) {
        try {
            Boolean result = scheduleJobLogService.deleteScheduleJobLog(id);
            return HttpResult.success(result);
        } catch (Exception e) {
            return HttpResult.fail(false, "500", e.getMessage());
        }
    }

    /**
     * 根据ID获取调度任务执行记录
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取调度任务执行记录详情", description = "根据ID获取调度任务执行记录详细信息")
    public HttpResult<ScheduleJobLogDTO> getScheduleJobLogById(@PathVariable String id) {
        try {
            ScheduleJobLogDTO response = scheduleJobLogService.getScheduleJobLogById(id);
            return HttpResult.success(response);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }

    /**
     * 获取调度任务执行记录列表
     */
    @GetMapping
    @Operation(summary = "获取调度任务执行记录列表", description = "获取所有调度任务执行记录列表")
    public HttpResult<List<ScheduleJobLogDTO>> getScheduleJobLogList() {
        try {
            List<ScheduleJobLogDTO> list = scheduleJobLogService.getScheduleJobLogList();
            return HttpResult.success(list);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }

    /**
     * 根据任务ID获取执行记录列表
     */
    @GetMapping("/job/{jobId}")
    @Operation(summary = "根据任务ID获取执行记录列表", description = "根据任务ID获取对应的执行记录列表")
    public HttpResult<List<ScheduleJobLogDTO>> getScheduleJobLogByJobId(@PathVariable String jobId) {
        try {
            List<ScheduleJobLogDTO> list = scheduleJobLogService.getScheduleJobLogByJobId(jobId);
            return HttpResult.success(list);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }
    
    /**
     * 获取调度任务执行记录分页列表
     */
    @GetMapping("/page")
    @Operation(summary = "获取调度任务执行记录分页列表", description = "获取调度任务执行记录分页列表，支持按条件查询")
    public HttpResult<PageResult<ScheduleJobLogDTO>> getScheduleJobLogPage(ScheduleJobQueryRequest queryRequest) {
        try {
            PageResult<ScheduleJobLogDTO> pageResult = scheduleJobLogService.getScheduleJobLogPage(queryRequest);
            return HttpResult.success(pageResult);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }
}