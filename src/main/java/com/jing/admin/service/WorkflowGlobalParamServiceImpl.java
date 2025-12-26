package com.jing.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jing.admin.config.LoginUserUtil;
import com.jing.admin.core.PageResult;
import com.jing.admin.mapper.WorkflowGlobalParamMapper;
import com.jing.admin.model.domain.WorkflowGlobalParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作流全局参数服务实现类
 * @author 
 * @date 
 */
@Service
public class WorkflowGlobalParamServiceImpl extends ServiceImpl<WorkflowGlobalParamMapper, WorkflowGlobalParam> 
        implements WorkflowGlobalParamService {
    
    @Override
    public PageResult<WorkflowGlobalParam> getPage(String workflowId, String paramType, String valueType, String paramKey, int pageNum, int pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<WorkflowGlobalParam> queryWrapper = new LambdaQueryWrapper<>();
        
        // 工作流ID过滤（可选）
        if (workflowId != null) {
            queryWrapper.eq(WorkflowGlobalParam::getWorkflowId, workflowId);
        }
        
        // 参数用途类型过滤（可选）
        if (paramType != null && !paramType.trim().isEmpty()) {
            queryWrapper.eq(WorkflowGlobalParam::getParamType, paramType);
        }
        
        // 值的数据类型过滤（可选）
        if (valueType != null && !valueType.trim().isEmpty()) {
            queryWrapper.eq(WorkflowGlobalParam::getValueType, valueType);
        }
        
        // 参数键模糊查询
        if (paramKey != null && !paramKey.trim().isEmpty()) {
            queryWrapper.like(WorkflowGlobalParam::getParamKey, paramKey);
        }
        
        // 排序
        queryWrapper.orderByDesc(WorkflowGlobalParam::getCreateTime);
        
        // 执行分页查询
        IPage<WorkflowGlobalParam> page = new Page<>(pageNum, pageSize);
        IPage<WorkflowGlobalParam> result = this.page(page, queryWrapper);
        
        return new PageResult<>(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }
    
    @Override
    public String getParamValue(String paramKey, String workflowId) {
        LambdaQueryWrapper<WorkflowGlobalParam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WorkflowGlobalParam::getParamKey, paramKey);
        // 工作流ID可以为null，表示全局参数
        if (workflowId != null) {
            queryWrapper.eq(WorkflowGlobalParam::getWorkflowId, workflowId);
        } else {
            queryWrapper.isNull(WorkflowGlobalParam::getWorkflowId);
        }
        
        WorkflowGlobalParam param = this.getOne(queryWrapper);
        return param != null ? param.getParamValue() : null;
    }
    
    @Override
    public void batchSaveOrUpdate(List<WorkflowGlobalParam> params) {
        String userId = LoginUserUtil.getCurrentUserId();
        
        for (WorkflowGlobalParam param : params) {
            // 检查是否已存在
            LambdaQueryWrapper<WorkflowGlobalParam> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(WorkflowGlobalParam::getParamKey, param.getParamKey());
            // 工作流ID可能为null，需要特别处理
            if (param.getWorkflowId() != null) {
                queryWrapper.eq(WorkflowGlobalParam::getWorkflowId, param.getWorkflowId());
            } else {
                queryWrapper.isNull(WorkflowGlobalParam::getWorkflowId);
            }
            
            WorkflowGlobalParam existing = this.getOne(queryWrapper);
            
            if (existing != null) {
                // 更新现有参数
                param.setId(existing.getId());
                param.setUpdateTime(System.currentTimeMillis());
                param.setUpdateUserId(userId);
                this.updateById(param);
            } else {
                // 创建新参数
                param.setCreateTime(System.currentTimeMillis());
                param.setCreateUserId(userId);
                this.save(param);
            }
        }
    }
    
    @Override
    public void deleteByWorkflowId(String workflowId) {
        LambdaQueryWrapper<WorkflowGlobalParam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WorkflowGlobalParam::getWorkflowId, workflowId);
        
        this.remove(queryWrapper);
    }
}