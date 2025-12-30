package com.jing.admin.controller;

import com.jing.admin.core.dto.ScheduleJobRequest;
import com.jing.admin.core.dto.ScheduleJobResponse;
import com.jing.admin.service.ScheduleJobService;
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
    public ScheduleJobResponse createScheduleJob(@RequestBody ScheduleJobRequest request) {
        return scheduleJobService.createScheduleJob(request);
    }

    /**
     * 更新调度工作流
     */
    @PutMapping("/{id}")
    public ScheduleJobResponse updateScheduleJob(@PathVariable String id, @RequestBody ScheduleJobRequest request) {
        return scheduleJobService.updateScheduleJob(id, request);
    }

    /**
     * 删除调度工作流
     */
    @DeleteMapping("/{id}")
    public Boolean deleteScheduleJob(@PathVariable String id) {
        return scheduleJobService.deleteScheduleJob(id);
    }

    /**
     * 根据ID获取调度工作流
     */
    @GetMapping("/{id}")
    public ScheduleJobResponse getScheduleJobById(@PathVariable String id) {
        return scheduleJobService.getScheduleJobById(id);
    }

    /**
     * 获取调度工作流列表
     */
    @GetMapping
    public List<ScheduleJobResponse> getScheduleJobList() {
        return scheduleJobService.getScheduleJobList();
    }

    /**
     * 停用调度工作流
     */
    @PutMapping("/{id}/disable")
    public Boolean disableJob(@PathVariable String id) {
        return scheduleJobService.disableJob(id);
    }

    /**
     * 启用调度工作流
     */
    @PutMapping("/{id}/enable")
    public Boolean enableJob(@PathVariable String id) {
        return scheduleJobService.enableJob(id);
    }

    /**
     * 立即执行调度工作流
     */
    @PutMapping("/{id}/execute")
    public Boolean executeJob(@PathVariable String id) {
        return scheduleJobService.executeJob(id);
    }
}