package com.jing.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.core.PageResult;
import com.jing.admin.model.api.ScheduleJobRequest;
import com.jing.admin.model.domain.ScheduleJob;
import com.jing.admin.mapper.ScheduleJobMapper;
import com.jing.admin.model.api.ScheduleJobQueryRequest;
import com.jing.admin.model.dto.ScheduleJobDTO;
import com.jing.admin.model.mapping.ScheduleJobMapping;
import com.jing.admin.repository.ScheduleJobRepository;
import com.jing.admin.service.ScheduleJobService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 调度工作流Service实现类
 */
@Service
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJob> implements ScheduleJobService {

    @Autowired
    private ScheduleJobRepository scheduleJobRepository;

    @Override
    public ScheduleJobDTO createScheduleJob(ScheduleJobRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("调度名称不能为空");
        }
        
        ScheduleJob scheduleJob = ScheduleJobMapping.INSTANCE.toEntity(request);
        scheduleJob.setCreateTime(System.currentTimeMillis());
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        scheduleJob.setCreateUserId(MDC.get("userId"));
        scheduleJob.setUpdateUserId(MDC.get("userId"));
        
        this.save(scheduleJob);
        
        return ScheduleJobMapping.INSTANCE.toDTO(scheduleJob);
    }

    @Override
    public ScheduleJobDTO updateScheduleJob(String id, ScheduleJobRequest request) {
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        ScheduleJob scheduleJob = this.getOne(queryWrapper);
        if (scheduleJob == null) {
            throw new RuntimeException("调度工作流不存在");
        }
        
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("调度名称不能为空");
        }
        
        scheduleJob = ScheduleJobMapping.INSTANCE.updateEntityFromRequest(request);
        scheduleJob.setId(id);
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        
        this.updateById(scheduleJob);
        
        return ScheduleJobMapping.INSTANCE.toDTO(scheduleJob);
    }

    @Override
    public Boolean deleteScheduleJob(String id) {
        return this.removeById(id);
    }

    @Override
    public ScheduleJobDTO getScheduleJobById(String id) {
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        ScheduleJob scheduleJob = this.getOne(queryWrapper);
        if (scheduleJob == null) {
            return null;
        }
        
        return ScheduleJobMapping.INSTANCE.toDTO(scheduleJob);
    }

    @Override
    public List<ScheduleJobDTO> getScheduleJobList() {
        List<ScheduleJob> scheduleJobs = this.list();
        return scheduleJobs.stream()
                .map(ScheduleJobMapping.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<ScheduleJobDTO> getScheduleJobPage(ScheduleJobQueryRequest queryRequest) {
        // 创建分页对象
        Page<ScheduleJobDTO> page = new Page<>(queryRequest.getCurrent(), queryRequest.getSize());

        // 使用Repository的自定义分页查询方法（关联用户表）
        IPage<ScheduleJobDTO> scheduleJobPage = scheduleJobRepository.selectScheduleJobPageWithUser(page, queryRequest);

        // 直接使用DTO列表
        List<ScheduleJobDTO> records = scheduleJobPage.getRecords();

        // 构建分页结果
        return PageResult.of(
                records,
                scheduleJobPage.getTotal(),
                scheduleJobPage.getCurrent(),
                scheduleJobPage.getSize()
        );
    }

    @Override
    public Boolean disableJob(String id) {
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        ScheduleJob scheduleJob = this.getOne(queryWrapper);
        if (scheduleJob == null) {
            return false;
        }
        
        scheduleJob.setStatus("DISABLED");
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        return this.updateById(scheduleJob);
    }

    @Override
    public Boolean enableJob(String id) {
        QueryWrapper<ScheduleJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UUID.fromString(id));
        ScheduleJob scheduleJob = this.getOne(queryWrapper);
        if (scheduleJob == null) {
            return false;
        }
        scheduleJob.setStatus("ENABLED");
        scheduleJob.setUpdateTime(System.currentTimeMillis());
        return this.updateById(scheduleJob);
    }

    @Override
    public Boolean executeJob(String id) {
        // 这里可以实现立即执行调度工作流的逻辑
        // 暂时返回true，实际实现需要根据具体工作流来执行
        return true;
    }
}