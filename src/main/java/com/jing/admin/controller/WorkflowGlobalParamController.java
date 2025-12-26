package com.jing.admin.controller;

import com.jing.admin.core.HttpResult;
import com.jing.admin.core.PageResult;
import com.jing.admin.model.domain.WorkflowGlobalParam;
import com.jing.admin.service.WorkflowGlobalParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流全局参数控制器
 * @author 
 * @date 
 */
@RestController
@RequestMapping("/workflow/global/param")
public class WorkflowGlobalParamController {
    
    @Autowired
    private WorkflowGlobalParamService workflowGlobalParamService;
    
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
    @GetMapping("/page")
    public HttpResult<PageResult<WorkflowGlobalParam>> getPage(
            @RequestParam(required = false) String workflowId,
            @RequestParam(required = false) String paramType,
            @RequestParam(required = false) String valueType,
            @RequestParam(required = false) String paramKey,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return HttpResult.success(workflowGlobalParamService.getPage(workflowId, paramType, valueType, paramKey, pageNum, pageSize));
    }
    
    /**
     * 根据ID获取工作流全局参数
     * @param id 参数ID
     * @return 参数信息
     */
    @GetMapping("/{id}")
    public HttpResult<WorkflowGlobalParam> getById(@PathVariable String id) {
        return HttpResult.success(workflowGlobalParamService.getById(id));
    }
    
    /**
     * 保存工作流全局参数
     * @param param 参数信息
     * @return 保存结果
     */
    @PostMapping
    public HttpResult<String> save(@RequestBody WorkflowGlobalParam param) {
        workflowGlobalParamService.save(param);
        return HttpResult.success("保存成功");
    }
    
    /**
     * 更新工作流全局参数
     * @param param 参数信息
     * @return 更新结果
     */
    @PutMapping
    public HttpResult<String> update(@RequestBody WorkflowGlobalParam param) {
        workflowGlobalParamService.updateById(param);
        return HttpResult.success("更新成功");
    }
    
    /**
     * 删除工作流全局参数
     * @param id 参数ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public HttpResult<String> delete(@PathVariable String id) {
        workflowGlobalParamService.removeById(id);
        return HttpResult.success("删除成功");
    }
    
    /**
     * 批量保存或更新参数
     * @param params 参数列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    public HttpResult<String> batchSaveOrUpdate(@RequestBody List<WorkflowGlobalParam> params) {
        workflowGlobalParamService.batchSaveOrUpdate(params);
        return HttpResult.success("批量操作成功");
    }
    
    /**
     * 根据参数键获取参数值
     * @param paramKey 参数键
     * @param workflowId 工作流ID，可为null表示全局参数
     * @return 参数值
     */
    @GetMapping("/value/{paramKey}")
    public HttpResult<String> getValue(@PathVariable String paramKey, 
                                       @RequestParam(required = false) String workflowId) {
        String value = workflowGlobalParamService.getParamValue(paramKey, workflowId);
        return HttpResult.success(value);
    }
    
    /**
     * 根据工作流ID删除所有参数
     * @param workflowId 工作流ID
     * @return 删除结果
     */
    @DeleteMapping("/workflow/{workflowId}")
    public HttpResult<String> deleteByWorkflowId(@PathVariable String workflowId) {
        workflowGlobalParamService.deleteByWorkflowId(workflowId);
        return HttpResult.success("删除成功");
    }
}