package com.jing.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jing.admin.model.domain.WorkflowGlobalParam;
import com.jing.admin.core.PageResult;

import java.util.List;

/**
 * 工作流全局参数服务接口
 * @author 
 * @date 
 */
public interface WorkflowGlobalParamService extends IService<WorkflowGlobalParam> {
    
    /**
     * 分页查询工作流全局参数
     * @param workflowId 工作流ID(可选)
     * @param paramType 参数用途类型(可选)
     * @param valueType 值的数据类型(可选)
     * @param paramKey 参数键(可选)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<WorkflowGlobalParam> getPage(String workflowId, String paramType, String valueType, String paramKey, long pageNum, long pageSize);
    
    /**
     * 查询所有工作流全局参数
     * @param workflowId 工作流ID(可选)
     * @param paramType 参数用途类型(可选)
     * @param valueType 值的数据类型(可选)
     * @param paramKey 参数键(可选)
     * @return 参数列表
     */
    List<WorkflowGlobalParam> getAll(String workflowId, String paramType, String valueType, String paramKey);
    
    /**
     * 根据参数键和工作流ID获取参数值
     * @param paramKey 参数键
     * @param workflowId 工作流ID，可为null表示全局参数
     * @return 参数值
     */
    String getParamValue(String paramKey, String workflowId);
    
    /**
     * 批量保存或更新参数
     * @param params 参数列表
     */
    void batchSaveOrUpdate(List<WorkflowGlobalParam> params);
    
    /**
     * 根据工作流ID删除所有参数
     * @param workflowId 工作流ID
     */
    void deleteByWorkflowId(String workflowId);
}