package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
import com.jing.admin.core.PageResult;
import com.jing.admin.core.dto.ScheduleJobRequest;
import com.jing.admin.core.dto.ScheduleJobResponse;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import com.jing.admin.service.ScheduleJobService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度工作流Controller
 */
@RestController
@RequestMapping("/api/schedule/job")
public class ScheduleJobController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    /**
     * 创建调度工作流
     */
    @PostMapping
    @Operation(summary = "创建调度任务", description = "创建新的调度任务")
    public HttpResult<ScheduleJobResponse> createScheduleJob(@RequestBody ScheduleJobRequest request) {
        try {
            ScheduleJobResponse response = scheduleJobService.createScheduleJob(request);
            return HttpResult.success(response);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }

    /**
     * 更新调度工作流
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新调度任务", description = "根据ID更新调度任务信息")
    public HttpResult<ScheduleJobResponse> updateScheduleJob(@PathVariable String id, @RequestBody ScheduleJobRequest request) {
        try {
            ScheduleJobResponse response = scheduleJobService.updateScheduleJob(id, request);
            return HttpResult.success(response);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }

    /**
     * 删除调度工作流
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除调度任务", description = "根据ID删除调度任务")
    public HttpResult<Boolean> deleteScheduleJob(@PathVariable String id) {
        try {
            Boolean result = scheduleJobService.deleteScheduleJob(id);
            return HttpResult.success(result);
        } catch (Exception e) {
            return HttpResult.fail(false, "500", e.getMessage());
        }
    }

    /**
     * 根据ID获取调度工作流
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取调度任务详情", description = "根据ID获取调度任务详细信息")
    public HttpResult<ScheduleJobResponse> getScheduleJobById(@PathVariable String id) {
        try {
            ScheduleJobResponse response = scheduleJobService.getScheduleJobById(id);
            return HttpResult.success(response);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }

    /**
     * 获取调度工作流列表（分页）
     */
    @GetMapping("/page")
    @Operation(summary = "获取调度任务分页列表", description = "获取调度任务分页列表，支持按条件查询")
    public HttpResult<PageResult<ScheduleJobResponse>> getScheduleJobPage(ScheduleJobQueryRequest queryRequest) {
        try {
            PageResult<ScheduleJobResponse> pageResult = scheduleJobService.getScheduleJobPage(queryRequest);
            return HttpResult.success(pageResult);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }

    /**
     * 获取调度工作流列表
     */
    @GetMapping
    @Operation(summary = "获取调度任务列表", description = "获取所有调度任务列表")
    public HttpResult<List<ScheduleJobResponse>> getScheduleJobList() {
        try {
            List<ScheduleJobResponse> list = scheduleJobService.getScheduleJobList();
            return HttpResult.success(list);
        } catch (Exception e) {
            return HttpResult.fail(null, "500", e.getMessage());
        }
    }

    /**
     * 停用调度工作流
     */
    @PutMapping("/{id}/disable")
    @Operation(summary = "停用调度任务", description = "根据ID停用调度任务")
    public HttpResult<Boolean> disableJob(@PathVariable String id) {
        try {
            Boolean result = scheduleJobService.disableJob(id);
            return HttpResult.success(result);
        } catch (Exception e) {
            return HttpResult.fail(false, "500", e.getMessage());
        }
    }

    /**
     * 启用调度工作流
     */
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用调度任务", description = "根据ID启用调度任务")
    public HttpResult<Boolean> enableJob(@PathVariable String id) {
        try {
            Boolean result = scheduleJobService.enableJob(id);
            return HttpResult.success(result);
        } catch (Exception e) {
            return HttpResult.fail(false, "500", e.getMessage());
        }
    }

    /**
     * 立即执行调度工作流
     */
    @PutMapping("/{id}/execute")
    @Operation(summary = "立即执行调度任务", description = "根据ID立即执行调度任务")
    public HttpResult<Boolean> executeJob(@PathVariable String id) {
        try {
            Boolean result = scheduleJobService.executeJob(id);
            return HttpResult.success(result);
        } catch (Exception e) {
            return HttpResult.fail(false, "500", e.getMessage());
        }
    }
}