package com.jing.admin.controller;

import com.jing.admin.core.dto.ScheduleJobLogRequest;
import com.jing.admin.core.dto.ScheduleJobLogResponse;
import com.jing.admin.service.ScheduleJobLogService;
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
    public ScheduleJobLogResponse createScheduleJobLog(@RequestBody ScheduleJobLogRequest request) {
        return scheduleJobLogService.createScheduleJobLog(request);
    }

    /**
     * 更新调度任务执行记录
     */
    @PutMapping("/{id}")
    public ScheduleJobLogResponse updateScheduleJobLog(@PathVariable String id, @RequestBody ScheduleJobLogRequest request) {
        return scheduleJobLogService.updateScheduleJobLog(id, request);
    }

    /**
     * 删除调度任务执行记录
     */
    @DeleteMapping("/{id}")
    public Boolean deleteScheduleJobLog(@PathVariable String id) {
        return scheduleJobLogService.deleteScheduleJobLog(id);
    }

    /**
     * 根据ID获取调度任务执行记录
     */
    @GetMapping("/{id}")
    public ScheduleJobLogResponse getScheduleJobLogById(@PathVariable String id) {
        return scheduleJobLogService.getScheduleJobLogById(id);
    }

    /**
     * 获取调度任务执行记录列表
     */
    @GetMapping
    public List<ScheduleJobLogResponse> getScheduleJobLogList() {
        return scheduleJobLogService.getScheduleJobLogList();
    }

    /**
     * 根据任务ID获取执行记录列表
     */
    @GetMapping("/job/{jobId}")
    public List<ScheduleJobLogResponse> getScheduleJobLogByJobId(@PathVariable String jobId) {
        return scheduleJobLogService.getScheduleJobLogByJobId(jobId);
    }
}